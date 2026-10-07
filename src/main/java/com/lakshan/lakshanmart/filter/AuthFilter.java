package com.lakshan.lakshanmart.filter;

import com.lakshan.lakshanmart.dto.ApiResponse;
import com.lakshan.lakshanmart.dto.UserResponseDTO;
import com.lakshan.lakshanmart.model.Role;
import com.lakshan.lakshanmart.util.JsonUtil;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter enforcing session-based authentication and role-based access control.
 * <p>
 * Complies with Section 9: All protected servlets enforce session checks via AuthFilter.
 * </p>
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {
        "/api/v1/auth/me",
        "/api/v1/auth/logout",
        "/api/v1/admin/*",
        "/api/v1/seller/*"
})
public class AuthFilter implements Filter {

    public static final String SESSION_USER_KEY = "currentUser";

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        UserResponseDTO currentUser = (session != null)
                ? (UserResponseDTO) session.getAttribute(SESSION_USER_KEY)
                : null;

        if (currentUser == null) {
            JsonUtil.writeResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED,
                    ApiResponse.error("AUTHENTICATION_REQUIRED", "Authentication required. Please log in."));
            return;
        }

        String requestUri = httpRequest.getRequestURI();

        // Admin authorization check
        if (requestUri.contains("/api/v1/admin/")) {
            if (!Role.ADMIN.name().equalsIgnoreCase(currentUser.getRole())) {
                JsonUtil.writeResponse(httpResponse, HttpServletResponse.SC_FORBIDDEN,
                        ApiResponse.error("FORBIDDEN", "Admin access required."));
                return;
            }
        }

        // Seller authorization check
        if (requestUri.contains("/api/v1/seller/")) {
            boolean isSellerOrAdmin = Role.SELLER.name().equalsIgnoreCase(currentUser.getRole())
                    || Role.ADMIN.name().equalsIgnoreCase(currentUser.getRole());
            if (!isSellerOrAdmin) {
                JsonUtil.writeResponse(httpResponse, HttpServletResponse.SC_FORBIDDEN,
                        ApiResponse.error("FORBIDDEN", "Seller or Admin access required."));
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
