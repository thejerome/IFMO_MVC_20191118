package com.efimchik.ifmo.web.mvc.repository;

import java.sql.ResultSet;

public interface RowMapper<T> {
    T mapRow(ResultSet resultSet);
}
