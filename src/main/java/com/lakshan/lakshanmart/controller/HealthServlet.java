package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.util.DatabaseUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Health check endpoint verifying service and database readiness.
 * Complies with Section 18.1: GET /api/v1/health returns { "status": "UP", "db": "UP" }.
 */
@WebServlet(name = "HealthServlet", urlPatterns = "/api/v1/health")
public class HealthServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, String> health = new LinkedHashMap<>();
        health.put("status", "UP");

        try (Connection conn = DatabaseUtil.getConnection()) {
            if (conn.isValid(2)) {
                health.put("db", "UP");
            } else {
                health.put("db", "DOWN");
            }
        } catch (Exception e) {
            health.put("db", "DOWN");
            health.put("status", "DEGRADED");
            sendError(resp, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "DATABASE_UNAVAILABLE", "Database connection is DOWN");
            return;
        }

        sendSuccess(resp, HttpServletResponse.SC_OK, health);
    }
}
