package com.lakshan.lakshanmart.listener;

import com.lakshan.lakshanmart.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Application context listener managing the lifecycle of HikariCP connection pool
 * and ensuring database schema readiness.
 *
 * <p>Strictly fulfills Engineering Rule 5: Connection pool lifecycle owned by a single
 * ServletContextListener.</p>
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initializing LakshanMart Application Context...");
        ServletContext context = sce.getServletContext();

        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                props.load(in);
                logger.info("Loaded application.properties from classpath");
            } else {
                logger.warn("application.properties not found on classpath, using defaults");
            }
        } catch (Exception e) {
            logger.error("Error reading application.properties: {}", e.getMessage(), e);
        }

        // Initialize HikariCP pool
        DatabaseUtil.init(props);
        context.setAttribute("dataSource", DatabaseUtil.getDataSource());

        // Check and bootstrap schema if needed
        bootstrapDatabaseIfEmpty(context);

        logger.info("LakshanMart Application Context initialized successfully.");
    }

    private void bootstrapDatabaseIfEmpty(ServletContext context) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            boolean tablesExist = checkTablesExist(conn);

            if (!tablesExist) {
                logger.info("Database tables not found. Executing schema.sql bootstrap...");
                InputStream schemaStream = getResourceStream(context, "db/schema.sql");
                if (schemaStream != null) {
                    try (InputStream toCloseSchema = schemaStream) {
                        DatabaseUtil.executeSqlScript(conn, toCloseSchema);
                        logger.info("Schema executed successfully.");
                    }
                } else {
                    logger.error("Unable to locate db/schema.sql on classpath or in webapp context!");
                }

                logger.info("Executing data.sql bootstrap...");
                InputStream dataStream = getResourceStream(context, "db/data.sql");
                if (dataStream == null) {
                    dataStream = getResourceStream(context, "db/seed.sql");
                }
                if (dataStream != null) {
                    try (InputStream toCloseData = dataStream) {
                        DatabaseUtil.executeSqlScript(conn, toCloseData);
                        logger.info("Sample seed data executed successfully.");
                    }
                } else {
                    logger.error("Unable to locate db/data.sql or db/seed.sql on classpath or in webapp context!");
                }
            } else {
                logger.info("Database tables already exist. Skipping bootstrap.");
            }
        } catch (SQLException e) {
            logger.warn("Could not check/bootstrap schema (may already be initialized): {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Failed during database bootstrap: {}", e.getMessage(), e);
        }
    }

    private boolean checkTablesExist(Connection conn) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products")) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private InputStream getResourceStream(ServletContext context, String path) {
        InputStream is = null;
        if (context != null) {
            is = context.getResourceAsStream("/WEB-INF/classes/" + path);
            if (is != null) return is;
            is = context.getResourceAsStream("/" + path);
            if (is != null) return is;
        }
        is = getClass().getClassLoader().getResourceAsStream(path);
        if (is != null) return is;
        is = getClass().getResourceAsStream("/" + path);
        if (is != null) return is;
        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        return is;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Destroying LakshanMart Application Context...");
        DatabaseUtil.close();
        logger.info("LakshanMart Application Context destroyed cleanly.");
    }
}
