<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Your Account | LakshanMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .profile-grid {
            display: grid;
            grid-template-columns: 320px 1fr;
            gap: 2rem;
            margin-top: 1.5rem;
        }
        .user-card {
            background: #ffffff;
            border: 1px solid var(--border-color);
            border-radius: var(--radius-md);
            padding: 1.75rem;
            box-shadow: var(--shadow-sm);
        }
        .user-avatar-circle {
            width: 72px;
            height: 72px;
            border-radius: var(--radius-full);
            background: linear-gradient(135deg, #131921, #232f3e);
            color: #ffffff;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 2rem;
            font-weight: 700;
            margin-bottom: 1rem;
        }
        .info-row {
            display: flex;
            justify-content: space-between;
            padding: 0.65rem 0;
            border-bottom: 1px solid #f1f5f9;
            font-size: 0.875rem;
        }
        .info-label {
            color: var(--text-muted);
            font-weight: 500;
        }
        .info-val {
            font-weight: 600;
            color: var(--text-primary);
        }
        @media (max-width: 850px) {
            .profile-grid { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>

    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <main class="container" style="padding-top: 2rem; padding-bottom: 3.5rem;">
        <div style="font-size: 0.875rem; color: var(--text-muted); margin-bottom: 1rem;">
            <a href="${pageContext.request.contextPath}/">Home</a> &rsaquo; <span>Your Account</span>
        </div>

        <h1 style="font-size: 1.875rem; font-weight: 800; margin-bottom: 0.5rem;">Your Account & Order History</h1>
        <p style="color: var(--text-muted); font-size: 0.95rem;">Manage your profile settings, view active orders, and track deliveries</p>

        <div class="profile-grid">
            <!-- Left Column: User Profile Card -->
            <div>
                <div class="user-card">
                    <div class="user-avatar-circle">
                        ${user.name.substring(0, 1).toUpperCase()}
                    </div>
                    <h2 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 0.25rem;"><c:out value="${user.name}" /></h2>
                    <span class="badge-status" style="background-color: #fef3c7; color: #92400e; margin-bottom: 1.25rem;">
                        Verified <c:out value="${user.role}" />
                    </span>

                    <div style="margin-top: 1rem;">
                        <div class="info-row">
                            <span class="info-label">Email</span>
                            <span class="info-val"><c:out value="${user.email}" /></span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Account ID</span>
                            <span class="info-val">#<c:out value="${user.id}" /></span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Member Since</span>
                            <span class="info-val"><fmt:formatDate value="${user.createdAt}" pattern="MMM yyyy" /></span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Total Orders</span>
                            <span class="info-val"><c:out value="${totalOrders}" /></span>
                        </div>
                    </div>

                    <div style="margin-top: 1.75rem; display: flex; flex-direction: column; gap: 0.5rem;">
                        <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline btn-sm" style="width: 100%;">
                            🛒 View Shopping Cart
                        </a>
                        <c:choose>
                            <c:when test="${user.role eq 'ADMIN'}">
                                <a href="${pageContext.request.contextPath}/admin" class="btn btn-outline btn-sm" style="width: 100%; border-color: var(--primary); color: var(--primary); font-weight: 700;">
                                    ⚙️ Administrator Dashboard
                                </a>
                                <a href="${pageContext.request.contextPath}/seller/dashboard" class="btn btn-outline btn-sm" style="width: 100%; border-color: #2563eb; color: #2563eb; font-weight: 600;">
                                    📦 Seller Central Hub
                                </a>
                            </c:when>
                            <c:when test="${user.role eq 'SELLER'}">
                                <a href="${pageContext.request.contextPath}/seller/dashboard" class="btn btn-primary btn-sm" style="width: 100%; font-weight: 700;">
                                    📦 Open Seller Central Hub
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/seller/onboard" class="btn btn-primary btn-sm" style="width: 100%; font-weight: 700;">
                                    🚀 Become a Marketplace Seller
                                </a>
                            </c:otherwise>
                        </c:choose>
                        <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline btn-sm" style="width: 100%; color: var(--danger); border-color: #fee2e2;">
                            Sign Out
                        </a>
                    </div>
                </div>

                <!-- Amazon/Flipkart Prime/Plus Promo Card -->
                <div class="card" style="margin-top: 1.5rem; background: linear-gradient(135deg, #f8fafc, #eff6ff); border: 1px solid #bfdbfe;">
                    <div style="display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.5rem;">
                        <span style="font-size: 1.25rem;">⚡</span>
                        <strong style="color: var(--primary);">LakshanMart Assured</strong>
                    </div>
                    <p style="font-size: 0.8125rem; color: var(--text-secondary); line-height: 1.4;">
                        Enjoy fast & free delivery on eligible marketplace items with authentic buyer guarantee and hassle-free returns.
                    </p>
                </div>
            </div>

            <!-- Right Column: Order History -->
            <div>
                <div class="card" style="padding: 1.5rem;">
                    <div class="flex items-center justify-between" style="margin-bottom: 1.25rem;">
                        <h2 style="font-size: 1.25rem; font-weight: 700;">Order History (${orders.size()})</h2>
                        <a href="${pageContext.request.contextPath}/shop" class="btn btn-primary btn-sm">Shop Today's Deals</a>
                    </div>

                    <c:choose>
                        <c:when test="${empty orders}">
                            <div style="text-align: center; padding: 3rem 1rem;">
                                <div style="font-size: 3rem; margin-bottom: 0.75rem;">📦</div>
                                <h3 style="font-size: 1.125rem; font-weight: 700; margin-bottom: 0.35rem;">No orders placed yet</h3>
                                <p style="color: var(--text-muted); font-size: 0.875rem; margin-bottom: 1.25rem;">Explore top trending products in Electronics, Fashion, and more.</p>
                                <a href="${pageContext.request.contextPath}/shop" class="btn btn-primary">Browse Catalog</a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div style="display: flex; flex-direction: column; gap: 1.25rem;">
                                <c:forEach var="order" items="${orders}">
                                    <div class="card" style="background: #fafafa; border: 1px solid var(--border-color);">
                                        <div class="flex items-center justify-between" style="border-bottom: 1px solid var(--border-color); padding-bottom: 0.75rem; margin-bottom: 0.75rem; flex-wrap: wrap; gap: 0.5rem;">
                                            <div>
                                                <span style="font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase;">ORDER PLACED</span>
                                                <div style="font-size: 0.875rem; font-weight: 600;">
                                                    <fmt:formatDate value="${order.createdAt}" pattern="dd MMMM yyyy" />
                                                </div>
                                            </div>
                                            <div>
                                                <span style="font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase;">TOTAL</span>
                                                <div style="font-size: 0.875rem; font-weight: 700; color: var(--primary);">
                                                    ₹<fmt:formatNumber value="${order.totalAmount}" minFractionDigits="2" maxFractionDigits="2" />
                                                </div>
                                            </div>
                                            <div>
                                                <span style="font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase;">STATUS</span>
                                                <div>
                                                    <span class="badge-status badge-${order.status.toLowerCase()}">
                                                        <c:out value="${order.status}" />
                                                    </span>
                                                </div>
                                            </div>
                                            <div>
                                                <span style="font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase;">ORDER #<c:out value="${order.id}" /></span>
                                            </div>
                                        </div>

                                        <!-- Purchased Items in this Order -->
                                        <div style="display: flex; flex-direction: column; gap: 0.75rem;">
                                            <c:forEach var="item" items="${order.items}">
                                                <div class="flex items-center justify-between" style="flex-wrap: wrap; gap: 0.75rem;">
                                                    <div class="flex items-center gap-3">
                                                        <c:if test="${not empty item.productImageUrl}">
                                                            <img src="<c:out value="${item.productImageUrl}" />" alt=""
                                                                 style="width: 50px; height: 50px; object-fit: cover; border-radius: 6px; background: #ffffff; border: 1px solid #e2e8f0;" />
                                                        </c:if>
                                                        <div>
                                                            <a href="${pageContext.request.contextPath}/product?id=${item.productId}" style="font-weight: 600; color: var(--text-primary); font-size: 0.9rem;">
                                                                <c:out value="${item.productName}" />
                                                            </a>
                                                            <div style="font-size: 0.8rem; color: var(--text-muted);">
                                                                Qty: ${item.quantity} × ₹<fmt:formatNumber value="${item.unitPrice}" minFractionDigits="2" maxFractionDigits="2" />
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="flex gap-2">
                                                        <a href="${pageContext.request.contextPath}/product?id=${item.productId}" class="btn btn-outline btn-sm">
                                                            Buy Again
                                                        </a>
                                                        <a href="${pageContext.request.contextPath}/product?id=${item.productId}#review-form" class="btn btn-primary btn-sm">
                                                            ★ Write Review
                                                        </a>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>

                                        <c:if test="${not empty order.shippingAddress}">
                                            <div style="margin-top: 0.75rem; padding-top: 0.5rem; border-top: 1px dashed var(--border-color); font-size: 0.8rem; color: var(--text-secondary);">
                                                <strong>Shipping to:</strong> <c:out value="${order.shippingAddress}" />
                                            </div>
                                        </c:if>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
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
