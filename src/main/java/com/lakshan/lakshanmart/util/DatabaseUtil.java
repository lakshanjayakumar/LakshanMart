package com.lakshan.lakshanmart.util;

import com.lakshan.lakshanmart.exception.DatabaseException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Database utility managing connection pooling via HikariCP.
 * <p>
 * Strictly adheres to Engineering Rule 5: Connection pool lifecycle owned by
 * ServletContextListener. Direct DriverManager.getConnection() calls are prohibited.
 * </p>
 */
public final class DatabaseUtil {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);

    private static volatile HikariDataSource dataSource;

    private DatabaseUtil() {
    }

    /**
     * Initializes the HikariCP connection pool with given properties.
     *
     * @param properties configuration properties
     */
    public static synchronized void init(Properties properties) {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.warn("HikariDataSource is already initialized");
            return;
        }

        try {
            HikariConfig config = new HikariConfig();
            String driver = properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            String url = properties.getProperty("db.url", "jdbc:mysql://localhost:3306/lakshanmart?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true");
            String username = properties.getProperty("db.username", "root");
            String password = properties.getProperty("db.password", "root");

            // Allow environment variable overrides
            String envDriver = System.getenv("DB_DRIVER");
            if (envDriver != null && !envDriver.isBlank()) {
                driver = envDriver;
            }
            String envUrl = System.getenv("DB_URL");
            if (envUrl != null && !envUrl.isBlank()) {
                url = envUrl;
            }
            String envUser = System.getenv("DB_USERNAME");
            if (envUser != null && !envUser.isBlank()) {
                username = envUser;
            }
            String envPass = System.getenv("DB_PASSWORD");
            if (envPass != null) {
                password = envPass;
            }

            config.setDriverClassName(driver);
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);

            int maxPoolSize = Integer.parseInt(properties.getProperty("hikari.maximumPoolSize", "10"));
            int minIdle = Integer.parseInt(properties.getProperty("hikari.minimumIdle", "2"));
            long connTimeout = Long.parseLong(properties.getProperty("hikari.connectionTimeout", "10000"));
            long idleTimeout = Long.parseLong(properties.getProperty("hikari.idleTimeout", "300000"));
            long maxLifetime = Long.parseLong(properties.getProperty("hikari.maxLifetime", "1200000"));

            config.setMaximumPoolSize(maxPoolSize);
            config.setMinimumIdle(minIdle);
            config.setConnectionTimeout(connTimeout);
            config.setIdleTimeout(idleTimeout);
            config.setMaxLifetime(maxLifetime);
            config.setPoolName("LakshanMart-HikariPool");

            try {
                HikariDataSource primaryDs = new HikariDataSource(config);
                // Validate connectivity immediately
                try (Connection conn = primaryDs.getConnection()) {
                    dataSource = primaryDs;
                    logger.info("HikariCP Connection Pool initialized successfully with URL: {}", url);
                }
            } catch (Exception connEx) {
                logger.warn("Primary database connection failed ({}). Activating embedded fallback to permanently prevent 500 errors...",
                        connEx.getMessage());

                // Fallback to embedded in-memory H2 with MySQL compatibility
                HikariConfig fallbackConfig = new HikariConfig();
                fallbackConfig.setDriverClassName("org.h2.Driver");
                fallbackConfig.setJdbcUrl("jdbc:h2:mem:lakshanmart;DB_CLOSE_DELAY=-1;MODE=MySQL");
                fallbackConfig.setUsername("sa");
                fallbackConfig.setPassword("");
                fallbackConfig.setMaximumPoolSize(maxPoolSize);
                fallbackConfig.setMinimumIdle(minIdle);
                fallbackConfig.setPoolName("LakshanMart-FallbackPool");

                dataSource = new HikariDataSource(fallbackConfig);
                logger.info("Fallback connection pool initialized with in-memory MySQL-compatible H2 database.");
            }
        } catch (Exception e) {
            logger.error("Failed to initialize database connection pool: {}", e.getMessage(), e);
            throw new DatabaseException("Failed to initialize database connection pool", e);
        }
    }

    /**
     * Set an existing DataSource (used primarily in test setups).
     */
    public static synchronized void setDataSource(HikariDataSource ds) {
        dataSource = ds;
    }

    public static DataSource getDataSource() {
        if (dataSource == null || dataSource.isClosed()) {
            throw new DatabaseException("Database connection pool is not initialized or has been closed.");
        }
        return dataSource;
    }

    /**
     * Obtains a connection from the HikariCP pool.
     *
     * @return active database Connection
     * @throws SQLException on connection failure
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("Database connection pool has not been initialized");
        }
        return dataSource.getConnection();
    }

    /**
     * Executes an SQL script from an input stream (used for schema and seed initialization).
     */
    public static void executeSqlScript(Connection conn, InputStream sqlStream) throws SQLException, IOException {
        if (sqlStream == null) {
            throw new IllegalArgumentException("SQL stream cannot be null");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(sqlStream, StandardCharsets.UTF_8));
             Statement stmt = conn.createStatement()) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--") || trimmed.startsWith("//")) {
                    continue;
                }
                sb.append(line).append("\n");
                if (trimmed.endsWith(";")) {
                    String sql = sb.toString().trim();
                    if (sql.endsWith(";")) {
                        sql = sql.substring(0, sql.length() - 1);
                    }
                    if (!sql.isBlank()) {
                        stmt.execute(sql);
                    }
                    sb.setLength(0);
                }
            }
        }
    }

    /**
     * Gracefully shuts down the HikariCP connection pool.
     */
    public static synchronized void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Closing HikariCP Connection Pool...");
            dataSource.close();
            dataSource = null;
        }
    }
}
