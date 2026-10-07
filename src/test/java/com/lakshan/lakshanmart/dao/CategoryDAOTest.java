package com.lakshan.lakshanmart.dao;

import com.lakshan.lakshanmart.model.Category;
import com.lakshan.lakshanmart.util.DatabaseUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CategoryDAOTest extends BaseDAOTest {

    private CategoryDAO categoryDAO;

    @BeforeEach
    void setUp() throws Exception {
        categoryDAO = new CategoryDAOImpl();

        // Seed test categories
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO categories (name, description, icon) VALUES (?, ?, ?)")) {
            ps.setString(1, "Electronics");
            ps.setString(2, "Gadgets and tech");
            ps.setString(3, "🎧");
            ps.executeUpdate();

            ps.setString(1, "Fashion");
            ps.setString(2, "Apparel and clothing");
            ps.setString(3, "👔");
            ps.executeUpdate();
        }
    }

    @Test
    @DisplayName("findAll returns all inserted categories")
    void testFindAll() {
        List<Category> list = categoryDAO.findAll();
        assertNotNull(list);
        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("findById returns category if present")
    void testFindById() {
        List<Category> list = categoryDAO.findAll();
        Long firstId = list.get(0).getId();

        Optional<Category> found = categoryDAO.findById(firstId);
        assertTrue(found.isPresent());
        assertEquals("Electronics", found.get().getName());
    }

    @Test
    @DisplayName("findByName performs case-insensitive category search")
    void testFindByName() {
        Optional<Category> found = categoryDAO.findByName("electronics");
        assertTrue(found.isPresent());
        assertEquals("Electronics", found.get().getName());

        Optional<Category> notFound = categoryDAO.findByName("NonExistent");
        assertFalse(notFound.isPresent());
    }
}
