package com.efimchik.ifmo.web.mvc.service;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;
import com.efimchik.ifmo.web.mvc.source.ConnectionSource;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Service {
    public static List<Employee> getNeededList (Integer page, Integer size, List<Employee> ans){
        if (page != null && size != null){
            int from = page * size;
            int to = ((page + 1) * size < ans.size())? (page + 1) * size : ans.size();
            if (from > to){
                return new ArrayList<>();
            }
            return ans.subList(from, to);
        }else{
            return ans;
        }
    }

    public static List<Employee> getListOfEmployee(String sort){
        try{
            ResultSet rs;
            if (sort != null){
                rs = giveMeResultSet("SELECT * FROM EMPLOYEE ORDER BY " + sort);
            }else{
                rs = giveMeResultSet("SELECT * FROM EMPLOYEE");
            }
            List<Employee> ans = new ArrayList<>();
            while (rs.next()){
                ans.add(getEmployee(rs, false));
            }
            return ans;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public static List<Employee> getListOfEmployeeById(String param, int id, Boolean isFull_chain, String sort){
        try{
            ResultSet rs;
            if (sort != null){
                rs = giveMeResultSet("SELECT * FROM EMPLOYEE ORDER BY " + sort);
            }else{
                rs = giveMeResultSet("SELECT * FROM EMPLOYEE");
            }
            List<Employee> ans = new ArrayList<>();
            while (rs.next()){
                if (rs.getInt(param) == id){
                    if (isFull_chain != null){
                        ans.add(getEmployee(rs, isFull_chain));
                    }else{
                        ans.add(getEmployee(rs, false));
                    }
                }
            }
            return ans;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }


    private static Employee getEmployee(ResultSet resultSet, boolean isFull_chain)throws SQLException{

        int thisRow = resultSet.getRow();
        Employee manager = null;

        if (resultSet.getInt("MANAGER") != 0){
            int managerID = resultSet.getInt("manager");
            resultSet.first();
            while (!resultSet.isAfterLast() &&
                    managerID != resultSet.getInt("id")){
                resultSet.next();
                //search manager row
            }
            if (!resultSet.isAfterLast()){//if found
                if (isFull_chain){
                    manager = getEmployee(resultSet, isFull_chain);
                }else{
                    manager = new Employee(
                            Long.valueOf(resultSet.getString("id")),
                            new FullName(
                                    resultSet.getString("firstName"),
                                    resultSet.getString("lastName"),
                                    resultSet.getString("middleName")),
                            Position.valueOf(resultSet.getString("Position")),
                            LocalDate.parse(resultSet.getString("hireDate")),
                            new BigDecimal(resultSet.getDouble("Salary")),
                            null,
                            departmentMapRow(resultSet.getLong("DEPARTMENT"))
                    );
                }//*/
            }/*
            if (upperManager != null &&
                    managerID == upperManager.getId().intValue()) manager = upperManager;//*/
        }// we have manager*/
        resultSet.absolute( thisRow);


        return new Employee(
                Long.valueOf(resultSet.getString("id")),
                new FullName(
                        resultSet.getString("FIRSTNAME"),
                        resultSet.getString("LASTNAME"),
                        resultSet.getString("MIDDLENAME")),
                Position.valueOf(resultSet.getString("POSITION")),
                LocalDate.parse(resultSet.getString("HIREDATE")),
                new BigDecimal(resultSet.getDouble("SALARY")),
                manager,
                departmentMapRow(resultSet.getLong("DEPARTMENT"))
        );
    }

    private static Department departmentMapRow(Long id) throws SQLException {
        ResultSet rs = giveMeResultSet("SELECT * FROM DEPARTMENT WHERE ID = " + id);
        if (rs.next()) {
            return new Department(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("location"));
        }else {
            return null;
        }
    }

    private static ResultSet giveMeResultSet(String query) throws SQLException {
        return ConnectionSource.getInstance()
                .createConnection()
                .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
                .executeQuery(query);
    }
}
