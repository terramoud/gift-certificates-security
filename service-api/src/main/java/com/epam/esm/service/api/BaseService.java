package com.epam.esm.service.api;


public interface BaseService<T, N> {

    T findById(N id);

    T create(T t);

    T update(N id, T t);

    T deleteById(N id);
}
