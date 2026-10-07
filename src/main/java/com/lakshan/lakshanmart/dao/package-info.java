/**
 * Data Access Object (DAO) layer containing interfaces and JDBC implementations.
 *
 * <p>Engineering Rules:
 * <ul>
 *   <li>All SQL queries and database operations reside exclusively in this layer.</li>
 *   <li>All queries must use {@link java.sql.PreparedStatement}; string concatenation is strictly prohibited.</li>
 *   <li>Every database resource (Connection, PreparedStatement, ResultSet) must use try-with-resources.</li>
 * </ul>
 * </p>
 */
package com.lakshan.lakshanmart.dao;
