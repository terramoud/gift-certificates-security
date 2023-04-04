package com.epam.esm.hateoas;

import org.springframework.hateoas.RepresentationModel;

import java.util.Collection;

/**
 * The interface {@code HateoasAdder} defines methods for
 * adding HATEOAS links to a DTO entity object.
 * <p>
 * The implementation of this interface is responsible for
 * adding HATEOAS links to a DTO object of the entity type.
 * </p>
 * @param <T> the type of DTO entity for which HATEOAS links are added
 * @author Oleksandr Koreshev
 * @since 1.0
 */
public interface HateoasAdder<T extends RepresentationModel<T>> {

    String UPDATE = "update";
    String DELETE = "delete";
    String CREATE = "create";

    /**
     * Adds links to Dto entity object.
     *
     * @param dto this dto of entity to which links will be added
     */
    void addLinks(T dto);

    /**
     * Adds HATEOAS links to the given collection of
     * DTO entity objects.
     * @param dtos the collection of DTO entity
     *             objects to which links will be added
     */
    default void addLinks(Collection<T> dtos) {
        dtos.forEach(this::addLinks);
    }
}
