<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Orders | LakshanMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <main class="container" style="padding-top: 2rem; padding-bottom: 3rem;">
        <h1 style="font-size: 1.875rem; font-weight: 800; margin-bottom: 1.5rem;">My Orders & History</h1>

        <c:choose>
            <c:when test="${empty orders}">
                <div class="card" style="text-align: center; padding: 4rem 2rem;">
                    <div style="font-size: 4rem; margin-bottom: 1rem;">📦</div>
                    <h2 style="font-size: 1.5rem; font-weight: 700; margin-bottom: 0.5rem;">No orders found</h2>
                    <p style="color: var(--text-muted); margin-bottom: 1.5rem;">You haven't placed any marketplace orders yet.</p>
                    <a href="${pageContext.request.contextPath}/shop" class="btn btn-primary">Start Shopping</a>
                </div>
            </c:when>
            <c:otherwise>
                <div style="display: flex; flex-direction: column; gap: 1.5rem;">
                    <c:forEach var="order" items="${orders}">
                        <div class="card">
                            <div class="flex items-center justify-between" style="border-bottom: 1px solid var(--border-color); padding-bottom: 1rem; margin-bottom: 1rem; flex-wrap: wrap; gap: 0.5rem;">
                                <div>
                                    <span style="font-size: 0.8125rem; color: var(--text-muted);">ORDER #<c:out value="${order.id}" /></span>
                                    <div style="font-size: 0.875rem; font-weight: 600; color: var(--text-secondary);">
                                        Placed on: <fmt:formatDate value="${order.createdAt}" pattern="dd MMM yyyy, hh:mm a" />
                                    </div>
                                </div>
                                <div class="flex items-center gap-4">
                                    <span class="badge-status badge-${order.status.toLowerCase()}">
                                        <c:out value="${order.status}" />
                                    </span>
                                    <span style="font-size: 1.25rem; font-weight: 800; color: var(--primary);">
                                        ₹<fmt:formatNumber value="${order.totalAmount}" minFractionDigits="2" maxFractionDigits="2" />
                                    </span>
                                </div>
                            </div>

                            <!-- Line Items -->
                            <div style="display: flex; flex-direction: column; gap: 0.75rem;">
                                <c:forEach var="item" items="${order.items}">
                                    <div class="flex items-center justify-between" style="font-size: 0.9375rem; padding: 0.5rem 0;">
                                        <div class="flex items-center gap-3">
                                            <c:if test="${not empty item.productImageUrl}">
                                                <img src="<c:out value="${item.productImageUrl}" />" alt="" style="width: 45px; height: 45px; object-fit: cover; border-radius: 4px;" />
                                            </c:if>
                                            <div>
                                                <a href="${pageContext.request.contextPath}/product?id=${item.productId}" style="font-weight: 600; color: var(--text-primary);">
                                                    <c:out value="${item.productName}" />
                                                </a>
                                                <div style="font-size: 0.8125rem; color: var(--text-muted);">Qty: ${item.quantity} × ₹<fmt:formatNumber value="${item.unitPrice}" minFractionDigits="2" maxFractionDigits="2" /></div>
                                            </div>
                                        </div>
                                        <div class="flex items-center gap-4">
                                            <strong style="color: var(--text-primary);">₹<fmt:formatNumber value="${item.subtotal}" minFractionDigits="2" maxFractionDigits="2" /></strong>
                                            <a href="${pageContext.request.contextPath}/product?id=${item.productId}" class="btn btn-outline btn-sm">
                                                Review Product ★
                                            </a>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>

                            <c:if test="${not empty order.shippingAddress}">
                                <div style="margin-top: 1rem; padding-top: 0.75rem; border-top: 1px dashed var(--border-color); font-size: 0.8125rem; color: var(--text-secondary);">
                                    <strong>Shipping Address:</strong> <c:out value="${order.shippingAddress}" />
                                </div>
                            </c:if>
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
