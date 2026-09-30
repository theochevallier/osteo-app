package org.example.ostheo_projet.Interface;

import java.util.List;

/**
 * Product class of the factory design pattern
 * The genericity T allows each implementation of product to adapt it to its entity
 * @param <T>
 */
public interface Service<T> {

    T getById(int id);
    void delete(T entity);

    default T getByName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    default void save(T entity) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    default List<T> getAll(){
        throw new UnsupportedOperationException("Not supported yet.");
    }
}