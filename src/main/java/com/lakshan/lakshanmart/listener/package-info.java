/**
 * Listener layer containing application lifecycle listeners.
 *
 * <p>Engineering Rule: The HikariCP connection pool lifecycle is owned by a single
 * {@link jakarta.servlet.ServletContextListener}. Manual {@code DriverManager.getConnection()}
 * calls outside this listener pattern are strictly prohibited.</p>
 */
package com.lakshan.lakshanmart.listener;
