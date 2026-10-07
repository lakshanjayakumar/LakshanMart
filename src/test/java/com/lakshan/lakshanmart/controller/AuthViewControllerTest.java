package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dto.UserLoginRequest;
import com.lakshan.lakshanmart.dto.UserRegisterRequest;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.exception.AuthenticationException;
import com.lakshan.lakshanmart.filter.AuthFilter;
import com.lakshan.lakshanmart.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthViewControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    private AuthViewController authViewController;

    @BeforeEach
    void setUp() {
        authViewController = new AuthViewController(userService);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
        when(request.getContextPath()).thenReturn("/LakshanMart");
        when(request.getSession(true)).thenReturn(session);
    }

    @Test
    @DisplayName("POST /login with valid buyer credentials redirects to /home")
    void testLoginBuyerSuccess() throws Exception {
        when(request.getServletPath()).thenReturn("/login");
        when(request.getParameter("email")).thenReturn("bob@lakshanmart.com");
        when(request.getParameter("password")).thenReturn("Password@123");

        UserResponseDTO user = new UserResponseDTO(2L, "Bob", "bob@lakshanmart.com", "BUYER", new Timestamp(System.currentTimeMillis()));
        when(userService.authenticate(any(UserLoginRequest.class))).thenReturn(user);

        authViewController.doPost(request, response);

        verify(session).setAttribute(eq(AuthFilter.SESSION_USER_KEY), eq(user));
        verify(response).sendRedirect("/LakshanMart/home");
    }

    @Test
    @DisplayName("POST /login with valid admin credentials redirects to /admin")
    void testLoginAdminSuccess() throws Exception {
        when(request.getServletPath()).thenReturn("/login");
        when(request.getParameter("email")).thenReturn("admin@lakshanmart.com");
        when(request.getParameter("password")).thenReturn("Admin@123");

        UserResponseDTO admin = new UserResponseDTO(1L, "Admin", "admin@lakshanmart.com", "ADMIN", new Timestamp(System.currentTimeMillis()));
        when(userService.authenticate(any(UserLoginRequest.class))).thenReturn(admin);

        authViewController.doPost(request, response);

        verify(session).setAttribute(eq(AuthFilter.SESSION_USER_KEY), eq(admin));
        verify(response).sendRedirect("/LakshanMart/admin");
    }

    @Test
    @DisplayName("POST /login with bad credentials sets error and returns to login view")
    void testLoginFailure() throws Exception {
        when(request.getServletPath()).thenReturn("/login");
        when(request.getParameter("email")).thenReturn("bob@lakshanmart.com");
        when(request.getParameter("password")).thenReturn("WrongPassword");

        when(userService.authenticate(any(UserLoginRequest.class)))
                .thenThrow(new AuthenticationException("Invalid email or password."));

        authViewController.doPost(request, response);

        verify(request).setAttribute(eq("errorMessage"), eq("Invalid email or password."));
        verify(request).getRequestDispatcher("/WEB-INF/views/auth/login.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("POST /register creates user, establishes session, and redirects to /home")
    void testRegisterSuccess() throws Exception {
        when(request.getServletPath()).thenReturn("/register");
        when(request.getParameter("name")).thenReturn("New User");
        when(request.getParameter("email")).thenReturn("new@example.com");
        when(request.getParameter("password")).thenReturn("Secret@123");
        when(request.getParameter("role")).thenReturn("BUYER");

        UserResponseDTO user = new UserResponseDTO(10L, "New User", "new@example.com", "BUYER", new Timestamp(System.currentTimeMillis()));
        when(userService.register(any(UserRegisterRequest.class))).thenReturn(user);
        when(userService.authenticate(any(UserLoginRequest.class))).thenReturn(user);

        authViewController.doPost(request, response);

        verify(session).setAttribute(eq(AuthFilter.SESSION_USER_KEY), eq(user));
        verify(response).sendRedirect("/LakshanMart/home");
    }

    @Test
    @DisplayName("GET /logout invalidates session and redirects to /login")
    void testLogoutSuccess() throws Exception {
        when(request.getServletPath()).thenReturn("/logout");
        when(request.getSession(false)).thenReturn(session);

        authViewController.doGet(request, response);

        verify(session).invalidate();
        verify(response).sendRedirect("/LakshanMart/login");
    }
}
