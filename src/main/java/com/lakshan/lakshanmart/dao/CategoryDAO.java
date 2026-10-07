package com.lakshan.lakshanmart.dao;

import com.lakshan.lakshanmart.model.Category;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for product categories.
 */
public interface CategoryDAO {

    /**
     * Lists all available marketplace categories ordered alphabetically.
     */
    List<Category> findAll();

    /**
     * Finds a category by its primary key ID.
     */
    Optional<Category> findById(Long id);

    /**
     * Finds a category by its unique name.
     */
    Optional<Category> findByName(String name);
}
