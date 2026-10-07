<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LakshanMart - Discover Great Products from Trusted Sellers</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <main class="container">
        <!-- Hero Section -->
        <section class="hero-banner">
            <div class="hero-content">
                <span class="hero-tagline">R2025 Capstone Marketplace</span>
                <h1 class="hero-title">Experience Modern Multi-Seller Commerce</h1>
                <p class="hero-subtitle">Explore high-quality products across Electronics, Fashion, Home, and Books directly from independent merchants.</p>
                <div class="flex gap-4">
                    <a href="${pageContext.request.contextPath}/shop" class="btn btn-primary">Shop All Products</a>
                    <a href="${pageContext.request.contextPath}/register" class="btn btn-outline" style="color: #ffffff; border-color: rgba(255,255,255,0.4);">Become a Seller</a>
                </div>
            </div>
            <div style="font-size: 6rem; opacity: 0.85;" class="hero-icon">🛍️</div>
        </section>

        <!-- Category Highlights -->
        <section style="margin: 2.5rem 0;">
            <div class="section-header">
                <h2 class="section-title">Explore by Category</h2>
                <a href="${pageContext.request.contextPath}/shop" class="nav-link" style="color: var(--primary); font-weight: 600;">View All &rarr;</a>
            </div>

            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 1rem;">
                <a href="${pageContext.request.contextPath}/shop?category=Electronics" class="card" style="text-align: center; padding: 1.25rem; transition: var(--transition);">
                    <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">🎧</div>
                    <strong>Electronics</strong>
                    <div style="font-size: 0.8rem; color: var(--text-muted); margin-top: 2px;">Audio, Monitors, Keyboards</div>
                </a>
                <a href="${pageContext.request.contextPath}/shop?category=Fashion" class="card" style="text-align: center; padding: 1.25rem; transition: var(--transition);">
                    <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">👔</div>
                    <strong>Fashion</strong>
                    <div style="font-size: 0.8rem; color: var(--text-muted); margin-top: 2px;">Apparel, Shoes, Watches</div>
                </a>
                <a href="${pageContext.request.contextPath}/shop?category=Home%20%26%20Kitchen" class="card" style="text-align: center; padding: 1.25rem; transition: var(--transition);">
                    <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">☕</div>
                    <strong>Home & Kitchen</strong>
                    <div style="font-size: 0.8rem; color: var(--text-muted); margin-top: 2px;">Coffee, Bottles, Lamps</div>
                </a>
                <a href="${pageContext.request.contextPath}/shop?category=Books" class="card" style="text-align: center; padding: 1.25rem; transition: var(--transition);">
                    <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">📚</div>
                    <strong>Books</strong>
                    <div style="font-size: 0.8rem; color: var(--text-muted); margin-top: 2px;">Engineering, Journals</div>
                </a>
                <a href="${pageContext.request.contextPath}/shop?category=Sports%20%26%20Fitness" class="card" style="text-align: center; padding: 1.25rem; transition: var(--transition);">
                    <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">🏋️</div>
                    <strong>Fitness</strong>
                    <div style="font-size: 0.8rem; color: var(--text-muted); margin-top: 2px;">Yoga Mats, Weights</div>
                </a>
            </div>
        </section>

        <!-- Featured Products -->
        <section>
            <div class="section-header">
                <h2 class="section-title">Trending & Featured Items</h2>
                <a href="${pageContext.request.contextPath}/shop" class="nav-link" style="color: var(--primary); font-weight: 600;">Browse Catalog &rarr;</a>
            </div>

            <div class="product-grid">
                <c:forEach var="p" items="${featuredProducts}">
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
                                <a href="${pageContext.request.contextPath}/product?id=${p.id}" class="btn btn-outline btn-sm" style="flex: 1;">Details</a>
                                <c:if test="${p.stockQty > 0}">
                                    <button type="button" class="btn btn-add-cart btn-sm" onclick="LakshanMart.addToCart(${p.id}, 1)">+ Cart</button>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </section>
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
