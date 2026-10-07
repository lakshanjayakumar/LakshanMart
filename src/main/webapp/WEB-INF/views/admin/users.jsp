<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User & Seller Management | LakshanMart Admin</title>
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
        .filter-pill {
            padding: 0.4rem 0.85rem;
            border-radius: 999px;
            font-size: 0.8125rem;
            font-weight: 600;
            border: 1px solid var(--border-color);
            color: var(--text-secondary);
            text-decoration: none;
        }
        .filter-pill.active {
            background: var(--primary);
            color: #ffffff;
            border-color: var(--primary);
        }
    </style>
</head>
<body>

    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <main class="container" style="padding-top: 2rem; padding-bottom: 3.5rem;">
        <div class="flex items-center justify-between" style="margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem;">
            <div>
                <h1 style="font-size: 1.875rem; font-weight: 800;">User & Merchant Directory</h1>
                <p style="color: var(--text-muted); font-size: 0.9rem;">Manage registered buyers, marketplace sellers, and administrator roles</p>
            </div>
            <div class="flex gap-2">
                <span class="badge-status" style="background-color: #dbeafe; color: #1e40af; font-size: 0.85rem;">
                    Sellers: <strong><c:out value="${totalSellers}" /></strong>
                </span>
                <span class="badge-status" style="background-color: #f1f5f9; color: #334155; font-size: 0.85rem;">
                    Buyers: <strong><c:out value="${totalBuyers}" /></strong>
                </span>
            </div>
        </div>

        <!-- Navigation Tabs -->
        <nav class="admin-nav">
            <a href="${pageContext.request.contextPath}/admin">Overview Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/products">Product Catalog</a>
            <a href="${pageContext.request.contextPath}/admin/orders">Order Management</a>
            <a href="${pageContext.request.contextPath}/admin/users" class="active">User Directory</a>
        </nav>

        <!-- Feedback Alert -->
        <c:if test="${not empty param.success}">
            <div style="background: #d1fae5; color: #065f46; padding: 0.75rem 1rem; border-radius: 6px; font-size: 0.875rem; margin-bottom: 1.25rem;">
                ✓ <c:out value="${param.success}" />
            </div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div style="background: #fee2e2; color: #991b1b; padding: 0.75rem 1rem; border-radius: 6px; font-size: 0.875rem; margin-bottom: 1.25rem;">
                ✕ <c:out value="${param.error}" />
            </div>
        </c:if>

        <!-- Filter Pills -->
        <div class="flex items-center gap-2" style="margin-bottom: 1.25rem;">
            <span style="font-size: 0.85rem; font-weight: 600; color: var(--text-muted); margin-right: 0.5rem;">Filter by:</span>
            <a href="${pageContext.request.contextPath}/admin/users" class="filter-pill ${empty currentRoleFilter ? 'active' : ''}">
                All (${totalUsers})
            </a>
            <a href="${pageContext.request.contextPath}/admin/users?role=SELLER" class="filter-pill ${currentRoleFilter eq 'SELLER' ? 'active' : ''}">
                Sellers (${totalSellers})
            </a>
            <a href="${pageContext.request.contextPath}/admin/users?role=BUYER" class="filter-pill ${currentRoleFilter eq 'BUYER' ? 'active' : ''}">
                Buyers (${totalBuyers})
            </a>
            <a href="${pageContext.request.contextPath}/admin/users?role=ADMIN" class="filter-pill ${currentRoleFilter eq 'ADMIN' ? 'active' : ''}">
                Admins (${totalAdmins})
            </a>
        </div>

        <!-- Users Table -->
        <div class="card" style="padding: 0; overflow: hidden;">
            <table class="cart-table" style="margin: 0;">
                <thead>
                    <tr>
                        <th>User ID</th>
                        <th>Full Name</th>
                        <th>Email Address</th>
                        <th>Current Role</th>
                        <th>Date Registered</th>
                        <th>Role Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty users}">
                            <tr>
                                <td colspan="6" style="text-align: center; padding: 2.5rem; color: var(--text-muted);">
                                    No registered accounts found matching this filter.
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="u" items="${users}">
                                <tr>
                                    <td>#<c:out value="${u.id}" /></td>
                                    <td>
                                        <strong><c:out value="${u.name}" /></strong>
                                    </td>
                                    <td>
                                        <code><c:out value="${u.email}" /></code>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${u.role eq 'ADMIN'}">
                                                <span class="badge-status" style="background-color: #ede9fe; color: #5b21b6;">ADMIN</span>
                                            </c:when>
                                            <c:when test="${u.role eq 'SELLER'}">
                                                <span class="badge-status" style="background-color: #dbeafe; color: #1e40af;">SELLER (Merchant)</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge-status" style="background-color: #f1f5f9; color: #475569;">BUYER</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${u.createdAt}" pattern="dd MMM yyyy, hh:mm a" />
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/admin/users/role" method="post" style="display: flex; gap: 0.5rem; align-items: center;">
                                            <input type="hidden" name="userId" value="${u.id}" />
                                            <select name="role" class="search-category-select" style="padding: 0.35rem 0.5rem; font-size: 0.8125rem;">
                                                <option value="BUYER" ${u.role eq 'BUYER' ? 'selected' : ''}>BUYER</option>
                                                <option value="SELLER" ${u.role eq 'SELLER' ? 'selected' : ''}>SELLER</option>
                                                <option value="ADMIN" ${u.role eq 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                                            </select>
                                            <button type="submit" class="btn btn-outline btn-sm" style="padding: 0.35rem 0.75rem; font-size: 0.8rem;">
                                                Update
                                            </button>
                                        </form>
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
