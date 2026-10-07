<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<footer class="site-footer">
    <div class="container">
        <div class="footer-grid">
            <div class="footer-col">
                <h4 style="color: #ffffff; font-size: 1.25rem; font-weight: 800; margin-bottom: 0.75rem;">LakshanMart</h4>
                <p style="line-height: 1.6; margin-bottom: 1rem;">
                    A modern multi-seller marketplace built with pure Java Servlets, JDBC, and Apache Tomcat.
                    Developed for Anna University R2025 Semester 3 Capstone.
                </p>
                <div style="display: flex; gap: 1rem; color: #38bdf8; font-weight: 600;">
                    <span>✓ Secure Checkout</span>
                    <span>✓ Verified Reviews</span>
                    <span>✓ 7-Day Returns</span>
                </div>
            </div>

            <div class="footer-col">
                <h4>Shop Categories</h4>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/shop?category=Electronics">Electronics</a></li>
                    <li><a href="${pageContext.request.contextPath}/shop?category=Fashion">Fashion & Apparel</a></li>
                    <li><a href="${pageContext.request.contextPath}/shop?category=Home%20%26%20Kitchen">Home & Kitchen</a></li>
                    <li><a href="${pageContext.request.contextPath}/shop?category=Books">Books & Stationery</a></li>
                    <li><a href="${pageContext.request.contextPath}/shop?category=Sports%20%26%20Fitness">Sports & Fitness</a></li>
                </ul>
            </div>

            <div class="footer-col">
                <h4>Customer Service</h4>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/orders">Track Your Orders</a></li>
                    <li><a href="${pageContext.request.contextPath}/cart">Shopping Cart</a></li>
                    <li><a href="#" onclick="document.getElementById('chat-trigger-btn').click(); return false;">Ask AI Assistant</a></li>
                    <li><a href="#">Return Policy</a></li>
                </ul>
            </div>

            <div class="footer-col">
                <h4>Marketplace Account</h4>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/login">Customer Sign In</a></li>
                    <li><a href="${pageContext.request.contextPath}/seller/onboard">Sell on LakshanMart</a></li>
                    <li><a href="${pageContext.request.contextPath}/seller/dashboard">Seller Central Hub</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin">Admin Console</a></li>
                </ul>
            </div>
        </div>

        <div class="footer-bottom">
            <p>&copy; 2026 LakshanMart Marketplace. All rights reserved.</p>
            <p style="color: #64748b;">Anna University R2025 Capstone Specification</p>
        </div>
    </div>
</footer>
