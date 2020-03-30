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
    private String BaseEmployeeRequest(Integer page, Integer size, String sort, String optional){
        return "SELECT * FROM employee" +
                optional +
                ((sort != null) ? " ORDER BY " + sort.replace("hired","HIREDATE") : " ") +
                ((size != null) ? " LIMIT " + size : " ") +
                ((page != null && size != null) ? " OFFSET " + size * page : " ");
    }

    public Department GetDepartment(String id, boolean byName){
        String filter = "id = " + id;
        if (byName){
            filter = "NAME = " + "'"+id +"'";
        }
        List<Department> resultList = CalculateResultList("SELECT * FROM DEPARTMENT WHERE " + filter, new Parsers.DepartmentParser());
        if (resultList.size()>0){
            return resultList.get(0);
        }
        return null;
    }

    public <T>List<T> CalculateResultList(String sql, Parsers.Parser<T> converter){
        List<T> temp = new ArrayList<>();
        try {
            ResultSet res = ConnectionSource.generateSqlQuery(sql);
            while (res.next()){
                temp.add(converter.Parse(res));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temp;
    }

    EmployeeDao employeeDAO() {
        return new EmployeeDao() {
            @Override
            public Employee getEmployee(String Id, boolean full_chain) {
                return getById(Long.parseLong(Id), new Parsers.EmployeeParser(true, full_chain));
            }

            public Employee getById(Long Id, Parsers.EmployeeParser converter) {
                List<Employee> resultList = CalculateResultList("SELECT * FROM employee WHERE id = " + Id, converter);
                if (resultList.size()>0){
                    System.out.println(resultList.get(0));
                    return resultList.get(0);
                }
                return null;
            }

            @Override
            public List<Employee> getEmployeesFilteredList(FilterType filterType, String filter, Integer page, Integer size, String sort) {

                if (filterType == FilterType.byDepartmentId){
                    try{
                        return CalculateResultList( BaseEmployeeRequest(page, size, sort," WHERE department = " + Integer.parseInt(filter)), new Parsers.EmployeeParser());
                    }catch (Exception ex){
                        Department department = new DaoFactory().GetDepartment(filter, true);
                        if (department == null){
                            return new LinkedList<>();
                        }

                        return CalculateResultList(BaseEmployeeRequest(page, size, sort," WHERE department = " + department.getId()), new Parsers.EmployeeParser());
                    }
                }

                if (filterType == FilterType.byManagerId){
                    return CalculateResultList(BaseEmployeeRequest(page, size, sort," WHERE manager = " + filter),new Parsers.EmployeeParser());
                }

                if (filterType == FilterType.byName){
                    return CalculateResultList(BaseEmployeeRequest(page, size, sort," WHERE manager = " + filter),new Parsers.EmployeeParser());
                }

                if (filterType == FilterType.none){
                    return CalculateResultList(BaseEmployeeRequest(page, size, sort,""), new Parsers.EmployeeParser());
                }

                return null;
            }
        };
    }
}
