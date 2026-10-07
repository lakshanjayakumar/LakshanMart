package com.lakshan.lakshanmart.service;

import com.lakshan.lakshanmart.dao.UserDAO;
import com.lakshan.lakshanmart.dto.UserLoginRequest;
import com.lakshan.lakshanmart.dto.UserRegisterRequest;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.exception.AuthenticationException;
import com.lakshan.lakshanmart.exception.ConflictException;
import com.lakshan.lakshanmart.exception.ResourceNotFoundException;
import com.lakshan.lakshanmart.exception.ValidationException;
import com.lakshan.lakshanmart.model.Role;
import com.lakshan.lakshanmart.model.User;
import com.lakshan.lakshanmart.util.PasswordUtil;
import com.lakshan.lakshanmart.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of UserService containing domain business logic and validation.
 */
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDAO userDAO;

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserResponseDTO register(UserRegisterRequest request) {
        // Validation at the top of the method (Section 13.5)
        if (request == null) {
            throw new ValidationException("Registration request payload cannot be null.");
        }
        ValidationUtil.requireNonBlank(request.getName(), "Name");
        ValidationUtil.validateEmail(request.getEmail());
        ValidationUtil.validatePassword(request.getPassword());

        String requestedRoleStr = request.getRole();
        ValidationUtil.requireNonBlank(requestedRoleStr, "Role");
        Role role = Role.fromString(requestedRoleStr);
        if (role == null || role == Role.ADMIN) {
            throw new ValidationException("Invalid registration role. Allowed roles are BUYER and SELLER.");
        }

        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (userDAO.existsByEmail(normalizedEmail)) {
            throw new ConflictException("Email is already registered: " + normalizedEmail);
        }

        // Hash password with BCrypt
        String passwordHash = PasswordUtil.hashPassword(request.getPassword());

        User newUser = new User();
        newUser.setName(request.getName().trim());
        newUser.setEmail(normalizedEmail);
        newUser.setPasswordHash(passwordHash);
        newUser.setRole(role);

        User created = userDAO.create(newUser);
        logger.info("New user registered successfully with ID: {}, role: {}", created.getId(), created.getRole());

        return toResponseDTO(created);
    }

    @Override
    public UserResponseDTO authenticate(UserLoginRequest request) {
        if (request == null) {
            throw new ValidationException("Login credentials cannot be null.");
        }
        ValidationUtil.requireNonBlank(request.getEmail(), "Email");
        ValidationUtil.requireNonBlank(request.getPassword(), "Password");

        String normalizedEmail = request.getEmail().trim().toLowerCase();
        User user = userDAO.findByEmail(normalizedEmail)
                .orElseThrow(() -> new AuthenticationException("Invalid email or password."));

        if (!PasswordUtil.checkPassword(request.getPassword(), user.getPasswordHash())) {
            logger.warn("Authentication failed for email: {}", normalizedEmail);
            throw new AuthenticationException("Invalid email or password.");
        }

        logger.info("User authenticated successfully: ID {}, role {}", user.getId(), user.getRole());
        return toResponseDTO(user);
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        ValidationUtil.requirePositiveId(id, "User");
        User user = userDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return toResponseDTO(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userDAO.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO updateUserRole(Long userId, String role) {
        ValidationUtil.requirePositiveId(userId, "User");
        Role r = Role.fromString(role);
        if (r == null) {
            throw new ValidationException("Invalid role: " + role);
        }
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        userDAO.updateRole(userId, r.name());
        user.setRole(r);
        logger.info("User ID {} role updated to {}", userId, r.name());
        return toResponseDTO(user);
    }

    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
