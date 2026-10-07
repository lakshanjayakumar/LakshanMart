package com.lakshan.lakshanmart.filter;

import org.slf4j.MDC;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Filter generating a unique request ID per incoming HTTP request
 * and attaching it to the SLF4J MDC context for structured logging (Section 18.2).
 */
@WebFilter(filterName = "MdcLoggingFilter", urlPatterns = "/*")
public class MdcLoggingFilter implements Filter {

    private static final String MDC_REQUEST_ID_KEY = "requestId";
    private static final String HEADER_REQUEST_ID = "X-Request-Id";

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Use incoming X-Request-Id header if present, otherwise generate a random UUID
        String requestId = httpRequest.getHeader(HEADER_REQUEST_ID);
        if (requestId == null || requestId.trim().isEmpty()) {
            requestId = UUID.randomUUID().toString().substring(0, 8);
        }

        MDC.put(MDC_REQUEST_ID_KEY, requestId);
        httpResponse.setHeader(HEADER_REQUEST_ID, requestId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_REQUEST_ID_KEY);
        }
    }

    @Override
    public void destroy() {
    }
}
