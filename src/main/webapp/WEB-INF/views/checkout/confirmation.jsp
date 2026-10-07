<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order Confirmed | LakshanMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .confirmation-card {
            background: #ffffff;
            border: 1px solid var(--border-color);
            border-radius: var(--radius-md);
            padding: 2.5rem;
            box-shadow: var(--shadow-sm);
            max-width: 800px;
            margin: 2rem auto;
        }
        .success-header {
            display: flex;
            align-items: center;
            gap: 1.25rem;
            padding-bottom: 1.5rem;
            border-bottom: 1px solid var(--border-color);
            margin-bottom: 1.5rem;
        }
        .check-circle {
            width: 56px;
            height: 56px;
            border-radius: var(--radius-full);
            background-color: #d1fae5;
            color: #065f46;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.75rem;
            font-weight: 800;
        }
    </style>
</head>
<body>

    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <main class="container">
        <div class="confirmation-card">
            <!-- Amazon / Flipkart Style Order Placed Header -->
            <div class="success-header">
                <div class="check-circle">✓</div>
                <div>
                    <h1 style="font-size: 1.75rem; font-weight: 800; color: #065f46; margin-bottom: 0.25rem;">
                        Order Placed, thank you!
                    </h1>
                    <p style="color: var(--text-secondary); font-size: 0.95rem;">
                        Confirmation will be sent to <strong><c:out value="${sessionScope.currentUser.email}" /></strong>
                    </p>
                </div>
            </div>

            <!-- Order Metadata Details -->
            <div style="background-color: #f8fafc; border: 1px solid var(--border-color); border-radius: var(--radius-sm); padding: 1.25rem; margin-bottom: 2rem;">
                <div class="flex justify-between" style="flex-wrap: wrap; gap: 1rem; font-size: 0.9rem;">
                    <div>
                        <span style="color: var(--text-muted); font-size: 0.75rem; text-transform: uppercase;">ORDER NUMBER</span>
                        <div style="font-weight: 700; font-size: 1.1rem; color: var(--text-primary);">
                            #<c:out value="${order.id}" />
                        </div>
                    </div>
                    <div>
                        <span style="color: var(--text-muted); font-size: 0.75rem; text-transform: uppercase;">ESTIMATED DELIVERY</span>
                        <div style="font-weight: 600; color: var(--success);">
                            2 - 3 Business Days (Express)
                        </div>
                    </div>
                    <div>
                        <span style="color: var(--text-muted); font-size: 0.75rem; text-transform: uppercase;">ORDER STATUS</span>
                        <div>
                            <span class="badge-status badge-${order.status.toLowerCase()}">
                                <c:out value="${order.status}" />
                            </span>
                        </div>
                    </div>
                    <div>
                        <span style="color: var(--text-muted); font-size: 0.75rem; text-transform: uppercase;">TOTAL PAID</span>
                        <div style="font-weight: 800; font-size: 1.2rem; color: var(--primary);">
                            ₹<fmt:formatNumber value="${order.totalAmount}" minFractionDigits="2" maxFractionDigits="2" />
                        </div>
                    </div>
                </div>

                <div style="margin-top: 1rem; padding-top: 0.75rem; border-top: 1px dashed var(--border-color); font-size: 0.85rem; color: var(--text-secondary);">
                    <strong>Shipping Address:</strong> <c:out value="${order.shippingAddress}" />
                </div>
            </div>

            <!-- Items Purchased Review -->
            <h2 style="font-size: 1.125rem; font-weight: 700; margin-bottom: 1rem;">Items in this Shipment</h2>
            <div style="display: flex; flex-direction: column; gap: 0.75rem; margin-bottom: 2rem;">
                <c:forEach var="item" items="${order.items}">
                    <div class="flex items-center justify-between" style="padding: 0.75rem 0; border-bottom: 1px solid #f1f5f9;">
                        <div class="flex items-center gap-3">
                            <c:if test="${not empty item.productImageUrl}">
                                <img src="<c:out value="${item.productImageUrl}" />" alt=""
                                     style="width: 52px; height: 52px; object-fit: cover; border-radius: 6px; border: 1px solid var(--border-color);" />
                            </c:if>
                            <div>
                                <a href="${pageContext.request.contextPath}/product?id=${item.productId}" style="font-weight: 600; color: var(--text-primary); font-size: 0.95rem;">
                                    <c:out value="${item.productName}" />
                                </a>
                                <div style="font-size: 0.8rem; color: var(--text-muted);">
                                    Qty: ${item.quantity} × ₹<fmt:formatNumber value="${item.unitPrice}" minFractionDigits="2" maxFractionDigits="2" />
                                </div>
                            </div>
                        </div>
                        <div style="font-weight: 700; color: var(--text-primary);">
                            ₹<fmt:formatNumber value="${item.subtotal}" minFractionDigits="2" maxFractionDigits="2" />
                        </div>
                    </div>
                </c:forEach>
            </div>

            <!-- Navigation Actions -->
            <div class="flex gap-4" style="justify-content: flex-end; flex-wrap: wrap;">
                <a href="${pageContext.request.contextPath}/orders" class="btn btn-outline">
                    View All Orders & Tracking
                </a>
                <a href="${pageContext.request.contextPath}/profile" class="btn btn-outline">
                    Your Account
                </a>
                <a href="${pageContext.request.contextPath}/shop" class="btn btn-primary">
                    Continue Shopping &rarr;
                </a>
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
