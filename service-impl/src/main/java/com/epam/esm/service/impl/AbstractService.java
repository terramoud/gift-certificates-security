package com.epam.esm.service.impl;

import com.epam.esm.service.api.BaseService;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public abstract class AbstractService<T, N> implements BaseService<T, N> {
    @SafeVarargs
    protected final boolean isEqualsIds(N... ids) {
        return List.of(ids).isEmpty() || Arrays.stream(ids).allMatch(ids[0]::equals);
    }
}
