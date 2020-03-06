package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Department;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentListMapper implements ListMapper<Department> {
    @Override
    public List<Department> mapList(ResultSet resultSet) {
        List<Department> departments;
        try {
            departments = new ArrayList<>();
            while (resultSet.next()) {
                departments.add(mapRow(resultSet));
            }
            return departments;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Department mapRow(ResultSet resultSet) throws SQLException {
        return new Department(
                Long.parseLong(resultSet.getString("ID")),
                resultSet.getString("NAME"),
                resultSet.getString("LOCATION")
        );
    }
}
