package com.epam.esm.service.api;

import com.epam.esm.domain.payload.PageDto;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

public interface BaseService<T, N> {
    List<T> findAll(LinkedMultiValueMap<String, String> fields, PageDto pageDto);

    T findById(N id);

    T create(T t);

    T update(N id, T t);

    T deleteById(N id);
}
