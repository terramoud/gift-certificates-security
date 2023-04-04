package com.epam.esm.model.entity;

/**
 * The Role enum represents the roles that a user can have in the application.
 * The enum values are: ADMIN, USER, GUEST.
 *
 * @author Alexander Koreshev
 * @since 1.0
 */
public enum Role {
    ADMIN, USER, GUEST;

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    }
}
