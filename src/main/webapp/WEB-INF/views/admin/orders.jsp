<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order Management | LakshanMart Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .admin-nav {
            display: flex;
            gap: 1rem;
            margin-bottom: 2rem;
            border-bottom: 2px solid var(--border-color);
            padding-bottom: 0.5rem;
        }
        .admin-nav a {
            padding: 0.5rem 1rem;
            font-weight: 600;
            color: var(--text-secondary);
            border-radius: var(--radius-sm);
        }
        .admin-nav a.active {
            background-color: var(--primary);
            color: #ffffff;
        }
    </style>
</head>
<body>

    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <main class="container" style="padding-top: 2rem; padding-bottom: 3rem;">
        <div class="flex items-center justify-between" style="margin-bottom: 1.5rem;">
            <div>
                <h1 style="font-size: 1.875rem; font-weight: 800;">Customer Order Management</h1>
                <p style="color: var(--text-muted); font-size: 0.9rem;">Inspect marketplace purchase records and update shipment fulfillment status</p>
            </div>
            <span style="font-size: 0.875rem; color: var(--text-secondary);">
                Total Orders: <strong><c:out value="${orders.size()}" /></strong>
            </span>
        </div>

        <!-- Navigation Tabs -->
        <nav class="admin-nav">
            <a href="${pageContext.request.contextPath}/admin">Overview Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/products">Product Catalog</a>
            <a href="${pageContext.request.contextPath}/admin/orders" class="active">Order Management</a>
            <a href="${pageContext.request.contextPath}/admin/users">User Directory</a>
        </nav>

        <!-- Orders Table -->
        <div class="card" style="padding: 0; overflow: hidden;">
            <table class="cart-table" style="margin: 0;">
                <thead>
                    <tr>
                        <th>Order</th>
                        <th>Buyer ID</th>
                        <th>Date Placed</th>
                        <th>Items Purchased</th>
                        <th>Shipping Address</th>
                        <th>Total</th>
                        <th>Status</th>
                        <th>Update Status</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty orders}">
                            <tr>
                                <td colspan="8" style="text-align: center; padding: 2rem; color: var(--text-muted);">
                                    No customer orders found in the database.
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="order" items="${orders}">
                                <tr>
                                    <td><strong>#<c:out value="${order.id}" /></strong></td>
                                    <td>Buyer #<c:out value="${order.buyerId}" /></td>
                                    <td><fmt:formatDate value="${order.createdAt}" pattern="dd MMM yyyy, hh:mm a" /></td>
                                    <td>
                                        <div style="font-size: 0.8125rem;">
                                            <c:forEach var="item" items="${order.items}">
                                                <div>• <c:out value="${item.quantity}" />x <c:out value="${item.productName}" /></div>
                                            </c:forEach>
                                        </div>
                                    </td>
                                    <td style="font-size: 0.8125rem; max-width: 180px; color: var(--text-secondary);">
                                        <c:out value="${order.shippingAddress}" />
                                    </td>
                                    <td style="font-weight: 700; color: var(--primary);">
                                        ₹<fmt:formatNumber value="${order.totalAmount}" minFractionDigits="2" maxFractionDigits="2" />
                                    </td>
                                    <td>
                                        <span class="badge-status badge-${order.status.toLowerCase()}">
                                            <c:out value="${order.status}" />
                                        </span>
                                    </td>
                                    <td>
                                        <select class="search-input" style="padding: 0.35rem 0.65rem; border-radius: 4px; font-size: 0.8125rem; width: auto;"
                                                onchange="updateOrderStatus(${order.id}, this.value)">
                                            <option value="PENDING" ${order.status eq 'PENDING' ? 'selected' : ''}>PENDING</option>
                                            <option value="CONFIRMED" ${order.status eq 'CONFIRMED' ? 'selected' : ''}>CONFIRMED</option>
                                            <option value="SHIPPED" ${order.status eq 'SHIPPED' ? 'selected' : ''}>SHIPPED</option>
                                            <option value="DELIVERED" ${order.status eq 'DELIVERED' ? 'selected' : ''}>DELIVERED</option>
                                            <option value="CANCELLED" ${order.status eq 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                                        </select>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </main>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />
    <jsp:include page="/WEB-INF/views/common/chat-widget.jsp" />

    <script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            LakshanMart.init('${pageContext.request.contextPath}');
        });

        async function updateOrderStatus(orderId, newStatus) {
            try {
                const response = await fetch('${pageContext.request.contextPath}/api/v1/admin/orders/' + orderId, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ status: newStatus })
                });

                const data = await response.json();
                if (response.ok && data.success) {
                    LakshanMart.showToast('Order #' + orderId + ' updated to ' + newStatus, 'success');
                    setTimeout(() => window.location.reload(), 700);
                } else {
                    LakshanMart.showToast(data.error ? data.error.message : 'Failed to update order status', 'danger');
                }
            } catch (err) {
                LakshanMart.showToast('Network error updating order', 'danger');
            }
        }
    </script>
</body>
</html>
