<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<header class="site-header">
    <div class="container top-bar">
        <!-- Brand & Delivery Pin -->
        <div style="display: flex; align-items: center; gap: 1rem;">
            <a href="${pageContext.request.contextPath}/home" class="brand-logo" title="LakshanMart Home">
                <span>LakshanMart</span>
                <span class="brand-badge">Marketplace</span>
            </a>

            <div class="deliver-to-box" title="Delivery Location">
                <span style="font-size: 1.15rem;">📍</span>
                <div>
                    <div class="del-label">Deliver to</div>
                    <div class="del-loc">Chennai 600032</div>
                </div>
            </div>
        </div>

        <!-- Amazon-style Category & Search Bar -->
        <form action="${pageContext.request.contextPath}/shop" method="get" class="search-box-unified">
            <select name="category" class="search-category-select">
                <option value="">All Categories</option>
                <option value="Electronics" ${param.category eq 'Electronics' or currentCategory eq 'Electronics' ? 'selected' : ''}>Electronics</option>
                <option value="Fashion" ${param.category eq 'Fashion' or currentCategory eq 'Fashion' ? 'selected' : ''}>Fashion</option>
                <option value="Home & Kitchen" ${param.category eq 'Home & Kitchen' or currentCategory eq 'Home & Kitchen' ? 'selected' : ''}>Home & Kitchen</option>
                <option value="Books" ${param.category eq 'Books' or currentCategory eq 'Books' ? 'selected' : ''}>Books</option>
                <option value="Sports & Fitness" ${param.category eq 'Sports & Fitness' or currentCategory eq 'Sports & Fitness' ? 'selected' : ''}>Sports & Fitness</option>
            </select>
            <input type="text" name="q" class="search-input-unified"
                   placeholder="Search products, brands and deals in ₹ INR..."
                   value="<c:out value="${param.q}" />" />
            <button type="submit" class="search-submit-btn" aria-label="Search">🔍</button>
        </form>

        <!-- Navigation Actions -->
        <div class="nav-actions">
            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <a href="${pageContext.request.contextPath}/profile" class="nav-link" style="flex-direction: column; align-items: flex-start; gap: 0;">
                        <span style="font-size: 0.7rem; color: var(--text-muted); line-height: 1;">Hello, <c:out value="${sessionScope.currentUser.name}" /></span>
                        <strong style="font-size: 0.875rem; color: var(--text-primary);">Account & Lists ▾</strong>
                    </a>

                    <c:choose>
                        <c:when test="${sessionScope.currentUser.role eq 'ADMIN'}">
                            <a href="${pageContext.request.contextPath}/admin" class="btn btn-outline btn-sm" style="font-weight: 700; border-color: var(--primary);">Admin</a>
                            <a href="${pageContext.request.contextPath}/seller/dashboard" class="nav-link" style="font-size: 0.85rem; font-weight: 600;">Seller Hub</a>
                        </c:when>
                        <c:when test="${sessionScope.currentUser.role eq 'SELLER'}">
                            <a href="${pageContext.request.contextPath}/seller/dashboard" class="btn btn-primary btn-sm" style="font-weight: 700;">Seller Hub</a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/seller/onboard" class="nav-link" style="font-size: 0.85rem; font-weight: 600; color: var(--accent);">Become a Seller</a>
                        </c:otherwise>
                    </c:choose>

                    <a href="${pageContext.request.contextPath}/orders" class="nav-link" style="flex-direction: column; align-items: flex-start; gap: 0;">
                        <span style="font-size: 0.7rem; color: var(--text-muted); line-height: 1;">Returns</span>
                        <strong style="font-size: 0.875rem; color: var(--text-primary);">& Orders</strong>
                    </a>

                    <a href="${pageContext.request.contextPath}/logout" class="nav-link" style="color: var(--danger); font-size: 0.85rem;">Logout</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login" class="nav-link" style="flex-direction: column; align-items: flex-start; gap: 0;">
                        <span style="font-size: 0.7rem; color: var(--text-muted); line-height: 1;">Hello, Sign in</span>
                        <strong style="font-size: 0.875rem; color: var(--text-primary);">Account & Lists ▾</strong>
                    </a>

                    <a href="${pageContext.request.contextPath}/seller/onboard" class="nav-link" style="font-size: 0.85rem; font-weight: 600; color: var(--accent);">Sell on LakshanMart</a>

                    <a href="${pageContext.request.contextPath}/orders" class="nav-link" style="flex-direction: column; align-items: flex-start; gap: 0;">
                        <span style="font-size: 0.7rem; color: var(--text-muted); line-height: 1;">Returns</span>
                        <strong style="font-size: 0.875rem; color: var(--text-primary);">& Orders</strong>
                    </a>

                    <a href="${pageContext.request.contextPath}/register" class="btn btn-primary btn-sm">Register</a>
                </c:otherwise>
            </c:choose>

            <!-- Cart Button -->
            <a href="${pageContext.request.contextPath}/cart" class="cart-btn" title="View Cart">
                <span>🛒 Cart</span>
                <span class="cart-count" style="display: ${cartItemCount > 0 ? 'flex' : 'none'};">
                    <c:out value="${cartItemCount > 0 ? cartItemCount : 0}" />
                </span>
            </a>
        </div>
    </div>

    <!-- Category Strip -->
    <nav class="category-nav">
        <div class="container">
            <ul class="category-list">
                <li class="category-item ${empty currentCategory ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/shop">☰ All Products</a>
                </li>
                <li class="category-item ${currentCategory eq 'Electronics' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/shop?category=Electronics">💻 Electronics</a>
                </li>
                <li class="category-item ${currentCategory eq 'Fashion' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/shop?category=Fashion">👔 Fashion</a>
                </li>
                <li class="category-item ${currentCategory eq 'Home & Kitchen' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/shop?category=Home%20%26%20Kitchen">🍳 Home & Kitchen</a>
                </li>
                <li class="category-item ${currentCategory eq 'Books' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/shop?category=Books">📚 Books</a>
                </li>
                <li class="category-item ${currentCategory eq 'Sports & Fitness' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/shop?category=Sports%20%26%20Fitness">🏋️ Sports & Fitness</a>
                </li>
                <li class="category-item">
                    <a href="${pageContext.request.contextPath}/seller/onboard" style="color: #fbbf24; font-weight: 700;">🚀 Sell</a>
                </li>
            </ul>
        </div>
    </nav>
</header>
