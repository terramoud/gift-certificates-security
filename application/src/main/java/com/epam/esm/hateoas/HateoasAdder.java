package com.epam.esm.hateoas;

import org.springframework.hateoas.RepresentationModel;

import java.util.Collection;

public interface HateoasAdder<T extends RepresentationModel<T>> {
    int DEFAULT_PAGE = 0;
    int DEFAULT_SIZE = 5;
    String UPDATE = "update";
    String DELETE = "delete";
    String CREATE = "create";

    /**
     * Adds links to Dto entity object.
     *
     * @param dto this dto of entity to which links will be added
     */
    void addLinks(T dto);

    default void addLinks(Collection<T> dtos) {
        dtos.forEach(this::addLinks);
    }
}
