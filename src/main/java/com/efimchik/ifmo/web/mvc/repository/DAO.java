package com.efimchik.ifmo.web.mvc.repository;

import java.util.List;

public interface DAO<T, ID> {
    List<T> findAll(String sort);

    T findById(ID id);
}
