<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sell on LakshanMart | Become a Seller</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .seller-hero {
            background: linear-gradient(135deg, #131921 0%, #1e293b 100%);
            color: #ffffff;
            padding: 3.5rem 1.5rem;
            border-radius: var(--radius-lg);
            margin-bottom: 2.5rem;
            text-align: center;
        }
        .benefit-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
            gap: 1.5rem;
            margin-bottom: 3rem;
        }
        .benefit-card {
            background: #ffffff;
            border: 1px solid var(--border-color);
            border-radius: var(--radius-md);
            padding: 1.75rem;
            text-align: center;
            box-shadow: var(--shadow-sm);
        }
        .benefit-icon {
            font-size: 2.5rem;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>

    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <main class="container" style="padding-top: 2rem; padding-bottom: 4rem;">
        <!-- Hero Banner -->
        <div class="seller-hero">
            <span style="background: rgba(245, 158, 11, 0.2); color: #fbbf24; padding: 4px 12px; border-radius: 999px; font-size: 0.85rem; font-weight: 700; text-transform: uppercase;">
                Merchant Partner Program
            </span>
            <h1 style="font-size: 2.5rem; font-weight: 900; margin: 1rem 0 0.5rem;">Sell to Crores of Customers on LakshanMart</h1>
            <p style="color: #94a3b8; font-size: 1.1rem; max-width: 650px; margin: 0 auto 1.5rem;">
                Launch your business online with India's premier multi-vendor marketplace. List products across Electronics, Fashion, Home & Kitchen, and more with zero initial setup fees.
            </p>
        </div>

        <!-- 4 Key Seller Advantages -->
        <div class="benefit-grid">
            <div class="benefit-card">
                <div class="benefit-icon">📈</div>
                <h3 style="font-size: 1.15rem; font-weight: 700; margin-bottom: 0.5rem;">Nationwide Reach</h3>
                <p style="font-size: 0.875rem; color: var(--text-muted); line-height: 1.5;">
                    Access millions of active shoppers looking for top-brand electronics, fashion, and everyday essentials.
                </p>
            </div>
            <div class="benefit-card">
                <div class="benefit-icon">⚡</div>
                <h3 style="font-size: 1.15rem; font-weight: 700; margin-bottom: 0.5rem;">LakshanMart Assured</h3>
                <p style="font-size: 0.875rem; color: var(--text-muted); line-height: 1.5;">
                    Hassle-free shipping, door-step delivery pick-up, automated tracking, and dedicated seller support.
                </p>
            </div>
            <div class="benefit-card">
                <div class="benefit-icon">💳</div>
                <h3 style="font-size: 1.15rem; font-weight: 700; margin-bottom: 0.5rem;">7-Day Express Payouts</h3>
                <p style="font-size: 0.875rem; color: var(--text-muted); line-height: 1.5;">
                    Get timely, transparent payments directly deposited to your bank account with complete revenue analytics.
                </p>
            </div>
            <div class="benefit-card">
                <div class="benefit-icon">🛡️</div>
                <h3 style="font-size: 1.15rem; font-weight: 700; margin-bottom: 0.5rem;">0% Commission Tier</h3>
                <p style="font-size: 0.875rem; color: var(--text-muted); line-height: 1.5;">
                    Enjoy zero platform listing fees for your first 90 days. Keep 100% of your product margins.
                </p>
            </div>
        </div>

        <!-- Onboarding Activation Card -->
        <div class="card" style="max-width: 600px; margin: 0 auto; padding: 2.5rem;">
            <div style="text-align: center; margin-bottom: 1.75rem;">
                <h2 style="font-size: 1.5rem; font-weight: 800;">Activate Your Merchant Account</h2>
                <p style="color: var(--text-muted); font-size: 0.875rem; margin-top: 4px;">
                    Upgrade your current account (${sessionScope.currentUser.email}) to start listing products instantly.
                </p>
            </div>

            <c:if test="${not empty param.error}">
                <div style="background: #fee2e2; color: #991b1b; padding: 0.75rem; border-radius: 6px; font-size: 0.875rem; margin-bottom: 1.25rem;">
                    <c:out value="${param.error}" />
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/seller/onboard" method="post">
                <div style="margin-bottom: 1.25rem;">
                    <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Merchant / Business Display Name</label>
                    <input type="text" name="storeName" class="search-input" style="padding: 0.75rem 1rem; border-radius: 6px; width: 100%;"
                           value="<c:out value="${sessionScope.currentUser.name}" /> Store" required />
                </div>

                <div style="margin-bottom: 1.25rem;">
                    <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Primary Product Category</label>
                    <select class="search-category-select" style="width: 100%; padding: 0.75rem; border: 1.5px solid var(--border-color); border-radius: 6px;">
                        <option value="Electronics">Electronics & Gadgets</option>
                        <option value="Fashion">Fashion & Apparel</option>
                        <option value="Home & Kitchen">Home & Kitchen Appliances</option>
                        <option value="Books">Books & Media</option>
                        <option value="Sports & Fitness">Sports & Fitness Equipment</option>
                    </select>
                </div>

                <div style="margin-bottom: 1.5rem;">
                    <label style="display: flex; align-items: flex-start; gap: 0.5rem; font-size: 0.8125rem; color: var(--text-secondary); cursor: pointer;">
                        <input type="checkbox" checked required style="margin-top: 3px;" />
                        <span>I agree to the LakshanMart Merchant Agreement, fair pricing policy, and seller code of conduct.</span>
                    </label>
                </div>

                <button type="submit" class="btn btn-primary" style="width: 100%; padding: 0.85rem; font-size: 1.05rem;">
                    🚀 Start Selling on LakshanMart
                </button>
            </form>
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
