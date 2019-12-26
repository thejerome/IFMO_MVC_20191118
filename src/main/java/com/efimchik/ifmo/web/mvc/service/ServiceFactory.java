package com.efimchik.ifmo.web.mvc.service;

import com.efimchik.ifmo.web.mvc.ConnectionSource;
import com.efimchik.ifmo.web.mvc.domain.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceFactory {

    private static Statement getStatement() throws SQLException {
        Connection connection = ConnectionSource.instance().createConnection();
        return connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }

    public static EmployeeService employeeService(){
        return new EmployeeService() {

            @Override
            public List<Employee> getAll(Paging paging, String sort) {
                try {
                    List<Employee> list;
                    switch (sort) {
                        case "lastName":
                            list = getEmployeesOrder("SELECT * FROM EMPLOYEE ORDER BY LASTNAME", getAllEmployee(false, getAllDepartment()));
                            break;
                        case "hired":
                            list = getEmployeesOrder("SELECT * FROM EMPLOYEE ORDER BY HIREDATE", getAllEmployee(false, getAllDepartment()));
                            break;
                        case "position":
                            list = getEmployeesOrder("SELECT * FROM EMPLOYEE ORDER BY POSITION", getAllEmployee(false, getAllDepartment()));
                            break;
                        case "salary":
                            list = getEmployeesOrder("SELECT * FROM EMPLOYEE ORDER BY SALARY", getAllEmployee(false, getAllDepartment()));
                            break;
                        default:
                            list = getEmployeesOrder("SELECT * FROM EMPLOYEE", getAllEmployee(false, getAllDepartment()));
                            break;
                    }
                    return getEmployees(paging, list);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }

            public List<Employee> getbyDepartment(String department, Paging paging, String sort) {
                try {
                    List<Employee> list;
                    List<Department> ld = getAllDepartment();
                    if (!department.matches(".*\\d.*")) {
                        department = proceedDepartment(department, ld);
                        System.out.println(department);
                    }
                    switch (sort) {
                        case "lastName":
                            list = getEmployeesOrder("SELECT * FROM EMPLOYEE WHERE EMPLOYEE.DEPARTMENT = " + department + " ORDER BY LASTNAME", getAllEmployee(false, ld));
                            break;
                        case "hired":
                            list = getEmployeesOrder("SELECT * FROM EMPLOYEE WHERE EMPLOYEE.DEPARTMENT = " + department + " ORDER BY HIREDATE", getAllEmployee(false, ld));
                            break;
                        case "position":
                            list = getEmployeesOrder("SELECT * FROM EMPLOYEE WHERE EMPLOYEE.DEPARTMENT = " + department + " ORDER BY POSITION", getAllEmployee(false, ld));
                            break;
                        case "salary":
                            list = getEmployeesOrder("SELECT * FROM EMPLOYEE WHERE EMPLOYEE.DEPARTMENT = " + department + " ORDER BY SALARY", getAllEmployee(false, ld));
                            break;
                        default:
                            list = getEmployeesOrder("SELECT * FROM EMPLOYEE WHERE EMPLOYEE.DEPARTMENT = " + department, getAllEmployee(false, ld));
                            break;
                    }
                    return getEmployees(paging, list);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }

            public List<Employee> getbyManager(String manager, Paging paging, String sort) {
                try {
                    List<Employee> list;
                    switch (sort) {
                        case "lastName":
                            list = getEmployeesOrder("SELECT * FROM EMPLOYEE WHERE MANAGER = " + manager + " ORDER BY LASTNAME", getAllEmployee(false, getAllDepartment()));
                            break;
                        case "hired":
                            list = getEmployeesOrder("SELECT * FROM EMPLOYEE WHERE MANAGER = " + manager + " ORDER BY HIREDATE", getAllEmployee(false, getAllDepartment()));
                            break;
                        case "position":
                            list = getEmployeesOrder("SELECT * FROM EMPLOYEE WHERE MANAGER = " + manager + " ORDER BY POSITION", getAllEmployee(false, getAllDepartment()));
                            break;
                        case "salary":
                            list = getEmployeesOrder("SELECT * FROM EMPLOYEE WHERE MANAGER = " + manager + " ORDER BY SALARY", getAllEmployee(false, getAllDepartment()));
                            break;
                        default:
                            list = getEmployeesOrder("SELECT * FROM EMPLOYEE WHERE MANAGER = " + manager, getAllEmployee(false, getAllDepartment()));
                            break;
                    }
                    return getEmployees(paging, list);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public Employee getWithId(String Id, boolean chain) {
                try {
                    List<Employee> list = getEmployeesOrder("SELECT * FROM EMPLOYEE WHERE ID = " + Id, getAllEmployee(chain, getAllDepartment()));
                    return list.get(0);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    private static List<Employee> getEmployees(Paging paging, List<Employee> list) {
        List<Employee> ans = new ArrayList<>();
        for (int i = paging.itemPerPage*paging.page; i<paging.itemPerPage*(paging.page+1) && i < list.size(); i++) {
            ans.add(list.get(i));
        }
        return ans;
    }

    private static List<Employee> getEmployeesOrder(String sql, List<Employee> le) throws SQLException {
        Statement ps = getStatement();
        ResultSet resultSet = ps.executeQuery(sql);
        List<Employee> list_res = new ArrayList<>();
        while (resultSet.next()) {
            Long id = resultSet.getLong("ID");
            for (Employee e : le) {
                if (id.equals(e.getId())) {
                    list_res.add(e);
                }
            }
        }
        return list_res;
    }

    private static Employee getInfoEmployee(Long Mid, ResultSet resultSet, int numberChain, boolean Chain, List<Department> ld) {
        try {
            while (resultSet.next()) {
                Long id = Long.valueOf(resultSet.getString("ID"));
                if (Mid.equals(id)) {
                    String first_name = resultSet.getString("FIRSTNAME");
                    String middle_name = resultSet.getString("MIDDLENAME");
                    String last_name = resultSet.getString("LASTNAME");
                    FullName full_name = new FullName(first_name, last_name, middle_name);
                    Position pos = Position.valueOf(resultSet.getString("POSITION"));
                    LocalDate date_hire = LocalDate.parse(resultSet.getString("HIREDATE"));
                    BigDecimal salary = resultSet.getBigDecimal("SALARY");
                    int managerid = resultSet.getInt("MANAGER");
                    Employee manager = null;
                    if (managerid != 0 && (Chain || numberChain<1)) {
                        int current = resultSet.getRow();
                        resultSet.beforeFirst();
                        manager = getInfoEmployee((long)managerid, resultSet, numberChain+1, Chain, ld);
                        resultSet.absolute(current);
                    }
                    Department department = null;
                    for (Department department1 : ld) {
                        if (department1.getId().equals(resultSet.getLong("DEPARTMENT")))
                            department = department1;
                    }
                    return new Employee(id, full_name, pos, date_hire, salary, manager, department);
                }
            }
            return null;
        } catch (SQLException e) {
            return null;
        }
    }

    private static List<Employee> getAllEmployee(boolean Chain, List<Department> ld) throws SQLException {
        Statement ps = getStatement();
        ResultSet resultSet = ps.executeQuery("SELECT * FROM EMPLOYEE");
        List<Employee> le = new ArrayList<>();
        while (resultSet.next()) {
            int current = resultSet.getRow();
            Long id = resultSet.getLong("ID");
            resultSet.beforeFirst();
            le.add(getInfoEmployee(id, resultSet, 0, Chain, ld));
            resultSet.absolute(current);
        }
        return le;
    }

    private static List<Department> getAllDepartment() throws SQLException {
        Statement ps = getStatement();
        ResultSet resultSet = ps.executeQuery("SELECT * FROM DEPARTMENT");
        List<Department> ld = new ArrayList<>();
        while (resultSet.next()) {
            ld.add(new Department((long) resultSet.getInt("ID"), resultSet.getString("NAME"), resultSet.getString("LOCATION")));
        }
        return ld;
    }

    private static String proceedDepartment(String department, List<Department> ld) {
        String result = null;
        for (Department department1 : ld) {
            if (department1.getName().equals(department)) {
                result = department1.getId().toString();
                break;
            }
        }
        return result;
    }
}

