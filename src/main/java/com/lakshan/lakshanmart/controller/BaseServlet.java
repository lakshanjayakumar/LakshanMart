package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dto.ApiResponse;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.exception.AppException;
import com.lakshan.lakshanmart.filter.AuthFilter;
import com.lakshan.lakshanmart.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Base abstract servlet providing standardized exception handling,
 * JSON response serialization, and session extraction.
 */
public abstract class BaseServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(BaseServlet.class);

    protected <T> void sendSuccess(HttpServletResponse resp, int statusCode, T data) throws IOException {
        JsonUtil.writeResponse(resp, statusCode, ApiResponse.success(data));
    }

    protected void sendError(HttpServletResponse resp, int statusCode, String code, String message) throws IOException {
        JsonUtil.writeResponse(resp, statusCode, ApiResponse.error(code, message));
    }

    protected void handleException(HttpServletResponse resp, Exception e) throws IOException {
        if (e instanceof AppException) {
            AppException appEx = (AppException) e;
            logger.warn("Application exception: [{} - {}]", appEx.getErrorCode(), appEx.getMessage());
            sendError(resp, appEx.getStatusCode(), appEx.getErrorCode(), appEx.getMessage());
        } else {
            logger.error("Unhandled server exception: {}", e.getMessage(), e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                    "An internal server error occurred. Please try again later.");
        }
    }

    protected UserResponseDTO getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            return (UserResponseDTO) session.getAttribute(AuthFilter.SESSION_USER_KEY);
        }
        return null;
    }
}
