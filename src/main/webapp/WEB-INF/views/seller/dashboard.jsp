<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Seller Dashboard | LakshanMart Merchant Portal</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .seller-stat-grid {
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
            font-size: 1.85rem;
            font-weight: 800;
            color: var(--text-primary);
            line-height: 1.2;
        }
        .stat-label {
            font-size: 0.8125rem;
            color: var(--text-muted);
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        .modal-overlay {
            position: fixed;
            top: 0; left: 0; right: 0; bottom: 0;
            background: rgba(15, 23, 42, 0.6);
            display: none;
            align-items: center;
            justify-content: center;
            z-index: 1000;
            padding: 1rem;
        }
        .modal-overlay.active {
            display: flex;
        }
        .modal-box {
            background: #ffffff;
            border-radius: var(--radius-md);
            padding: 2rem;
            width: 100%;
            max-width: 550px;
            max-height: 90vh;
            overflow-y: auto;
            box-shadow: var(--shadow-lg);
        }
        .seller-tabs {
            display: flex;
            gap: 1rem;
            border-bottom: 2px solid var(--border-color);
            margin-bottom: 1.5rem;
        }
        .seller-tab-btn {
            padding: 0.75rem 1.25rem;
            font-weight: 700;
            color: var(--text-muted);
            border-bottom: 3px solid transparent;
            cursor: pointer;
            background: none;
            border-top: none; border-left: none; border-right: none;
            font-size: 0.95rem;
        }
        .seller-tab-btn.active {
            color: var(--primary);
            border-bottom-color: var(--primary);
        }
    </style>
</head>
<body>

    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <main class="container" style="padding-top: 2rem; padding-bottom: 3.5rem;">
        <!-- Header -->
        <div class="flex items-center justify-between" style="margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem;">
            <div>
                <div class="flex items-center gap-2">
                    <h1 style="font-size: 1.875rem; font-weight: 800;">Seller Central</h1>
                    <span class="badge-status" style="background-color: #dbeafe; color: #1e40af;">Merchant Hub</span>
                </div>
                <p style="color: var(--text-muted); font-size: 0.9rem;">
                    Manage your product inventory, monitor orders, and track your business earnings.
                </p>
            </div>
            <div class="flex gap-2">
                <button type="button" class="btn btn-primary btn-sm" onclick="openAddModal()">
                    + Add New Product
                </button>
                <a href="${pageContext.request.contextPath}/shop" class="btn btn-outline btn-sm">Browse Marketplace &rarr;</a>
            </div>
        </div>

        <!-- Success/Error Feedback -->
        <c:if test="${not empty param.success}">
            <div style="background: #d1fae5; color: #065f46; padding: 0.75rem 1rem; border-radius: 6px; font-size: 0.9rem; margin-bottom: 1.5rem;">
                ✓ <c:out value="${param.success}" />
            </div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div style="background: #fee2e2; color: #991b1b; padding: 0.75rem 1rem; border-radius: 6px; font-size: 0.9rem; margin-bottom: 1.5rem;">
                ✕ <c:out value="${param.error}" />
            </div>
        </c:if>

        <!-- KPI Metrics Grid -->
        <div class="seller-stat-grid">
            <div class="stat-card">
                <div>
                    <div class="stat-label">Active Listings</div>
                    <div class="stat-val"><c:out value="${totalProducts}" /></div>
                </div>
                <div style="font-size: 2.5rem; opacity: 0.85;">📦</div>
            </div>
            <div class="stat-card">
                <div>
                    <div class="stat-label">Units Sold</div>
                    <div class="stat-val" style="color: var(--primary);"><c:out value="${totalUnitsSold}" /></div>
                </div>
                <div style="font-size: 2.5rem; opacity: 0.85;">🛍️</div>
            </div>
            <div class="stat-card">
                <div>
                    <div class="stat-label">Customer Orders</div>
                    <div class="stat-val"><c:out value="${totalOrders}" /></div>
                </div>
                <div style="font-size: 2.5rem; opacity: 0.85;">📋</div>
            </div>
            <div class="stat-card">
                <div>
                    <div class="stat-label">Total Earnings</div>
                    <div class="stat-val" style="color: var(--success);">
                        ₹<fmt:formatNumber value="${totalEarnings}" minFractionDigits="2" maxFractionDigits="2" />
                    </div>
                </div>
                <div style="font-size: 2.5rem; opacity: 0.85;">💰</div>
            </div>
        </div>

        <!-- Tabs -->
        <div class="seller-tabs">
            <button type="button" class="seller-tab-btn active" onclick="switchTab('inventory-tab', this)">
                Your Inventory (${products.size()})
            </button>
            <button type="button" class="seller-tab-btn" onclick="switchTab('orders-tab', this)">
                Orders Placed For Your Products (${soldItems.size()})
            </button>
        </div>

        <!-- Tab 1: Inventory -->
        <div id="inventory-tab" class="seller-tab-content">
            <div class="card" style="padding: 0; overflow: hidden;">
                <table class="cart-table" style="margin: 0;">
                    <thead>
                        <tr>
                            <th>Item Details</th>
                            <th>Category</th>
                            <th>Price</th>
                            <th>Stock</th>
                            <th>Rating</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty products}">
                                <tr>
                                    <td colspan="6" style="text-align: center; padding: 3rem 1rem; color: var(--text-muted);">
                                        <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">📦</div>
                                        <p style="font-weight: 600;">You haven't listed any products yet.</p>
                                        <p style="font-size: 0.85rem; margin-top: 4px;">Click "+ Add New Product" above to create your first listing!</p>
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="p" items="${products}">
                                    <tr>
                                        <td>
                                            <div class="flex items-center gap-3">
                                                <c:choose>
                                                    <c:when test="${not empty p.imageUrl}">
                                                        <img src="<c:out value="${p.imageUrl}" />" alt="" style="width: 48px; height: 48px; object-fit: cover; border-radius: 4px;" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div style="width: 48px; height: 48px; display: flex; align-items: center; justify-content: center; background: #e2e8f0; border-radius: 4px;">📦</div>
                                                    </c:otherwise>
                                                </c:choose>
                                                <div>
                                                    <a href="${pageContext.request.contextPath}/product?id=${p.id}" target="_blank" style="font-weight: 600; color: var(--text-primary);">
                                                        <c:out value="${p.name}" />
                                                    </a>
                                                    <div style="font-size: 0.75rem; color: var(--text-muted);">Listing ID: #${p.id}</div>
                                                </div>
                                            </div>
                                        </td>
                                        <td><span class="product-badge" style="position: static;"><c:out value="${p.category}" /></span></td>
                                        <td style="font-weight: 700; color: var(--primary);">₹<fmt:formatNumber value="${p.price}" minFractionDigits="2" maxFractionDigits="2" /></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${p.stockQty > 5}">
                                                    <span style="color: var(--success); font-weight: 600;">${p.stockQty} in stock</span>
                                                </c:when>
                                                <c:when test="${p.stockQty > 0}">
                                                    <span style="color: #d97706; font-weight: 600;">Only ${p.stockQty} left!</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="out-of-stock" style="font-size: 0.8rem;">Out of stock</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td style="color: #f59e0b; font-size: 0.9rem;">★ <fmt:formatNumber value="${p.rating}" minFractionDigits="1" maxFractionDigits="2" /></td>
                                        <td>
                                            <div class="flex gap-2">
                                                <button type="button" class="btn btn-outline btn-sm"
                                                        onclick="openEditModal(${p.id}, '${p.name.replace("'", "\\'")}', '${p.category}', ${p.price}, ${p.stockQty}, '${p.imageUrl}', '${p.description.replace("'", "\\'")}')">
                                                    Edit
                                                </button>
                                                <form action="${pageContext.request.contextPath}/seller/product/delete" method="post" style="display: inline;" onsubmit="return confirm('Delete this listing permanently?')">
                                                    <input type="hidden" name="id" value="${p.id}" />
                                                    <button type="submit" class="btn btn-outline btn-sm" style="color: var(--danger); border-color: #fee2e2;">
                                                        Delete
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Tab 2: Sold Items & Orders -->
        <div id="orders-tab" class="seller-tab-content" style="display: none;">
            <div class="card" style="padding: 0; overflow: hidden;">
                <table class="cart-table" style="margin: 0;">
                    <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>Date</th>
                            <th>Customer</th>
                            <th>Product Sold</th>
                            <th>Qty</th>
                            <th>Unit Price</th>
                            <th>Your Earnings</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty soldItems}">
                                <tr>
                                    <td colspan="8" style="text-align: center; padding: 3rem 1rem; color: var(--text-muted);">
                                        <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">📋</div>
                                        <p style="font-weight: 600;">No customer orders placed for your products yet.</p>
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="item" items="${soldItems}">
                                    <tr>
                                        <td><strong>#<c:out value="${item.orderId}" /></strong></td>
                                        <td><fmt:formatDate value="${item.orderDate}" pattern="dd MMM yyyy, hh:mm a" /></td>
                                        <td><c:out value="${item.buyerName}" /></td>
                                        <td>
                                            <div class="flex items-center gap-2">
                                                <c:if test="${not empty item.productImageUrl}">
                                                    <img src="<c:out value="${item.productImageUrl}" />" alt="" style="width: 36px; height: 36px; object-fit: cover; border-radius: 4px;" />
                                                </c:if>
                                                <span><c:out value="${item.productName}" /></span>
                                            </div>
                                        </td>
                                        <td><strong>${item.quantity}</strong></td>
                                        <td>₹<fmt:formatNumber value="${item.unitPrice}" minFractionDigits="2" maxFractionDigits="2" /></td>
                                        <td style="font-weight: 700; color: var(--success);">
                                            ₹<fmt:formatNumber value="${item.subtotal}" minFractionDigits="2" maxFractionDigits="2" />
                                        </td>
                                        <td>
                                            <span class="badge-status badge-${item.orderStatus.toLowerCase()}">
                                                <c:out value="${item.orderStatus}" />
                                            </span>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </main>

    <!-- Modal: Add Product -->
    <div id="addModal" class="modal-overlay">
        <div class="modal-box">
            <div class="flex items-center justify-between" style="margin-bottom: 1.25rem;">
                <h2 style="font-size: 1.25rem; font-weight: 700;">List New Product</h2>
                <button type="button" style="background: none; border: none; font-size: 1.5rem; cursor: pointer;" onclick="closeModals()">&times;</button>
            </div>
            <form action="${pageContext.request.contextPath}/seller/product/create" method="post">
                <div style="margin-bottom: 1rem;">
                    <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Product Name</label>
                    <input type="text" name="name" class="search-input" style="padding: 0.65rem 1rem; width: 100%;" required />
                </div>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1rem;">
                    <div>
                        <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Category</label>
                        <select name="category" class="search-category-select" style="width: 100%; padding: 0.65rem;" required>
                            <option value="Electronics">Electronics</option>
                            <option value="Fashion">Fashion</option>
                            <option value="Home & Kitchen">Home & Kitchen</option>
                            <option value="Books">Books</option>
                            <option value="Sports & Fitness">Sports & Fitness</option>
                        </select>
                    </div>
                    <div>
                        <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Price (₹)</label>
                        <input type="number" step="0.01" min="1" name="price" class="search-input" style="padding: 0.65rem 1rem; width: 100%;" placeholder="999.00" required />
                    </div>
                </div>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1rem;">
                    <div>
                        <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Stock Quantity</label>
                        <input type="number" min="0" name="stockQty" class="search-input" style="padding: 0.65rem 1rem; width: 100%;" value="10" required />
                    </div>
                    <div>
                        <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Image URL (Unsplash/Web)</label>
                        <input type="url" name="imageUrl" class="search-input" style="padding: 0.65rem 1rem; width: 100%;" placeholder="https://..." />
                    </div>
                </div>
                <div style="margin-bottom: 1.5rem;">
                    <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Description</label>
                    <textarea name="description" class="search-input" style="padding: 0.65rem; width: 100%; min-height: 80px;" required></textarea>
                </div>
                <button type="submit" class="btn btn-primary" style="width: 100%; padding: 0.75rem;">Publish Product Listing</button>
            </form>
        </div>
    </div>

    <!-- Modal: Edit Product -->
    <div id="editModal" class="modal-overlay">
        <div class="modal-box">
            <div class="flex items-center justify-between" style="margin-bottom: 1.25rem;">
                <h2 style="font-size: 1.25rem; font-weight: 700;">Edit Product Listing</h2>
                <button type="button" style="background: none; border: none; font-size: 1.5rem; cursor: pointer;" onclick="closeModals()">&times;</button>
            </div>
            <form action="${pageContext.request.contextPath}/seller/product/update" method="post">
                <input type="hidden" id="edit-id" name="id" />
                <div style="margin-bottom: 1rem;">
                    <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Product Name</label>
                    <input type="text" id="edit-name" name="name" class="search-input" style="padding: 0.65rem 1rem; width: 100%;" required />
                </div>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1rem;">
                    <div>
                        <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Category</label>
                        <select id="edit-category" name="category" class="search-category-select" style="width: 100%; padding: 0.65rem;" required>
                            <option value="Electronics">Electronics</option>
                            <option value="Fashion">Fashion</option>
                            <option value="Home & Kitchen">Home & Kitchen</option>
                            <option value="Books">Books</option>
                            <option value="Sports & Fitness">Sports & Fitness</option>
                        </select>
                    </div>
                    <div>
                        <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Price (₹)</label>
                        <input type="number" step="0.01" min="1" id="edit-price" name="price" class="search-input" style="padding: 0.65rem 1rem; width: 100%;" required />
                    </div>
                </div>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1rem;">
                    <div>
                        <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Stock Quantity</label>
                        <input type="number" min="0" id="edit-stock" name="stockQty" class="search-input" style="padding: 0.65rem 1rem; width: 100%;" required />
                    </div>
                    <div>
                        <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Image URL</label>
                        <input type="url" id="edit-image" name="imageUrl" class="search-input" style="padding: 0.65rem 1rem; width: 100%;" />
                    </div>
                </div>
                <div style="margin-bottom: 1.5rem;">
                    <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Description</label>
                    <textarea id="edit-description" name="description" class="search-input" style="padding: 0.65rem; width: 100%; min-height: 80px;" required></textarea>
                </div>
                <button type="submit" class="btn btn-primary" style="width: 100%; padding: 0.75rem;">Save Changes</button>
            </form>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />
    <jsp:include page="/WEB-INF/views/common/chat-widget.jsp" />

    <script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            LakshanMart.init('${pageContext.request.contextPath}');
        });

        function switchTab(tabId, btn) {
            document.querySelectorAll('.seller-tab-content').forEach(el => el.style.display = 'none');
            document.querySelectorAll('.seller-tab-btn').forEach(el => el.classList.remove('active'));
            document.getElementById(tabId).style.display = 'block';
            btn.classList.add('active');
        }

        function openAddModal() {
            document.getElementById('addModal').classList.add('active');
        }

        function openEditModal(id, name, category, price, stock, image, description) {
            document.getElementById('edit-id').value = id;
            document.getElementById('edit-name').value = name;
            document.getElementById('edit-category').value = category;
            document.getElementById('edit-price').value = price;
            document.getElementById('edit-stock').value = stock;
            document.getElementById('edit-image').value = image;
            document.getElementById('edit-description').value = description;
            document.getElementById('editModal').classList.add('active');
        }

        function closeModals() {
            document.querySelectorAll('.modal-overlay').forEach(el => el.classList.remove('active'));
        }
    </script>
</body>
</html>
