package com.lakshan.lakshanmart.dao;

import com.lakshan.lakshanmart.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for user persistence.
 */
public interface UserDAO {

    /**
     * Find a user by their unique primary key ID.
     */
    Optional<User> findById(Long id);

    /**
     * Find a user by unique email address.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists with the given email address.
     */
    boolean existsByEmail(String email);

    /**
     * Inserts a new user record.
     */
    User create(User user);

    /**
     * Lists all registered users (for Admin overview).
     */
    List<User> findAll();

    /**
     * Updates a user's role (e.g. promoting BUYER to SELLER).
     */
    boolean updateRole(Long userId, String role);
}
