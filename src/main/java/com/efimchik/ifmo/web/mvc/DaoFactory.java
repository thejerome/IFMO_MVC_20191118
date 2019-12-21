package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class DaoFactory {
    private ResultSet generateSqlQuery(String sql) throws SQLException {
        Connection connection = ConnectionSource.createConnection();
        return connection.createStatement().executeQuery(sql);
    }

    private class Converter<T>{
        protected T Convert(ResultSet res) throws SQLException{
            return null;
        }
    }

    public class EmployeeConverter extends Converter<Employee> {
        boolean FindNextEmployee;
        boolean Recursive;

        EmployeeConverter(){
            FindNextEmployee = true;
            Recursive = false;
        }
        EmployeeConverter(boolean findNextEmployee){
            FindNextEmployee = findNextEmployee;
        }
        EmployeeConverter(boolean findNextEmployee, boolean recursive){
            FindNextEmployee = findNextEmployee;
            Recursive = recursive;
        }

        long getFromStrId(ResultSet res, String Data) {
            String data = null;
            try {
                data = res.getString(Data);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (data == null){
                return -1;
            }
            return Long.parseLong(data);
        }

        @Override
        protected Employee Convert(ResultSet res) throws SQLException {
            DaoFactory factory = new DaoFactory();

            Employee manager = null;
            Department department = null;

            if (FindNextEmployee){
                long nextEmpId = getFromStrId(res,"MANAGER");
                if (nextEmpId >= 0){
                    manager = factory.employeeDAO().getById(nextEmpId, new EmployeeConverter(Recursive, Recursive));
                }
            }

            long depId = getFromStrId(res,"DEPARTMENT");
            if (depId >= 0){
                department = factory.departmentDAO().getById(depId);
            }

            return  new Employee(
                    Long.parseLong(res.getString("id")),
                    new FullName(res.getString("firstname"), res.getString("lastname"), res.getString("middlename")),
                    Position.valueOf(res.getString("position")),
                    LocalDate.parse(res.getString("hiredate")),
                    new BigDecimal(res.getString("salary")),
                    manager,
                    department);
        }
    }

    private class DepartmentConverter extends Converter<Department> {

        @Override
        public Department Convert(ResultSet res) throws SQLException {
            return new Department(
                    Long.parseLong(res.getString("id")),
                    res.getString("name"),
                    res.getString("location")
            );
        }
    }

    private <T>List<T> GetResultList(String sql, Converter<T> converter){
        List<T> temp = new ArrayList<>();
        try {
            ResultSet res = generateSqlQuery(sql);
            while (res.next()){
                temp.add(converter.Convert(res));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temp;
    }

    EmployeeDao employeeDAO() {
        return new EmployeeDao() {
            private String BaseRequest(Integer page, Integer size, String sort, String optional){
                return "SELECT * FROM employee" +
                        optional +
                        ((sort != null) ? " ORDER BY " + sort.replace("hired","HIREDATE") : " ") +
                        ((size != null) ? " LIMIT " + size : " ") +
                        ((page != null && size != null) ? " OFFSET " + size * page : " ");
            }

            @Override
            public List<Employee> getByDepartmentId(String departmentId, Integer page, Integer size, String sort) {
                try{
                    return GetResultList( BaseRequest(page, size, sort," WHERE department = " + Integer.parseInt(departmentId)),new EmployeeConverter());
                }catch (Exception ex){
                    return getByDepartmentName(departmentId, page, size, sort);
                }
            }

            @Override
            public List<Employee> getByDepartmentName(String departmentName, Integer page, Integer size, String sort) {
                Department department = new DaoFactory().departmentDAO().getByName(departmentName);
                if (department == null){
                    return new LinkedList<>();
                }
                return GetResultList( BaseRequest(page, size, sort," WHERE department = " + department.getId().toString()),new EmployeeConverter());
            }

            @Override
            public List<Employee> getByManagerId(String id, Integer page, Integer size, String sort) {
                return GetResultList( BaseRequest(page, size, sort," WHERE manager = " + id),new EmployeeConverter());
            }

            @Override
            public Employee getById(String Id, boolean full_chain) {
                return getById(Long.parseLong(Id), new EmployeeConverter(true, full_chain));
            }

            @Override
            public Employee getById(Long Id, EmployeeConverter converter) {
                List<Employee> resultList = GetResultList("SELECT * FROM employee WHERE id = " + Id, converter);
                if (resultList.size()>0){
                    return resultList.get(0);
                }
                return null;
            }

            @Override
            public List<Employee> getAll(Integer page, Integer size, String sort) {
                return GetResultList(BaseRequest(page, size, sort,""),new EmployeeConverter());
            }
        };
    }

    private DepartmentDao departmentDAO() {
        return new DepartmentDao() {

            @Override
            public Department getById(Long Id) {
                List<Department> resultList = GetResultList("SELECT * FROM DEPARTMENT WHERE id = " + Id, new DepartmentConverter());
                if (resultList.size()>0){
                    return resultList.get(0);
                }
                return null;
            }

            @Override
            public Department getByName(String Name) {
                List<Department> resultList = GetResultList("SELECT * FROM DEPARTMENT WHERE NAME = " + "'"+Name +"'", new DepartmentConverter());
                if (resultList.size()>0){
                    return resultList.get(0);
                }
                return null;
            }
        };
    }
}
