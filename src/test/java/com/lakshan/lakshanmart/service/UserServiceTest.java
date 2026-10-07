package com.lakshan.lakshanmart.service;

import com.lakshan.lakshanmart.dao.UserDAO;
import com.lakshan.lakshanmart.dto.UserLoginRequest;
import com.lakshan.lakshanmart.dto.UserRegisterRequest;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.exception.AuthenticationException;
import com.lakshan.lakshanmart.exception.ConflictException;
import com.lakshan.lakshanmart.exception.ValidationException;
import com.lakshan.lakshanmart.model.Role;
import com.lakshan.lakshanmart.model.User;
import com.lakshan.lakshanmart.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userDAO);
    }

    @Test
    @DisplayName("Register new BUYER successfully with bcrypt hash")
    void testRegisterSuccess() {
        UserRegisterRequest req = new UserRegisterRequest("John Doe", "john@example.com", "Password@123", "BUYER");

        when(userDAO.existsByEmail("john@example.com")).thenReturn(false);
        when(userDAO.create(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(10L);
            u.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            return u;
        });

        UserResponseDTO response = userService.register(req);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("BUYER", response.getRole());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDAO).create(captor.capture());
        User createdUser = captor.getValue();

        assertTrue(PasswordUtil.checkPassword("Password@123", createdUser.getPasswordHash()),
                "Saved password hash must match plain password via BCrypt");
    }

    @Test
    @DisplayName("Register throws ConflictException if email is already registered")
    void testRegisterDuplicateEmail() {
        UserRegisterRequest req = new UserRegisterRequest("Jane Doe", "jane@example.com", "Password@123", "BUYER");
        when(userDAO.existsByEmail("jane@example.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.register(req));
        verify(userDAO, never()).create(any());
    }

    @Test
    @DisplayName("Register rejects ADMIN role registration")
    void testRegisterAdminRoleRejected() {
        UserRegisterRequest req = new UserRegisterRequest("Hacker", "admin@fake.com", "Password@123", "ADMIN");

        ValidationException ex = assertThrows(ValidationException.class, () -> userService.register(req));
        assertTrue(ex.getMessage().contains("Allowed roles are BUYER and SELLER"));
        verify(userDAO, never()).create(any());
    }

    @Test
    @DisplayName("Register validates invalid email format")
    void testRegisterInvalidEmail() {
        UserRegisterRequest req = new UserRegisterRequest("Invalid", "not-an-email", "Password@123", "BUYER");

        assertThrows(ValidationException.class, () -> userService.register(req));
        verify(userDAO, never()).create(any());
    }

    @Test
    @DisplayName("Authenticate succeeds with valid email and password")
    void testAuthenticateSuccess() {
        String rawPassword = "ValidPassword123";
        String hash = PasswordUtil.hashPassword(rawPassword);
        User storedUser = new User(1L, "Alice", "alice@example.com", hash, Role.SELLER, new Timestamp(System.currentTimeMillis()));

        when(userDAO.findByEmail("alice@example.com")).thenReturn(Optional.of(storedUser));

        UserLoginRequest loginReq = new UserLoginRequest("alice@example.com", rawPassword);
        UserResponseDTO response = userService.authenticate(loginReq);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("SELLER", response.getRole());
    }

    @Test
    @DisplayName("Authenticate throws AuthenticationException on bad password")
    void testAuthenticateBadPassword() {
        String hash = PasswordUtil.hashPassword("CorrectPassword");
        User storedUser = new User(1L, "Alice", "alice@example.com", hash, Role.SELLER, null);

        when(userDAO.findByEmail("alice@example.com")).thenReturn(Optional.of(storedUser));

        UserLoginRequest loginReq = new UserLoginRequest("alice@example.com", "WrongPassword");
        assertThrows(AuthenticationException.class, () -> userService.authenticate(loginReq));
    }
}
