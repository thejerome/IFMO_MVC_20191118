package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Department;

public interface DepartmentDao {
    Department getById(Long Id);
    Department getByName(String Name);
}