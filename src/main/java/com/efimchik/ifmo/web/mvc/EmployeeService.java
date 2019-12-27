package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EmployeeService {

    public EmployeeService(){

    }

    public static List<Employee> getAllEmployees(Integer page, Integer size, String sort) throws SQLException {
        String str = "select * from EMPLOYEE";
        return getAnswer(str, page, size,sort);
    }

    public static List<Employee> getEmployeesByManager(Long managerId, Integer page, Integer size, String sort) throws SQLException {
        String str = "select * from EMPLOYEE where manager = " + managerId;
        return getAnswer(str, page, size, sort);
    }

    public static  List<Employee> getEmployeesByDepartment(Integer departmentId,Integer page, Integer size, String sort) throws SQLException {
        String str = "select * from EMPLOYEE where department = " + departmentId;
        return getAnswer(str,page,size,sort);
    }

    public static List<Employee> getEmpList(ResultSet resultSet) throws SQLException{
        List<Employee> employees = new LinkedList<Employee>();
        while(resultSet.next()){
            Employee employee = getEmployee(resultSet, false);
            employees.add(employee);
        }
        return employees;
    }

    public static List<Employee> getAnswer(String str, Integer page, Integer size, String sort) throws SQLException {
        if (sort != null) {
            str += " order by " + sort;
        }
        ResultSet resultSet = getResultSet(str);
        List<Employee> answer = getEmpList(resultSet);
        resultSet.close();
        if (page != null && size != null) {
            int first = page*size;
            int second = ((page + 1) * size < answer.size() ? (page + 1) * size : answer.size());
            answer = answer.subList(first,second);
        }
        return answer;
    }

    public static Employee getEmployeeById(String id, String needFull) throws SQLException {
        String str = "select * from EMPLOYEE where id = "+ id;
        Employee answer = null;
        ResultSet resultSet = getResultSet(str);
        resultSet.absolute(0);
        if(needFull!= null && needFull.equals(true)) {
            if (resultSet.next()) {
                resultSet.absolute(1);
                answer = getEmployee(resultSet, true);
            }
        }
        else answer = getEmployee(resultSet, false);
        resultSet.close();
        return answer;
    }

    public static Integer getDepartmentId(String departmentName){
        Integer departmentId;
        Map<String,Integer> departmentsMap = new HashMap<>();
        departmentsMap.put("ACCOUNTING",10);
        departmentsMap.put("RESEARCH",20);
        departmentsMap.put("SALES",30);
        departmentsMap.put("OPERATIONS",40);
        if (departmentsMap.containsKey(departmentName)){
            departmentId = departmentsMap.get(departmentName);
        } else departmentId = Integer.valueOf(departmentName);
        return departmentId;
    }


    public static Employee getEmployee(ResultSet resultSet, boolean needFull) throws SQLException {
            Long id = new Long(String.valueOf(resultSet.getInt("id")));
            FullName fullName = new FullName(
                    resultSet.getString("firstName"),
                    resultSet.getString("lastName"),
                    resultSet.getString("middleName"));

            Position position = Position.valueOf(resultSet.getString("position"));
            LocalDate localDate = LocalDate.parse(resultSet.getString("hiredate"));
            BigDecimal salary = new BigDecimal(String.valueOf(resultSet.getInt("salary")));
            Long departmentId = new Long(String.valueOf(resultSet.getInt("department")));
            Employee manager = getManager(resultSet, needFull);
            Department department = getDepartmentById(departmentId);

            return new Employee(id, fullName, position, localDate, salary, manager, department);
    }

        public static Department getDepartmentById(Long Id) throws SQLException {
            ResultSet resultSet = getResultSet("select * from DEPARTMENT");
            List<Department> departments = getDepList(resultSet);
            Department answer = null;
            resultSet.close();
            for (Department i : departments) {
                if (i.getId().equals(Id)) {
                    answer = i;
                    break;
                }
            }
            return answer;
        }

        private static List<Department> getDepList(ResultSet resultSet) throws SQLException {
            List<Department> departments = new LinkedList<Department>();
            while (resultSet.next()) {
                Long id = (long) resultSet.getInt("id");
                String name = resultSet.getString("name");
                String location = resultSet.getString("location");
                Department department = new Department(id, name, location);
                departments.add(department);
            }
            return departments;
        }

        private static Employee getManager(ResultSet resultSet, boolean needFull) throws SQLException {
            ResultSet resultSet1 = getResultSet("select * from EMPLOYEE");
            Employee manager = null;
            int cur = resultSet.getRow();
            if (resultSet.getObject("manager") != null) {
                int managerId = resultSet.getInt("manager");
                resultSet1.absolute(0);
                while (resultSet1.next()) {
                    if (managerId == resultSet1.getInt("id"))
                        break;
                }
                if (!resultSet1.isAfterLast()) {
                    if (!needFull) {
                        manager = new Employee(
                                new Long(String.valueOf(resultSet1.getInt("id"))),
                                new FullName(
                                        resultSet1.getString("firstName"),
                                        resultSet1.getString("lastName"),
                                        resultSet1.getString("middleName")),
                                Position.valueOf(resultSet1.getString("position")),
                                LocalDate.parse(resultSet1.getString("hireDate")),
                                new BigDecimal(resultSet1.getInt("salary")),
                                null,
                                getDepartmentById(new Long(String.valueOf(resultSet1.getInt("department"))))
                        );
                    } else {
                        resultSet.absolute(1);
                        while (resultSet1.getInt("id") != resultSet.getInt("id"))
                            resultSet.next();
                        manager = getEmployee(resultSet, true);
                    }
                }
            }
            resultSet.absolute(cur);
            resultSet1.close();
            return manager;
        }

        public static ResultSet getResultSet(String str) throws SQLException {
            return ConnectionSource.getInstance().createConnection().createStatement(1004, 1008).executeQuery(str);
        }
}