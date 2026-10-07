package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dao.UserDAOImpl;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.model.Role;
import com.lakshan.lakshanmart.service.UserService;
import com.lakshan.lakshanmart.service.UserServiceImpl;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * REST endpoint for administrative user management.
 * Protected for ADMIN role (Section 1 F7).
 * Endpoint: /api/v1/admin/users
 */
@WebServlet(name = "AdminUserServlet", urlPatterns = "/api/v1/admin/users")
public class AdminUserServlet extends BaseServlet {

    private final UserService userService;

    public AdminUserServlet() {
        this.userService = new UserServiceImpl(new UserDAOImpl());
    }

    public AdminUserServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserResponseDTO currentUser = getCurrentUser(req);
            if (currentUser == null || !Role.ADMIN.name().equalsIgnoreCase(currentUser.getRole())) {
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN", "Admin access required.");
                return;
            }

            List<UserResponseDTO> users = userService.getAllUsers();
            sendSuccess(resp, HttpServletResponse.SC_OK, users);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }
}
