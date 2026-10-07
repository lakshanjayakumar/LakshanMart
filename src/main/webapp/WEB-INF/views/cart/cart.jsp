<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Shopping Cart | LakshanMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <main class="container" style="padding-top: 2rem; padding-bottom: 3rem;">
        <h1 style="font-size: 1.875rem; font-weight: 800; margin-bottom: 1.5rem;">Your Shopping Cart</h1>

        <c:choose>
            <c:when test="${empty cart or empty cart.items}">
                <div class="card" style="text-align: center; padding: 4rem 2rem;">
                    <div style="font-size: 4rem; margin-bottom: 1rem;">🛒</div>
                    <h2 style="font-size: 1.5rem; font-weight: 700; margin-bottom: 0.5rem;">Your cart is empty</h2>
                    <p style="color: var(--text-muted); margin-bottom: 1.5rem;">Looks like you haven't added anything to your cart yet.</p>
                    <a href="${pageContext.request.contextPath}/shop" class="btn btn-primary">Start Shopping</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="cart-layout">
                    <!-- Items Table -->
                    <div class="card" style="padding: 0; overflow: hidden;">
                        <table class="cart-table">
                            <thead>
                                <tr>
                                    <th>Product</th>
                                    <th>Price</th>
                                    <th>Quantity</th>
                                    <th>Subtotal</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${cart.items}">
                                    <tr>
                                        <td>
                                            <div class="flex items-center gap-4">
                                                <img src="<c:out value="${item.productImageUrl}" />" alt="<c:out value="${item.productName}" />"
                                                     style="width: 60px; height: 60px; object-fit: cover; border-radius: 6px; background: #f1f5f9;" />
                                                <div>
                                                    <a href="${pageContext.request.contextPath}/product?id=${item.productId}" style="font-weight: 600; color: var(--text-primary);">
                                                        <c:out value="${item.productName}" />
                                                    </a>
                                                    <div style="font-size: 0.75rem; color: var(--text-muted);">Stock: ${item.stockQty}</div>
                                                </div>
                                            </div>
                                        </td>
                                        <td style="font-weight: 600;">₹<fmt:formatNumber value="${item.unitPrice}" minFractionDigits="2" maxFractionDigits="2" /></td>
                                        <td>
                                            <div class="qty-control">
                                                <button type="button" class="qty-btn" onclick="LakshanMart.updateCartQuantity(${item.id}, ${item.quantity - 1})">-</button>
                                                <input type="text" class="qty-input" value="${item.quantity}" readonly />
                                                <button type="button" class="qty-btn" onclick="LakshanMart.updateCartQuantity(${item.id}, ${item.quantity + 1})">+</button>
                                            </div>
                                        </td>
                                        <td style="font-weight: 700; color: var(--primary);">₹<fmt:formatNumber value="${item.subtotal}" minFractionDigits="2" maxFractionDigits="2" /></td>
                                        <td>
                                            <button type="button" class="btn btn-outline btn-sm" style="color: var(--danger); border-color: transparent;" onclick="LakshanMart.removeCartItem(${item.id})">
                                                🗑️
                                            </button>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <!-- Cart Summary Card -->
                    <div>
                        <div class="card">
                            <h3 style="font-size: 1.125rem; font-weight: 700; margin-bottom: 1.25rem;">Order Summary</h3>
                            <div class="flex justify-between" style="margin-bottom: 0.75rem; color: var(--text-secondary);">
                                <span>Items Subtotal (${cart.itemCount})</span>
                                <strong>₹<fmt:formatNumber value="${cart.totalAmount}" minFractionDigits="2" maxFractionDigits="2" /></strong>
                            </div>
                            <div class="flex justify-between" style="margin-bottom: 0.75rem; color: var(--text-secondary);">
                                <span>Shipping Estimate</span>
                                <span style="color: var(--success); font-weight: 600;">FREE</span>
                            </div>
                            <hr style="border: none; border-top: 1px solid var(--border-color); margin: 1rem 0;" />
                            <div class="flex justify-between" style="font-size: 1.25rem; font-weight: 800; margin-bottom: 1.5rem;">
                                <span>Total Amount</span>
                                <span style="color: var(--primary);">₹<fmt:formatNumber value="${cart.totalAmount}" minFractionDigits="2" maxFractionDigits="2" /></span>
                            </div>

                            <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary" style="width: 100%; padding: 0.85rem;">
                                Proceed to Checkout &rarr;
                            </a>
                            <div style="text-align: center; margin-top: 1rem;">
                                <a href="${pageContext.request.contextPath}/shop" style="font-size: 0.85rem; color: var(--text-muted);">
                                    &larr; Continue Shopping
                                </a>
                            </div>
                        </div>
                    </div>
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
