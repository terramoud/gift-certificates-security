package com.epam.esm.domain.entity;


import java.util.List;

/**
 * Represents relevant entity from database's table
 *
 * @author Alexander Koreshev
 */
public enum Role {
    ADMIN, USER;

    public static Role getRole(int userRoleId) {
        if (userRoleId < 1 || userRoleId > Role.values().length) {
            return null;
        }
        int roleId = userRoleId - 1;
        return Role.values()[roleId];
    }

    public static int getRoleId(Role role) {
        return List.of(Role.values()).indexOf(role) + 1;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
