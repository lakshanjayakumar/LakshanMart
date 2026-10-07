<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
        <c:choose>
            <c:when test="${not empty currentCategory}"><c:out value="${currentCategory}" /> | LakshanMart</c:when>
            <c:when test="${not empty searchQuery}">Search: <c:out value="${searchQuery}" /> | LakshanMart</c:when>
            <c:otherwise>All Products | LakshanMart</c:otherwise>
        </c:choose>
    </title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <main class="container" style="padding-top: 2rem; padding-bottom: 3rem;">
        <div style="margin-bottom: 2rem;">
            <div style="font-size: 0.875rem; color: var(--text-muted); margin-bottom: 0.5rem;">
                <a href="${pageContext.request.contextPath}/home">Home</a> &rsaquo;
                <c:choose>
                    <c:when test="${not empty currentCategory}">
                        <span><c:out value="${currentCategory}" /></span>
                    </c:when>
                    <c:when test="${not empty searchQuery}">
                        <span>Search Results</span>
                    </c:when>
                    <c:otherwise>
                        <span>Catalog</span>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="flex items-center justify-between">
                <h1 style="font-size: 1.75rem; font-weight: 800;">
                    <c:choose>
                        <c:when test="${not empty searchQuery}">Results for "<c:out value="${searchQuery}" />"</c:when>
                        <c:when test="${not empty currentCategory}"><c:out value="${currentCategory}" /></c:when>
                        <c:otherwise>Explore All Marketplace Listings</c:otherwise>
                    </c:choose>
                </h1>
                <span style="font-size: 0.875rem; color: var(--text-secondary);">
                    Showing <strong>${products.size()}</strong> items
                </span>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty products}">
                <div class="card" style="text-align: center; padding: 4rem 2rem;">
                    <div style="font-size: 3.5rem; margin-bottom: 1rem;">🔍</div>
                    <h3 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 0.5rem;">No products found</h3>
                    <p style="color: var(--text-muted); margin-bottom: 1.5rem;">Try modifying your keyword search or selecting a different category.</p>
                    <a href="${pageContext.request.contextPath}/shop" class="btn btn-primary">Browse All Products</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="product-grid">
                    <c:forEach var="p" items="${products}">
                        <div class="product-card">
                            <div class="product-image-wrap">
                                <c:choose>
                                    <c:when test="${not empty p.imageUrl}">
                                        <img src="<c:out value="${p.imageUrl}" />" alt="<c:out value="${p.name}" />" class="product-image" loading="lazy" />
                                    </c:when>
                                    <c:otherwise>
                                        <div style="width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; background: #e2e8f0; font-size: 2rem;">📦</div>
                                    </c:otherwise>
                                </c:choose>
                                <span class="product-badge"><c:out value="${p.category}" /></span>
                            </div>

                            <div class="product-details">
                                <span class="product-category"><c:out value="${p.category}" /></span>
                                <a href="${pageContext.request.contextPath}/product?id=${p.id}" class="product-name">
                                    <c:out value="${p.name}" />
                                </a>

                                <div class="product-price-row">
                                    <span class="product-price">₹<fmt:formatNumber value="${p.price}" minFractionDigits="2" maxFractionDigits="2" /></span>
                                    <c:choose>
                                        <c:when test="${p.stockQty > 0}">
                                            <span class="product-stock">In Stock (${p.stockQty})</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="product-stock out-of-stock">Out of Stock</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <div class="flex gap-2" style="margin-top: 0.5rem;">
                                    <a href="${pageContext.request.contextPath}/product?id=${p.id}" class="btn btn-outline btn-sm" style="flex: 1;">View</a>
                                    <c:if test="${p.stockQty > 0}">
                                        <button type="button" class="btn btn-add-cart btn-sm" onclick="LakshanMart.addToCart(${p.id}, 1)">+ Cart</button>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </main>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />
    <jsp:include page="/WEB-INF/views/common/chat-widget.jsp" />

    <script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            LakshanMart.init('${pageContext.request.contextPath}');
        });
    </script>
</body>
</html>
