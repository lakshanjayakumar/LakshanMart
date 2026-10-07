<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Checkout | LakshanMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <main class="container" style="padding-top: 2rem; padding-bottom: 3rem;">
        <h1 style="font-size: 1.875rem; font-weight: 800; margin-bottom: 1.5rem;">Secure Checkout</h1>

        <div class="cart-layout">
            <!-- Checkout Form -->
            <div>
                <form id="checkout-form" onsubmit="handleCheckout(event)" class="card">
                    <h2 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 1.25rem;">1. Shipping Address</h2>

                    <div style="margin-bottom: 1rem;">
                        <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Full Delivery Address</label>
                        <textarea id="shipping-address" class="search-input" style="padding: 0.75rem; border-radius: 6px; width: 100%; min-height: 90px; resize: vertical;"
                                  placeholder="House/Apartment #, Street, City, State, PIN/ZIP Code" required>42 Ocean View Blvd, Guindy Tech Zone, Chennai, Tamil Nadu 600032</textarea>
                    </div>

                    <h2 style="font-size: 1.25rem; font-weight: 700; margin: 1.75rem 0 1rem;">2. Mock Payment Method</h2>
                    <p style="font-size: 0.8125rem; color: var(--text-muted); margin-bottom: 1rem;">
                        (Simulation mode per R2025 Spec Section 1: No actual charges will be processed)
                    </p>

                    <div style="display: flex; flex-direction: column; gap: 0.75rem; margin-bottom: 1.5rem;">
                        <label style="display: flex; align-items: center; gap: 0.75rem; padding: 0.85rem; border: 1px solid var(--border-color); border-radius: 6px; cursor: pointer;">
                            <input type="radio" name="payment-method" value="UPI" checked />
                            <div>
                                <strong>UPI / Instant Transfer</strong>
                                <div style="font-size: 0.75rem; color: var(--text-muted);">Simulated instant mock confirmation</div>
                            </div>
                        </label>
                        <label style="display: flex; align-items: center; gap: 0.75rem; padding: 0.85rem; border: 1px solid var(--border-color); border-radius: 6px; cursor: pointer;">
                            <input type="radio" name="payment-method" value="CARD" />
                            <div>
                                <strong>Credit / Debit Card</strong>
                                <div style="font-size: 0.75rem; color: var(--text-muted);">Simulated Visa/Mastercard payment</div>
                            </div>
                        </label>
                        <label style="display: flex; align-items: center; gap: 0.75rem; padding: 0.85rem; border: 1px solid var(--border-color); border-radius: 6px; cursor: pointer;">
                            <input type="radio" name="payment-method" value="COD" />
                            <div>
                                <strong>Cash on Delivery (COD)</strong>
                                <div style="font-size: 0.75rem; color: var(--text-muted);">Pay upon delivery</div>
                            </div>
                        </label>
                    </div>

                    <button type="submit" id="place-order-btn" class="btn btn-primary" style="width: 100%; padding: 0.85rem; font-size: 1.05rem;">
                        Confirm & Place Order &rarr;
                    </button>
                </form>
            </div>

            <!-- Order Review Sidebar -->
            <div>
                <div class="card">
                    <h3 style="font-size: 1.125rem; font-weight: 700; margin-bottom: 1rem;">Review Items (${cart.itemCount})</h3>
                    <div style="max-height: 240px; overflow-y: auto; display: flex; flex-direction: column; gap: 0.75rem; margin-bottom: 1.25rem;">
                        <c:forEach var="item" items="${cart.items}">
                            <div class="flex items-center justify-between" style="font-size: 0.875rem;">
                                <div class="flex items-center gap-2" style="max-width: 70%;">
                                    <span style="font-weight: 600;">${item.quantity}x</span>
                                    <span style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"><c:out value="${item.productName}" /></span>
                                </div>
                                <strong>₹<fmt:formatNumber value="${item.subtotal}" minFractionDigits="2" maxFractionDigits="2" /></strong>
                            </div>
                        </c:forEach>
                    </div>

                    <hr style="border: none; border-top: 1px solid var(--border-color); margin: 0.75rem 0;" />
                    <div class="flex justify-between" style="margin-bottom: 0.5rem; color: var(--text-secondary);">
                        <span>Subtotal</span>
                        <strong>₹<fmt:formatNumber value="${cart.totalAmount}" minFractionDigits="2" maxFractionDigits="2" /></strong>
                    </div>
                    <div class="flex justify-between" style="margin-bottom: 0.5rem; color: var(--text-secondary);">
                        <span>Shipping</span>
                        <span style="color: var(--success); font-weight: 600;">FREE</span>
                    </div>
                    <div class="flex justify-between" style="font-size: 1.25rem; font-weight: 800; margin-top: 0.75rem;">
                        <span>Order Total</span>
                        <span style="color: var(--primary);">₹<fmt:formatNumber value="${cart.totalAmount}" minFractionDigits="2" maxFractionDigits="2" /></span>
                    </div>
                </div>
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

        async function handleCheckout(e) {
            e.preventDefault();
            const btn = document.getElementById('place-order-btn');
            btn.disabled = true;
            btn.textContent = 'Processing Payment...';

            const shippingAddress = document.getElementById('shipping-address').value;
            const paymentMethod = document.querySelector('input[name="payment-method"]:checked').value;

            try {
                const response = await fetch('${pageContext.request.contextPath}/api/v1/orders/checkout', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ shippingAddress, paymentMethod })
                });

                const data = await response.json();
                if (response.ok && data.success) {
                    LakshanMart.showToast('Order confirmed successfully!', 'success');
                    setTimeout(() => {
                        window.location.href = '${pageContext.request.contextPath}/order-confirmation?orderId=' + data.data.id;
                    }, 800);
                } else {
                    btn.disabled = false;
                    btn.textContent = 'Confirm & Place Order →';
                    LakshanMart.showToast(data.error ? data.error.message : 'Checkout failed', 'danger');
                }
            } catch (err) {
                btn.disabled = false;
                btn.textContent = 'Confirm & Place Order →';
                LakshanMart.showToast('Network error during checkout', 'danger');
            }
        }
    </script>
</body>
</html>
