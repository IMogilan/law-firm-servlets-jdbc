package com.mogilan.repository;

import java.util.List;
import java.util.Optional;

public interface CrudDao<T, K> {
    List<T> findAll();

    Optional<T> findById(K id);

    T save(T entity);

    boolean update(T entity);

    boolean delete(K id);
}
