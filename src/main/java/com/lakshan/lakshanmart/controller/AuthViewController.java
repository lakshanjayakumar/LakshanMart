package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dao.UserDAOImpl;
import com.lakshan.lakshanmart.dto.UserLoginRequest;
import com.lakshan.lakshanmart.dto.UserRegisterRequest;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.exception.AppException;
import com.lakshan.lakshanmart.filter.AuthFilter;
import com.lakshan.lakshanmart.model.Role;
import com.lakshan.lakshanmart.service.UserService;
import com.lakshan.lakshanmart.service.UserServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Controller handling standard browser form navigation for Authentication:
 * Login (/login), Register (/register), and Logout (/logout).
 */
@WebServlet(name = "AuthViewController", urlPatterns = {"/login", "/register", "/logout"})
public class AuthViewController extends BaseServlet {

    private final UserService userService;

    public AuthViewController() {
        this(new UserServiceImpl(new UserDAOImpl()));
    }

    public AuthViewController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();

        if ("/logout".equals(servletPath)) {
            handleLogout(req, resp);
            return;
        }

        UserResponseDTO currentUser = getCurrentUser(req);
        if (currentUser != null) {
            if (Role.ADMIN.name().equalsIgnoreCase(currentUser.getRole())) {
                resp.sendRedirect(req.getContextPath() + "/admin");
            } else {
                resp.sendRedirect(req.getContextPath() + "/home");
            }
            return;
        }

        if ("/register".equals(servletPath)) {
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();

        if ("/logout".equals(servletPath)) {
            handleLogout(req, resp);
        } else if ("/register".equals(servletPath)) {
            handleRegister(req, resp);
        } else {
            handleLogin(req, resp);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String redirect = req.getParameter("redirect");

        try {
            UserLoginRequest loginReq = new UserLoginRequest(email, password);
            UserResponseDTO user = userService.authenticate(loginReq);

            HttpSession session = req.getSession(true);
            try {
                req.changeSessionId(); // Prevent session fixation attacks
            } catch (IllegalStateException ignored) {}

            session.setAttribute(AuthFilter.SESSION_USER_KEY, user);

            if (redirect != null && !redirect.trim().isEmpty() && !redirect.contains("/login") && !redirect.contains("/logout")) {
                resp.sendRedirect(redirect);
            } else if (Role.ADMIN.name().equalsIgnoreCase(user.getRole())) {
                resp.sendRedirect(req.getContextPath() + "/admin");
            } else {
                resp.sendRedirect(req.getContextPath() + "/home");
            }
        } catch (AppException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "An error occurred during sign-in. Please try again.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String role = req.getParameter("role");

        try {
            UserRegisterRequest registerReq = new UserRegisterRequest(name, email, password, role);
            userService.register(registerReq);

            // Auto-login upon successful registration
            UserResponseDTO user = userService.authenticate(new UserLoginRequest(email, password));
            HttpSession session = req.getSession(true);
            try {
                req.changeSessionId();
            } catch (IllegalStateException ignored) {}

            session.setAttribute(AuthFilter.SESSION_USER_KEY, user);
            resp.sendRedirect(req.getContextPath() + "/home");
        } catch (AppException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Unable to create account. Please verify your details.");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
