package com.lakshan.lakshanmart.dao;

import com.lakshan.lakshanmart.model.Role;
import com.lakshan.lakshanmart.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest extends BaseDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAOImpl();
    }

    @Test
    @DisplayName("Create and retrieve user by ID and email")
    void testCreateAndFindUser() {
        User user = new User();
        user.setName("Test Buyer");
        user.setEmail("buyer@example.com");
        user.setPasswordHash("$2a$10$abcdefghijklmnopqrstuvwxyz");
        user.setRole(Role.BUYER);
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        User created = userDAO.create(user);
        assertNotNull(created.getId());

        Optional<User> foundById = userDAO.findById(created.getId());
        assertTrue(foundById.isPresent());
        assertEquals("buyer@example.com", foundById.get().getEmail());
        assertEquals(Role.BUYER, foundById.get().getRole());

        Optional<User> foundByEmail = userDAO.findByEmail("buyer@example.com");
        assertTrue(foundByEmail.isPresent());
        assertEquals(created.getId(), foundByEmail.get().getId());
    }

    @Test
    @DisplayName("Verify existsByEmail behaves correctly")
    void testExistsByEmail() {
        User user = new User();
        user.setName("Seller One");
        user.setEmail("seller1@example.com");
        user.setPasswordHash("$2a$10$abcdefghijklmnopqrstuvwxyz");
        user.setRole(Role.SELLER);

        userDAO.create(user);

        assertTrue(userDAO.existsByEmail("seller1@example.com"));
        assertTrue(userDAO.existsByEmail("SELLER1@EXAMPLE.COM"));
        assertFalse(userDAO.existsByEmail("unknown@example.com"));
    }

    @Test
    @DisplayName("Find all users returns inserted records")
    void testFindAll() {
        User user1 = new User(null, "User 1", "u1@example.com", "hash1", Role.BUYER, null);
        User user2 = new User(null, "User 2", "u2@example.com", "hash2", Role.SELLER, null);
        userDAO.create(user1);
        userDAO.create(user2);

        List<User> all = userDAO.findAll();
        assertTrue(all.size() >= 2);
    }
}
