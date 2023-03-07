package com.epam.esm.entity;

/**
 * Represents relevant entity from database's table
 *
 * @author Alexander Koreshev
 */
public enum Role {
    ADMIN, USER, GUEST;

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    }
}
