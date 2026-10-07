<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard | LakshanMart</title>
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
        .stat-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 1.5rem;
            margin-bottom: 2.5rem;
        }
        .stat-card {
            background: #ffffff;
            border: 1px solid var(--border-color);
            border-radius: var(--radius-md);
            padding: 1.5rem;
            box-shadow: var(--shadow-sm);
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .stat-val {
            font-size: 2rem;
            font-weight: 800;
            color: var(--text-primary);
            line-height: 1.2;
        }
        .stat-label {
            font-size: 0.875rem;
            color: var(--text-muted);
            font-weight: 500;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
    </style>
</head>
<body>

    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <main class="container" style="padding-top: 2rem; padding-bottom: 3rem;">
        <div class="flex items-center justify-between" style="margin-bottom: 1.5rem;">
            <div>
                <h1 style="font-size: 1.875rem; font-weight: 800;">Admin Management Portal</h1>
                <p style="color: var(--text-muted); font-size: 0.9rem;">Marketplace operations, inventory, and order analytics</p>
            </div>
            <div class="flex gap-2">
                <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-primary btn-sm">+ Add / Manage Products</a>
                <a href="${pageContext.request.contextPath}/shop" class="btn btn-outline btn-sm">Storefront &rarr;</a>
            </div>
        </div>

        <!-- Navigation Tabs -->
        <nav class="admin-nav">
            <a href="${pageContext.request.contextPath}/admin" class="active">Overview Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/products">Product Catalog</a>
            <a href="${pageContext.request.contextPath}/admin/orders">Order Management</a>
            <a href="${pageContext.request.contextPath}/admin/users">User Directory</a>
        </nav>

        <!-- KPI Metrics Grid -->
        <div class="stat-grid">
            <div class="stat-card">
                <div>
                    <div class="stat-label">Total Products</div>
                    <div class="stat-val"><c:out value="${totalProducts}" /></div>
                </div>
                <div style="font-size: 2.5rem; opacity: 0.8;">📦</div>
            </div>
            <div class="stat-card">
                <div>
                    <div class="stat-label">Total Orders</div>
                    <div class="stat-val"><c:out value="${totalOrders}" /></div>
                </div>
                <div style="font-size: 2.5rem; opacity: 0.8;">📋</div>
            </div>
            <div class="stat-card">
                <div>
                    <div class="stat-label">Gross Revenue</div>
                    <div class="stat-val" style="color: var(--success);">
                        ₹<fmt:formatNumber value="${totalRevenue}" minFractionDigits="2" maxFractionDigits="2" />
                    </div>
                </div>
                <div style="font-size: 2.5rem; opacity: 0.8;">💰</div>
            </div>
            <div class="stat-card">
                <div>
                    <div class="stat-label">Users & Sellers</div>
                    <div class="stat-val"><c:out value="${totalUsers}" /></div>
                    <div style="font-size: 0.75rem; color: var(--text-muted); margin-top: 4px;">
                        ${totalSellers} Sellers | ${totalBuyers} Buyers
                    </div>
                </div>
                <div style="font-size: 2.5rem; opacity: 0.8;">👥</div>
            </div>
        </div>

        <!-- Recent Marketplace Orders -->
        <div class="card" style="padding: 0; overflow: hidden; margin-bottom: 2.5rem;">
            <div class="flex items-center justify-between" style="padding: 1.25rem 1.5rem; border-bottom: 1px solid var(--border-color);">
                <h2 style="font-size: 1.25rem; font-weight: 700;">Recent Marketplace Orders</h2>
                <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-outline btn-sm">View All Orders</a>
            </div>

            <table class="cart-table" style="margin: 0;">
                <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>Buyer ID</th>
                        <th>Date Placed</th>
                        <th>Status</th>
                        <th>Total Amount</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty recentOrders}">
                            <tr>
                                <td colspan="6" style="text-align: center; padding: 2rem; color: var(--text-muted);">
                                    No orders have been recorded yet.
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="order" items="${recentOrders}">
                                <tr>
                                    <td><strong>#<c:out value="${order.id}" /></strong></td>
                                    <td>Buyer #<c:out value="${order.buyerId}" /></td>
                                    <td><fmt:formatDate value="${order.createdAt}" pattern="dd MMM yyyy, hh:mm a" /></td>
                                    <td>
                                        <span class="badge-status badge-${order.status.toLowerCase()}">
                                            <c:out value="${order.status}" />
                                        </span>
                                    </td>
                                    <td style="font-weight: 700;">
                                        ₹<fmt:formatNumber value="${order.totalAmount}" minFractionDigits="2" maxFractionDigits="2" />
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-outline btn-sm">Manage</a>
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
    </script>
</body>
</html>
