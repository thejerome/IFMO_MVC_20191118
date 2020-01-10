package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class serviceFactory {
    private static ResultSet getRS(String sql) throws SQLException {
        return ConnectionSource.instance().createConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(sql);
    }

    private static List<Employee> getEmployee(ResultSet resultSet, boolean chain){
        try{
            List<Employee> employees = new ArrayList<Employee>();
            while (resultSet.next()){
                int row = resultSet.getRow();
                Long id = resultSet.getLong("id");
                Employee employee = employeeFactory(resultSet, id, chain);
                employees.add(employee);
                resultSet.absolute(row);
            }
            return employees;
        }catch (SQLException ex){
            return null;
        }
    }

    private static Employee employeeFactory(ResultSet resultSet, Long id, boolean chain){
        try{
            FullName fullName = new FullName(resultSet.getString("firstname"),
                    resultSet.getString("lastname"),
                    resultSet.getString("middlename"));
            Position position = Position.valueOf(resultSet.getString("position"));
            LocalDate hireDate = LocalDate.parse(String.valueOf(resultSet.getDate("hiredate")));
            BigDecimal salary = resultSet.getBigDecimal("salary");
            Department department = getDepartment(resultSet);
            if(resultSet.getInt("manager") == 0){
                return new Employee(id, fullName, position, hireDate, salary, null, department);
            }
            else {
                Long manager_id = resultSet.getLong("manager");
                Employee manager = null;
                resultSet.beforeFirst();
                while (resultSet.next()) {
                    if (resultSet.getLong("id") == manager_id) {
                        if(!chain) manager = getManager(resultSet, manager_id);
                        else manager = employeeFactory(resultSet, manager_id, true);
                    }
                }
                return new Employee(id, fullName, position, hireDate, salary, manager, department);
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return null;
    }

    private static Department getDepartment(ResultSet resultSet) throws SQLException {
        return resultSet.getLong("department") !=0 ? new Department(resultSet.getLong("department"),
                resultSet.getString("name"),
                resultSet.getString("location")) : null;
    }

    private static Employee getManager(ResultSet resultSet, Long id){
        try{
            return (new Employee(id, new FullName(resultSet.getString("firstname"),
                    resultSet.getString("lastname"),
                    resultSet.getString("middlename")),
                    Position.valueOf(resultSet.getString("position")),
                    LocalDate.parse(String.valueOf(resultSet.getDate("hiredate"))),
                    resultSet.getBigDecimal("salary"),
                    null,
                    getDepartment(resultSet))
            );

        }catch (SQLException ex){
            return null;
        }
    }

    private static List<Employee> getList(Paging paging, String sql) throws SQLException {
        List<Employee> allSortByHireDate = new ArrayList<Employee>();
        List<Employee> all = getEmployee(getRS(sql), false);
        int start = paging.itemPerPage*paging.page;
        int number = paging.itemPerPage;
        while(number>0 && start<14){
            if(all!=null){
                allSortByHireDate.add(all.get(start));
                start ++;
                number --;
            }
        }
        return allSortByHireDate;
    }

    private static String getSQL(String sort){
        return "SELECT EMPLOYEE.ID, EMPLOYEE.FIRSTNAME, EMPLOYEE.LASTNAME, EMPLOYEE.MIDDLENAME, " +
                "EMPLOYEE.POSITION, EMPLOYEE.MANAGER, EMPLOYEE.HIREDATE, EMPLOYEE.SALARY, EMPLOYEE.DEPARTMENT, " +
                "DEPARTMENT.NAME, DEPARTMENT.LOCATION FROM EMPLOYEE LEFT JOIN DEPARTMENT ON EMPLOYEE.DEPARTMENT" +
                " = DEPARTMENT.ID" + sort;
    }

    public static List<Employee> getAll(Paging paging, String sort){
        try{
            switch (sort){
                case "lastName":
                    return getList(paging, getSQL(" ORDER BY EMPLOYEE.LASTNAME"));
                case "hired":
                    return getList(paging, getSQL(" ORDER BY EMPLOYEE.HIREDATE"));
                case "position":
                    return getList(paging, getSQL(" ORDER BY EMPLOYEE.POSITION"));
                case "salary":
                    return getList(paging, getSQL(" ORDER BY EMPLOYEE.SALARY"));
                default:
                    return getList(paging, getSQL(""));
            }
        } catch (SQLException e){
            return null;
        }
    }

    public static Employee getById(Long id, boolean chain) throws SQLException {
        List<Employee> empAll = getEmployee(getRS(getSQL(" ORDER BY EMPLOYEE.ID")), chain);
        for(int i=0; i<14; i++){
            if(empAll!=null && empAll.get(i).getId()!=0 && empAll.get(i).getId().equals(id)) return empAll.get(i);
        }
        return null;
    }

    private static List<Employee> getByDepNameOrId(String department, Paging paging, String sql) throws SQLException {
        List<Employee> empAllBySort = getEmployee(getRS(sql), false);
        List<Employee> empByDep = new ArrayList<Employee>();
        int i = 0;
        while (i<14){
            if(empAllBySort!=null){
                if(!isNumeric(department)){
                    if(empAllBySort.get(i).getDepartment() != null && empAllBySort.get(i).getDepartment().getName().equals(department)){
                        empByDep.add(empAllBySort.get(i));
                    }
                }else {
                    if(empAllBySort.get(i).getDepartment() != null && empAllBySort.get(i).getDepartment().getId().toString().equals(department)){
                        empByDep.add(empAllBySort.get(i));
                    }
                }
            }
            i++;
        }
        List<Employee> empPage = new ArrayList<Employee>();
        int start = paging.itemPerPage*paging.page;
        int number = paging.itemPerPage;
        while(number>0 && start<empByDep.size()){
            empPage.add(empByDep.get(start));
            start ++;
            number --;
        }
        return empPage;
    }

    private static boolean isNumeric(String str){
        for(int i=0; i<str.length(); i++){
            if(str.charAt(i)<'0' || str.charAt(i)>'9') return false;
        }
        return true;
    }

    public static List<Employee> getByDep(String department, Paging paging, String sort){
        try{
            switch (sort){
                case "lastName":
                    return getByDepNameOrId(department, paging, getSQL(" ORDER BY EMPLOYEE.LASTNAME"));
                case "hired":
                    return getByDepNameOrId(department, paging, getSQL(" ORDER BY EMPLOYEE.HIREDATE"));
                case "position":
                    return getByDepNameOrId(department, paging, getSQL(" ORDER BY EMPLOYEE.POSITION"));
                case "salary":
                    return getByDepNameOrId(department, paging, getSQL(" ORDER BY EMPLOYEE.SALARY"));
                default:
                    return getByDepNameOrId(department, paging, getSQL(""));
            }
        }catch (SQLException e){
            return null;
        }
    }

    private static List<Employee> getByMg(Long managerId, Paging paging, String sql) throws SQLException {
        List<Employee> empAllBySort = getEmployee(getRS(sql), false);
        List<Employee> empByManager = new ArrayList<Employee>();
        int i = 0;
        while (i<14){
            if (empAllBySort!=null &&empAllBySort.get(i).getManager() != null && empAllBySort.get(i).getManager().getId().equals(managerId)) {
                    empByManager.add(empAllBySort.get(i));
            }
            i++;
        }
        List<Employee> empPage = new ArrayList<Employee>();
        int start = paging.itemPerPage*paging.page;
        int number = paging.itemPerPage;
        while(number>0 && start<empByManager.size()){
            empPage.add(empByManager.get(start));
            start ++;
            number --;
        }
        return empPage;
    }

    public static List<Employee> getByManager(Long managerId, Paging paging, String sort){
        try{
            switch (sort){
                case "lastName":
                    return getByMg(managerId, paging, getSQL(" ORDER BY EMPLOYEE.LASTNAME"));
                case "hired":
                    return getByMg(managerId, paging, getSQL(" ORDER BY EMPLOYEE.HIREDATE"));
                case "position":
                    return getByMg(managerId, paging, getSQL(" ORDER BY EMPLOYEE.POSITION"));
                case "salary":
                    return getByMg(managerId, paging, getSQL(" ORDER BY EMPLOYEE.SALARY"));
                default:
                    return getByMg(managerId, paging, getSQL(""));
            }
        }catch (SQLException e){
            return null;
        }
    }
}
