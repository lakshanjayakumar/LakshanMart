<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${product.name}" /> | LakshanMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <main class="container" style="padding-top: 1.5rem; padding-bottom: 3rem;">
        <div style="font-size: 0.875rem; color: var(--text-muted); margin-bottom: 1.5rem;">
            <a href="${pageContext.request.contextPath}/home">Home</a> &rsaquo;
            <a href="${pageContext.request.contextPath}/shop?category=${product.category}"><c:out value="${product.category}" /></a> &rsaquo;
            <span><c:out value="${product.name}" /></span>
        </div>

        <div class="product-detail-layout">
            <div class="detail-image-box">
                <c:choose>
                    <c:when test="${not empty product.imageUrl}">
                        <img src="<c:out value="${product.imageUrl}" />" alt="<c:out value="${product.name}" />" class="detail-image" />
                    </c:when>
                    <c:otherwise>
                        <div style="height: 380px; display: flex; align-items: center; justify-content: center; font-size: 4rem;">📦</div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="detail-info">
                <div class="flex items-center gap-2" style="margin-bottom: 0.5rem;">
                    <span class="badge-choice"><span>LakshanMart's</span> Choice</span>
                    <span class="product-category" style="font-size: 0.875rem; font-weight: 700; color: var(--accent);"><c:out value="${product.category}" /></span>
                </div>

                <h1 style="font-size: 1.875rem; font-weight: 800; margin: 0.25rem 0 0.75rem; line-height: 1.25;"><c:out value="${product.name}" /></h1>

                <div class="flex items-center gap-2" style="margin-bottom: 1.25rem;">
                    <div class="rating-stars" style="color: #f59e0b; font-size: 1.1rem;">★★★★★</div>
                    <span style="font-size: 0.875rem; color: var(--text-secondary);">(${reviews.size()} customer ratings)</span>
                    <span style="color: var(--text-muted);">|</span>
                    <span style="font-size: 0.85rem; color: var(--primary); font-weight: 600;">100+ bought in past month</span>
                </div>

                <div style="margin-bottom: 1.5rem; padding-bottom: 1.25rem; border-bottom: 1px solid var(--border-color);">
                    <div class="flex items-center" style="margin-bottom: 0.25rem;">
                        <span class="badge-deal">Limited Deal</span>
                        <span class="mrp-strikethrough">M.R.P.: ₹<fmt:formatNumber value="${product.price * 1.25}" minFractionDigits="2" maxFractionDigits="2" /></span>
                    </div>
                    <div style="font-size: 2.25rem; font-weight: 800; color: var(--text-primary); margin-bottom: 0.25rem;">
                        ₹<fmt:formatNumber value="${product.price}" minFractionDigits="2" maxFractionDigits="2" />
                    </div>
                    <c:choose>
                        <c:when test="${product.stockQty > 0}">
                            <div style="color: var(--success); font-weight: 700; font-size: 0.95rem;">✓ In Stock (${product.stockQty} units available)</div>
                            <div style="font-size: 0.875rem; color: var(--text-secondary); margin-top: 0.35rem;">
                                ⚡ <strong>FREE Express Delivery</strong> by Tomorrow. Order within 4 hrs.
                            </div>
                            <div style="font-size: 0.875rem; color: var(--text-primary); margin-top: 0.45rem;">
                                Sold by: <strong style="color: var(--primary);"><c:out value="${sellerName}" /></strong> <span style="background: #e0f2fe; color: #0369a1; font-size: 0.72rem; font-weight: 700; padding: 2px 6px; border-radius: 4px; margin-left: 4px;">VERIFIED MERCHANT</span>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <span style="color: var(--danger); font-weight: 700; font-size: 0.95rem;">✕ Currently Out of Stock</span>
                            <div style="font-size: 0.875rem; color: var(--text-primary); margin-top: 0.45rem;">
                                Sold by: <strong style="color: var(--primary);"><c:out value="${sellerName}" /></strong>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div style="margin-bottom: 1.5rem;">
                    <h3 style="font-size: 1rem; font-weight: 700; margin-bottom: 0.5rem;">About this item</h3>
                    <p style="color: var(--text-secondary); line-height: 1.6;"><c:out value="${product.description}" /></p>
                </div>

                <!-- Technical Specifications Table -->
                <div style="margin-bottom: 1.75rem;">
                    <h3 style="font-size: 1rem; font-weight: 700; margin-bottom: 0.5rem;">Product Specifications</h3>
                    <table class="specs-table card" style="padding: 0;">
                        <tbody>
                            <tr>
                                <td class="spec-name">Brand & Quality</td>
                                <td class="spec-val">LakshanMart Verified Original</td>
                            </tr>
                            <tr>
                                <td class="spec-name">Category</td>
                                <td class="spec-val"><c:out value="${product.category}" /></td>
                            </tr>
                            <tr>
                                <td class="spec-name">Stock Status</td>
                                <td class="spec-val">${product.stockQty > 0 ? 'Ready to Ship' : 'Backordered'}</td>
                            </tr>
                            <tr>
                                <td class="spec-name">Warranty</td>
                                <td class="spec-val">1 Year Comprehensive Manufacturer Warranty</td>
                            </tr>
                            <tr>
                                <td class="spec-name">Return Policy</td>
                                <td class="spec-val">7 Days Replacement & 100% Refund Guarantee</td>
                            </tr>
                            <tr>
                                <td class="spec-name">Payment Methods</td>
                                <td class="spec-val">UPI, Credit/Debit Cards, Cash on Delivery (COD)</td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <c:if test="${product.stockQty > 0}">
                    <div style="background: #f8fafc; border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 1.25rem;">
                        <div class="flex items-center gap-3" style="margin-bottom: 1rem;">
                            <label style="font-weight: 600; font-size: 0.9rem;">Quantity:</label>
                            <div class="qty-control">
                                <button type="button" class="qty-btn" onclick="adjustDetailQty(-1)">-</button>
                                <input type="number" id="detail-qty" class="qty-input" value="1" min="1" max="${product.stockQty}" />
                                <button type="button" class="qty-btn" onclick="adjustDetailQty(1)">+</button>
                            </div>
                        </div>

                        <div class="flex gap-3">
                            <button type="button" class="btn btn-add-cart" style="flex: 1; padding: 0.85rem;" onclick="addDetailToCart(${product.id})">
                                🛒 Add to Cart
                            </button>
                            <button type="button" class="btn btn-buy-now" style="flex: 1; padding: 0.85rem;" onclick="buyNowDetail(${product.id})">
                                ⚡ Buy Now
                            </button>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>

        <!-- Verified Reviews Section (R2025 F8) -->
        <section style="margin-top: 3rem;">
            <div class="section-header">
                <div>
                    <h2 class="section-title">Verified Customer Reviews</h2>
                    <p style="font-size: 0.875rem; color: var(--text-secondary); margin-top: 2px;">
                        Authentic ratings and feedback from verified purchasers.
                    </p>
                </div>
            </div>

            <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 2rem;">
                <!-- Review List -->
                <div>
                    <c:choose>
                        <c:when test="${empty reviews}">
                            <div class="card" style="text-align: center; padding: 2.5rem;">
                                <p style="color: var(--text-muted);">No reviews submitted for this product yet.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div style="display: flex; flex-direction: column; gap: 1rem;">
                                <c:forEach var="r" items="${reviews}">
                                    <div class="card">
                                        <div class="flex items-center justify-between" style="margin-bottom: 0.5rem;">
                                            <div>
                                                <strong><c:out value="${r.userName}" /></strong>
                                                <span style="background: #d1fae5; color: #065f46; font-size: 0.7rem; font-weight: 700; padding: 2px 6px; border-radius: 4px; margin-left: 6px;">
                                                    VERIFIED PURCHASE
                                                </span>
                                            </div>
                                            <div class="rating-stars" style="font-size: 0.875rem;">
                                                <c:forEach begin="1" end="${r.rating}">★</c:forEach><c:forEach begin="${r.rating + 1}" end="5">☆</c:forEach>
                                            </div>
                                        </div>
                                        <p style="color: var(--text-secondary); line-height: 1.5; font-size: 0.925rem;"><c:out value="${r.comment}" /></p>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Review Submission Box (R2025 F8 rule) -->
                <div>
                    <div class="card">
                        <h3 style="font-size: 1.125rem; font-weight: 700; margin-bottom: 0.75rem;">Leave a Review</h3>
                        <c:choose>
                            <c:when test="${empty sessionScope.currentUser}">
                                <p style="font-size: 0.875rem; color: var(--text-muted); margin-bottom: 1rem;">
                                    Please sign in to share your product experience.
                                </p>
                                <a href="${pageContext.request.contextPath}/login?redirect=${pageContext.request.requestURI}?id=${product.id}" class="btn btn-outline btn-sm" style="width: 100%;">
                                    Sign In to Review
                                </a>
                            </c:when>
                            <c:when test="${canReview}">
                                <form id="review-form" onsubmit="handleReviewSubmit(event, ${product.id})">
                                    <div style="margin-bottom: 1rem;">
                                        <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Rating</label>
                                        <select id="review-rating" class="search-input" style="padding: 0.5rem; border-radius: 6px; width: 100%;" required>
                                            <option value="5">★★★★★ (5 Stars - Excellent)</option>
                                            <option value="4">★★★★☆ (4 Stars - Good)</option>
                                            <option value="3">★★★☆☆ (3 Stars - Average)</option>
                                            <option value="2">★★☆☆☆ (2 Stars - Poor)</option>
                                            <option value="1">★☆☆☆☆ (1 Star - Terrible)</option>
                                        </select>
                                    </div>
                                    <div style="margin-bottom: 1rem;">
                                        <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Comment</label>
                                        <textarea id="review-comment" class="search-input" style="padding: 0.5rem; border-radius: 6px; width: 100%; min-height: 80px; resize: vertical;" placeholder="Write your verified feedback..." required></textarea>
                                    </div>
                                    <button type="submit" class="btn btn-primary btn-sm" style="width: 100%;">Submit Verified Review</button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <div style="background-color: #f1f5f9; border-radius: 6px; padding: 1rem; font-size: 0.85rem; color: var(--text-secondary); line-height: 1.5;">
                                    ℹ️ Per project specification (R2025 F8), star ratings and reviews are restricted to verified buyers who have placed an order for this item.
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </section>
    </main>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />
    <jsp:include page="/WEB-INF/views/common/chat-widget.jsp" />

    <script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            LakshanMart.init('${pageContext.request.contextPath}');
        });

        function adjustDetailQty(delta) {
            const input = document.getElementById('detail-qty');
            if (input) {
                let val = parseInt(input.value) + delta;
                if (val >= 1 && val <= parseInt(input.max)) {
                    input.value = val;
                }
            }
        }

        function addDetailToCart(productId) {
            const qty = document.getElementById('detail-qty') ? parseInt(document.getElementById('detail-qty').value) : 1;
            LakshanMart.addToCart(productId, qty, true);
        }

        async function buyNowDetail(productId) {
            const qty = document.getElementById('detail-qty') ? parseInt(document.getElementById('detail-qty').value) : 1;
            const success = await LakshanMart.addToCart(productId, qty, false);
            if (success) {
                window.location.href = '${pageContext.request.contextPath}/checkout';
            }
        }

        function handleReviewSubmit(e, productId) {
            e.preventDefault();
            const rating = document.getElementById('review-rating').value;
            const comment = document.getElementById('review-comment').value;
            LakshanMart.submitReview(productId, rating, comment);
        }
    </script>
</body>
</html>
