package com.efimchik.ifmo.web.mvc;

import java.sql.ResultSet;
import java.util.List;

public interface ListMapper <T> {
    List<T> mapList(ResultSet resultSet);
}
