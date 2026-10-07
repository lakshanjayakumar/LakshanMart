package com.lakshan.lakshanmart.controller;

import com.lakshan.lakshanmart.dto.ProductResponseDTO;
import com.lakshan.lakshanmart.service.ProductService;
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductServletTest {

    @Mock
    private ProductService productService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private ProductServlet productServlet;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        productServlet = new ProductServlet(productService);
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    @DisplayName("GET /api/v1/products returns product list")
    void testListProducts() throws Exception {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("category")).thenReturn(null);
        when(request.getParameter("offset")).thenReturn("0");
        when(request.getParameter("limit")).thenReturn("10");

        ProductResponseDTO p = new ProductResponseDTO(1L, 2L, "Product A", "Desc",
                new BigDecimal("49.99"), 10, "Electronics", null, new Timestamp(System.currentTimeMillis()));
        when(productService.listProducts(null, 0, 10)).thenReturn(List.of(p));

        productServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String json = responseWriter.toString();
        assertTrue(json.contains("\"success\":true"));
        assertTrue(json.contains("Product A"));
    }

    @Test
    @DisplayName("GET /api/v1/products/10 returns product details")
    void testGetProductDetails() throws Exception {
        when(request.getPathInfo()).thenReturn("/10");

        ProductResponseDTO p = new ProductResponseDTO(10L, 2L, "Product 10", "Desc",
                new BigDecimal("99.99"), 5, "Electronics", null, new Timestamp(System.currentTimeMillis()));
        when(productService.getProductById(10L)).thenReturn(p);

        productServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String json = responseWriter.toString();
        assertTrue(json.contains("\"success\":true"));
        assertTrue(json.contains("Product 10"));
    }
}
