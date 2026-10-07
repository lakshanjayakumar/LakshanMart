<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Product Management | LakshanMart Admin</title>
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
        .modal-overlay {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
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
    </style>
</head>
<body>

    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <main class="container" style="padding-top: 2rem; padding-bottom: 3rem;">
        <div class="flex items-center justify-between" style="margin-bottom: 1.5rem;">
            <div>
                <h1 style="font-size: 1.875rem; font-weight: 800;">Product Inventory & Catalog</h1>
                <p style="color: var(--text-muted); font-size: 0.9rem;">Create, update, and manage marketplace product offerings</p>
            </div>
            <button type="button" class="btn btn-primary btn-sm" onclick="openAddProductModal()">
                + Add New Product
            </button>
        </div>

        <!-- Navigation Tabs -->
        <nav class="admin-nav">
            <a href="${pageContext.request.contextPath}/admin">Overview Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/products" class="active">Product Catalog</a>
            <a href="${pageContext.request.contextPath}/admin/orders">Order Management</a>
            <a href="${pageContext.request.contextPath}/admin/users">User Directory</a>
        </nav>

        <!-- Products Table -->
        <div class="card" style="padding: 0; overflow: hidden;">
            <table class="cart-table" style="margin: 0;">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Product Details</th>
                        <th>Category</th>
                        <th>Price</th>
                        <th>Stock</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty products}">
                            <tr>
                                <td colspan="6" style="text-align: center; padding: 2rem; color: var(--text-muted);">
                                    No products found in catalog. Click "+ Add New Product" to list one.
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="p" items="${products}">
                                <tr>
                                    <td><c:out value="${p.id}" /></td>
                                    <td>
                                        <div class="flex items-center gap-3">
                                            <c:choose>
                                                <c:when test="${not empty p.imageUrl}">
                                                    <img src="<c:out value="${p.imageUrl}" />" alt="" style="width: 48px; height: 48px; object-fit: cover; border-radius: 4px; background: #f1f5f9;" />
                                                </c:when>
                                                <c:otherwise>
                                                    <div style="width: 48px; height: 48px; display: flex; align-items: center; justify-content: center; background: #e2e8f0; border-radius: 4px;">📦</div>
                                                </c:otherwise>
                                            </c:choose>
                                            <div>
                                                <a href="${pageContext.request.contextPath}/product?id=${p.id}" target="_blank" style="font-weight: 600; color: var(--text-primary);">
                                                    <c:out value="${p.name}" />
                                                </a>
                                                <div style="font-size: 0.75rem; color: var(--text-muted); max-width: 300px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                                                    <c:out value="${p.description}" />
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <span class="product-badge" style="position: static; display: inline-block;">
                                            <c:out value="${p.category}" />
                                        </span>
                                    </td>
                                    <td style="font-weight: 700;">
                                        ₹<fmt:formatNumber value="${p.price}" minFractionDigits="2" maxFractionDigits="2" />
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${p.stockQty > 0}">
                                                <span style="color: var(--success); font-weight: 600;"><c:out value="${p.stockQty}" /> in stock</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: var(--danger); font-weight: 600;">Out of Stock</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="flex gap-2">
                                            <button type="button" class="btn btn-outline btn-sm"
                                                    onclick="openEditProductModal('${p.id}', '${p.name}', '${p.category}', '${p.price}', '${p.stockQty}', '${p.imageUrl}', '${p.description}')">
                                                Edit
                                            </button>
                                            <button type="button" class="btn btn-outline btn-sm" style="color: var(--danger);"
                                                    onclick="deleteProduct(${p.id})">
                                                Delete
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </main>

    <!-- Add/Edit Product Modal Dialog -->
    <div id="product-modal" class="modal-overlay">
        <div class="modal-box">
            <div class="flex items-center justify-between" style="margin-bottom: 1.25rem;">
                <h2 id="modal-title" style="font-size: 1.25rem; font-weight: 700;">Add New Product</h2>
                <button type="button" style="background: none; border: none; font-size: 1.5rem; cursor: pointer; color: var(--text-muted);" onclick="closeProductModal()">&times;</button>
            </div>

            <form id="product-form" onsubmit="handleProductSubmit(event)">
                <input type="hidden" id="prod-id" />

                <div style="margin-bottom: 1rem;">
                    <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Product Name</label>
                    <input type="text" id="prod-name" class="search-input" style="padding: 0.65rem 1rem; border-radius: 6px; width: 100%;" required />
                </div>

                <div style="margin-bottom: 1rem;">
                    <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Category</label>
                    <select id="prod-category" class="search-input" style="padding: 0.65rem 1rem; border-radius: 6px; width: 100%;" required>
                        <option value="Electronics">Electronics</option>
                        <option value="Fashion">Fashion</option>
                        <option value="Home & Kitchen">Home & Kitchen</option>
                        <option value="Books">Books</option>
                        <option value="Sports & Fitness">Sports & Fitness</option>
                    </select>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1rem;">
                    <div>
                        <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Price (₹)</label>
                        <input type="number" step="0.01" min="0.01" id="prod-price" class="search-input" style="padding: 0.65rem 1rem; border-radius: 6px; width: 100%;" required />
                    </div>
                    <div>
                        <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Stock Quantity</label>
                        <input type="number" min="0" id="prod-stock" class="search-input" style="padding: 0.65rem 1rem; border-radius: 6px; width: 100%;" required />
                    </div>
                </div>

                <div style="margin-bottom: 1rem;">
                    <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Image URL</label>
                    <input type="url" id="prod-image" class="search-input" style="padding: 0.65rem 1rem; border-radius: 6px; width: 100%;"
                           placeholder="https://images.unsplash.com/..." />
                </div>

                <div style="margin-bottom: 1.5rem;">
                    <label style="font-size: 0.875rem; font-weight: 600; display: block; margin-bottom: 0.35rem;">Description</label>
                    <textarea id="prod-desc" class="search-input" style="padding: 0.65rem 1rem; border-radius: 6px; width: 100%; min-height: 80px; resize: vertical;" required></textarea>
                </div>

                <div class="flex justify-between">
                    <button type="button" class="btn btn-outline" onclick="closeProductModal()">Cancel</button>
                    <button type="submit" id="save-product-btn" class="btn btn-primary">Save Product</button>
                </div>
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

        const modal = document.getElementById('product-modal');
        const modalTitle = document.getElementById('modal-title');
        const form = document.getElementById('product-form');

        function openAddProductModal() {
            modalTitle.textContent = 'Add New Product';
            document.getElementById('prod-id').value = '';
            form.reset();
            modal.classList.add('active');
        }

        function openEditProductModal(id, name, category, price, stock, image, desc) {
            modalTitle.textContent = 'Edit Product #' + id;
            document.getElementById('prod-id').value = id;
            document.getElementById('prod-name').value = name;
            document.getElementById('prod-category').value = category;
            document.getElementById('prod-price').value = price;
            document.getElementById('prod-stock').value = stock;
            document.getElementById('prod-image').value = image || '';
            document.getElementById('prod-desc').value = desc;
            modal.classList.add('active');
        }

        function closeProductModal() {
            modal.classList.remove('active');
        }

        async function handleProductSubmit(e) {
            e.preventDefault();
            const btn = document.getElementById('save-product-btn');
            btn.disabled = true;

            const id = document.getElementById('prod-id').value;
            const name = document.getElementById('prod-name').value.trim();
            const category = document.getElementById('prod-category').value;
            const price = parseFloat(document.getElementById('prod-price').value);
            const stockQty = parseInt(document.getElementById('prod-stock').value);
            const imageUrl = document.getElementById('prod-image').value.trim();
            const description = document.getElementById('prod-desc').value.trim();

            const isEdit = !!id;
            const url = isEdit
                ? '${pageContext.request.contextPath}/api/v1/admin/products/' + id
                : '${pageContext.request.contextPath}/api/v1/admin/products';
            const method = isEdit ? 'PUT' : 'POST';

            const payload = isEdit
                ? { id: Number(id), name, description, price, category, stockQty, imageUrl }
                : { name, description, price, category, stockQty, imageUrl };

            try {
                const response = await fetch(url, {
                    method: method,
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                const data = await response.json();
                if (response.ok && data.success) {
                    LakshanMart.showToast(isEdit ? 'Product updated successfully!' : 'Product created successfully!', 'success');
                    setTimeout(() => window.location.reload(), 800);
                } else {
                    btn.disabled = false;
                    LakshanMart.showToast(data.error ? data.error.message : 'Operation failed', 'danger');
                }
            } catch (err) {
                btn.disabled = false;
                LakshanMart.showToast('Network error saving product', 'danger');
            }
        }

        async function deleteProduct(productId) {
            if (!confirm('Are you sure you want to delete this product? This action cannot be undone.')) {
                return;
            }

            try {
                const response = await fetch('${pageContext.request.contextPath}/api/v1/admin/products/' + productId, {
                    method: 'DELETE'
                });

                const data = await response.json();
                if (response.ok && data.success) {
                    LakshanMart.showToast('Product deleted successfully', 'success');
                    setTimeout(() => window.location.reload(), 800);
                } else {
                    LakshanMart.showToast(data.error ? data.error.message : 'Could not delete product', 'danger');
                }
            } catch (err) {
                LakshanMart.showToast('Network error deleting product', 'danger');
            }
        }
    </script>
</body>
</html>
