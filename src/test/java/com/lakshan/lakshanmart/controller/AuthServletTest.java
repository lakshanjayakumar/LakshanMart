package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dto.UserLoginRequest;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.filter.AuthFilter;
import com.lakshan.lakshanmart.service.UserService;
import com.lakshan.lakshanmart.util.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServletTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private AuthServlet authServlet;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        authServlet = new AuthServlet(userService);
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login succeeds and sets session user")
    void testLoginSuccess() throws Exception {
        when(request.getPathInfo()).thenReturn("/login");

        UserLoginRequest loginReq = new UserLoginRequest("user@example.com", "Password@123");
        BufferedReader reader = new BufferedReader(new StringReader(JsonUtil.toJson(loginReq)));
        when(request.getReader()).thenReturn(reader);

        UserResponseDTO userDTO = new UserResponseDTO(1L, "User", "user@example.com", "BUYER", new Timestamp(System.currentTimeMillis()));
        when(userService.authenticate(any(UserLoginRequest.class))).thenReturn(userDTO);
        when(request.getSession(true)).thenReturn(session);

        authServlet.doPost(request, response);

        verify(request).changeSessionId();
        verify(session).setMaxInactiveInterval(30 * 60);
        verify(session).setAttribute(AuthFilter.SESSION_USER_KEY, userDTO);
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("\"success\":true"));
        assertTrue(jsonResponse.contains("Login successful"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/logout invalidates session")
    void testLogout() throws Exception {
        when(request.getPathInfo()).thenReturn("/logout");
        when(request.getSession(false)).thenReturn(session);

        authServlet.doPost(request, response);

        verify(session).invalidate();
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("\"success\":true"));
    }
}
