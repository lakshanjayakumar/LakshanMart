package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dao.UserDAOImpl;
import com.lakshan.lakshanmart.dto.UserLoginRequest;
import com.lakshan.lakshanmart.dto.UserRegisterRequest;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.filter.AuthFilter;
import com.lakshan.lakshanmart.service.UserService;
import com.lakshan.lakshanmart.service.UserServiceImpl;
import com.lakshan.lakshanmart.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * Servlet handling user registration, authentication, session regeneration, and logout.
 * Endpoints: /api/v1/auth/*
 */
@WebServlet(name = "AuthServlet", urlPatterns = "/api/v1/auth/*")
public class AuthServlet extends BaseServlet {

    private final UserService userService;

    public AuthServlet() {
        this.userService = new UserServiceImpl(new UserDAOImpl());
    }

    // Constructor injection for unit testing
    public AuthServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "";
        }

        try {
            switch (pathInfo) {
                case "/register":
                    handleRegister(req, resp);
                    break;
                case "/login":
                    handleLogin(req, resp);
                    break;
                case "/logout":
                    handleLogout(req, resp);
                    break;
                default:
                    sendError(resp, HttpServletResponse.SC_NOT_FOUND, "ENDPOINT_NOT_FOUND",
                            "Auth endpoint not found: " + pathInfo);
                    break;
            }
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if ("/me".equals(pathInfo)) {
            UserResponseDTO currentUser = getCurrentUser(req);
            if (currentUser != null) {
                sendSuccess(resp, HttpServletResponse.SC_OK, currentUser);
            } else {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "NOT_LOGGED_IN", "No active session found.");
            }
        } else {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "ENDPOINT_NOT_FOUND", "Endpoint not found.");
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserRegisterRequest registerReq = JsonUtil.fromJson(req.getReader(), UserRegisterRequest.class);
        UserResponseDTO createdUser = userService.register(registerReq);
        sendSuccess(resp, HttpServletResponse.SC_CREATED, createdUser);
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserLoginRequest loginReq = JsonUtil.fromJson(req.getReader(), UserLoginRequest.class);
        UserResponseDTO user = userService.authenticate(loginReq);

        // Security requirement 3: Regenerate session ID on login to prevent fixation attacks
        HttpSession session = req.getSession(true);
        req.changeSessionId();
        session.setMaxInactiveInterval(30 * 60); // 30 minutes explicit timeout
        session.setAttribute(AuthFilter.SESSION_USER_KEY, user);

        sendSuccess(resp, HttpServletResponse.SC_OK, Map.of(
                "message", "Login successful",
                "user", user
        ));
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        sendSuccess(resp, HttpServletResponse.SC_OK, Map.of("message", "Logged out successfully"));
    }
}
