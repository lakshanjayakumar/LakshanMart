package com.lakshan.lakshanmart.dao;

import com.lakshan.lakshanmart.util.DatabaseUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

public abstract class BaseDAOTest {

    @BeforeAll
    static void initDatabase() {
        Properties props = new Properties();
        props.setProperty("db.driver", "org.h2.Driver");
        props.setProperty("db.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MySQL");
        props.setProperty("db.username", "sa");
        props.setProperty("db.password", "");
        props.setProperty("hikari.maximumPoolSize", "5");
        props.setProperty("hikari.minimumIdle", "1");
        props.setProperty("hikari.connectionTimeout", "10000");

        DatabaseUtil.init(props);
    }

    @BeforeEach
    void resetSchema() throws Exception {
        try (Connection conn = DatabaseUtil.getConnection()) {
            try (var stmt = conn.createStatement()) {
                stmt.execute("DROP ALL OBJECTS");
            }
            try (InputStream schemaStream = getClass().getClassLoader().getResourceAsStream("db/schema.sql")) {
                if (schemaStream != null) {
                    DatabaseUtil.executeSqlScript(conn, schemaStream);
                }
            }
        }
    }

    @AfterAll
    static void tearDown() {
        DatabaseUtil.close();
    }
}
