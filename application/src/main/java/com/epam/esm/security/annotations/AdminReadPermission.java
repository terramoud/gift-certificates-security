package com.epam.esm.security.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to allow access
 * to methods and classes to users with
 * the "SCOPE_read" authority and "ROLE_ADMIN" role.
 * This is achieved by using Spring Security's
 * {@link org.springframework.security.access.prepost.PreAuthorize}
 * annotation which allows for method-level security to be
 * applied to Spring-managed beans. By applying @PreAuthorize
 * with this specific expression, only users with both
 * the "SCOPE_read" authority and the "ROLE_ADMIN" role
 * will be able to access the annotated method or class.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('SCOPE_read') and hasRole('ROLE_ADMIN')")
public @interface AdminReadPermission {
}
