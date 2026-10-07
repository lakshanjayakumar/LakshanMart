<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In | LakshanMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <main class="container" style="display: flex; justify-content: center; align-items: center; min-height: 70vh; padding: 2rem 1rem;">
        <div class="card" style="width: 100%; max-width: 420px; padding: 2.25rem;">
            <div style="text-align: center; margin-bottom: 1.5rem;">
                <h1 style="font-size: 1.5rem; font-weight: 800;">Sign in to LakshanMart</h1>
                <p style="color: var(--text-muted); font-size: 0.875rem; margin-top: 4px;">Access your buyer or seller account</p>
            </div>

            <c:if test="${not empty errorMessage}">
                <div style="background: #fee2e2; color: #991b1b; padding: 0.75rem; border-radius: 6px; font-size: 0.875rem; margin-bottom: 1rem;">
                    <c:out value="${errorMessage}" />
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="post">
                <input type="hidden" name="redirect" value="<c:out value="${param.redirect}" />" />

                <div style="margin-bottom: 1rem;">
                    <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Email Address</label>
                    <input type="email" name="email" class="search-input" style="padding: 0.65rem 1rem; border-radius: 6px; width: 100%;"
                           placeholder="you@example.com" value="<c:out value="${param.email}" />" required />
                </div>

                <div style="margin-bottom: 1.5rem;">
                    <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Password</label>
                    <input type="password" name="password" class="search-input" style="padding: 0.65rem 1rem; border-radius: 6px; width: 100%;"
                           placeholder="••••••••" required />
                </div>

                <button type="submit" class="btn btn-primary" style="width: 100%; padding: 0.75rem; font-size: 1rem;">
                    Sign In
                </button>
            </form>

            <div style="margin-top: 1.5rem; padding-top: 1rem; border-top: 1px solid var(--border-color); text-align: center; font-size: 0.875rem;">
                <span style="color: var(--text-muted);">New to LakshanMart?</span>
                <a href="${pageContext.request.contextPath}/register" style="font-weight: 600; margin-left: 4px;">Create an account</a>
            </div>

            <!-- Demo Seed Accounts Reference -->
            <div style="margin-top: 1rem; background: #f1f5f9; padding: 0.75rem; border-radius: 6px; font-size: 0.75rem; color: var(--text-secondary);">
                <strong>Seed Demo Accounts:</strong>
                <div>Admin: <code>admin@lakshanmart.com</code> / <code>Admin@123</code></div>
                <div>Buyer: <code>bob@lakshanmart.com</code> / <code>Admin@123</code></div>
                <div>Seller: <code>alice@lakshanmart.com</code> / <code>Admin@123</code></div>
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
