<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Account | LakshanMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <main class="container" style="display: flex; justify-content: center; align-items: center; min-height: 70vh; padding: 2rem 1rem;">
        <div class="card" style="width: 100%; max-width: 460px; padding: 2.25rem;">
            <div style="text-align: center; margin-bottom: 1.5rem;">
                <h1 style="font-size: 1.5rem; font-weight: 800;">Create a LakshanMart Account</h1>
                <p style="color: var(--text-muted); font-size: 0.875rem; margin-top: 4px;">Join as a verified buyer or marketplace seller</p>
            </div>

            <c:if test="${not empty errorMessage}">
                <div style="background: #fee2e2; color: #991b1b; padding: 0.75rem; border-radius: 6px; font-size: 0.875rem; margin-bottom: 1rem;">
                    <c:out value="${errorMessage}" />
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/register" method="post">
                <div style="margin-bottom: 1rem;">
                    <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Full Name</label>
                    <input type="text" name="name" class="search-input" style="padding: 0.65rem 1rem; border-radius: 6px; width: 100%;"
                           placeholder="John Doe" value="<c:out value="${param.name}" />" required />
                </div>

                <div style="margin-bottom: 1rem;">
                    <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Email Address</label>
                    <input type="email" name="email" class="search-input" style="padding: 0.65rem 1rem; border-radius: 6px; width: 100%;"
                           placeholder="you@example.com" value="<c:out value="${param.email}" />" required />
                </div>

                <div style="margin-bottom: 1rem;">
                    <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Password</label>
                    <input type="password" name="password" class="search-input" style="padding: 0.65rem 1rem; border-radius: 6px; width: 100%;"
                           placeholder="At least 6 characters" minlength="6" required />
                </div>

                <div style="margin-bottom: 1.5rem;">
                    <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Account Type</label>
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem;">
                        <label style="display: flex; align-items: center; gap: 0.5rem; padding: 0.75rem; border: 1.5px solid var(--border-color); border-radius: 6px; cursor: pointer;">
                            <input type="radio" name="role" value="BUYER" ${empty param.role or param.role eq 'BUYER' ? 'checked' : ''} />
                            <div>
                                <strong style="font-size: 0.875rem;">Buyer</strong>
                                <div style="font-size: 0.75rem; color: var(--text-muted);">Shop & order items</div>
                            </div>
                        </label>
                        <label style="display: flex; align-items: center; gap: 0.5rem; padding: 0.75rem; border: 1.5px solid var(--border-color); border-radius: 6px; cursor: pointer;">
                            <input type="radio" name="role" value="SELLER" ${param.role eq 'SELLER' ? 'checked' : ''} />
                            <div>
                                <strong style="font-size: 0.875rem;">Seller</strong>
                                <div style="font-size: 0.75rem; color: var(--text-muted);">List & sell goods</div>
                            </div>
                        </label>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary" style="width: 100%; padding: 0.75rem; font-size: 1rem;">
                    Create Account
                </button>
            </form>

            <div style="margin-top: 1.5rem; padding-top: 1rem; border-top: 1px solid var(--border-color); text-align: center; font-size: 0.875rem;">
                <span style="color: var(--text-muted);">Already have an account?</span>
                <a href="${pageContext.request.contextPath}/login" style="font-weight: 600; margin-left: 4px;">Sign In</a>
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
