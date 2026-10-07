package com.lakshan.lakshanmart.service;

import com.lakshan.lakshanmart.dto.UserLoginRequest;
import com.lakshan.lakshanmart.dto.UserRegisterRequest;
import com.lakshan.lakshanmart.dto.UserResponseDTO;

import java.util.List;

/**
 * Service interface for user operations and authentication business logic.
 */
public interface UserService {

    /**
     * Registers a new user account (BUYER or SELLER).
     *
     * @param request registration details
     * @return created user response DTO
     */
    UserResponseDTO register(UserRegisterRequest request);

    /**
     * Authenticates a user with email and password.
     *
     * @param request login credentials
     * @return authenticated user response DTO
     */
    UserResponseDTO authenticate(UserLoginRequest request);

    /**
     * Retrieves a user by their unique primary key.
     */
    UserResponseDTO getUserById(Long id);

    /**
     * Lists all registered users (for Admin moderation).
     */
    List<UserResponseDTO> getAllUsers();

    /**
     * Updates the role of a user (e.g. promoting BUYER to SELLER).
     */
    UserResponseDTO updateUserRole(Long userId, String role);
}
