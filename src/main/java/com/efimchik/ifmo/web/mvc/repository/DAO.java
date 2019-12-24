package com.efimchik.ifmo.web.mvc.repository;

import java.util.List;

public interface DAO<T, I> {
    List<T> findAll(String sort);

    T findById(I id);
}
