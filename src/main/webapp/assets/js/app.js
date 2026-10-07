/**
 * LakshanMart - Core Frontend JavaScript
 * Modern Vanilla JS with Fetch API
 */

const LakshanMart = {
    contextPath: '',

    init(contextPath) {
        this.contextPath = contextPath || '';
        this.initChatbot();
    },

    // =========================================================================
    // Cart Functionality
    // =========================================================================
    async addToCart(productId, quantity = 1, showToast = true) {
        try {
            const response = await fetch(`${this.contextPath}/api/v1/cart`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ productId: Number(productId), quantity: Number(quantity) })
            });

            const data = await response.json();
            if (response.ok && data.success) {
                this.updateCartBadge(data.data.itemCount);
                if (showToast) {
                    this.showToast('Item added to your cart!', 'success');
                }
                return true;
            } else if (response.status === 401) {
                window.location.href = `${this.contextPath}/login?redirect=${encodeURIComponent(window.location.pathname + window.location.search)}`;
                return false;
            } else {
                this.showToast(data.error ? data.error.message : 'Failed to add item to cart', 'danger');
                return false;
            }
        } catch (error) {
            console.error('Error adding to cart:', error);
            this.showToast('Network error while adding to cart', 'danger');
            return false;
        }
    },

    async updateCartQuantity(cartItemId, newQuantity) {
        try {
            const response = await fetch(`${this.contextPath}/api/v1/cart/${cartItemId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ cartItemId: Number(cartItemId), quantity: Number(newQuantity) })
            });

            const data = await response.json();
            if (response.ok && data.success) {
                window.location.reload();
            } else {
                this.showToast(data.error ? data.error.message : 'Could not update quantity', 'danger');
            }
        } catch (error) {
            console.error('Error updating cart item:', error);
            this.showToast('Network error updating cart', 'danger');
        }
    },

    async removeCartItem(cartItemId) {
        if (!confirm('Are you sure you want to remove this item from your cart?')) return;

        try {
            const response = await fetch(`${this.contextPath}/api/v1/cart/${cartItemId}`, {
                method: 'DELETE'
            });

            const data = await response.json();
            if (response.ok && data.success) {
                window.location.reload();
            } else {
                this.showToast(data.error ? data.error.message : 'Could not remove item', 'danger');
            }
        } catch (error) {
            console.error('Error removing item:', error);
            this.showToast('Network error removing item', 'danger');
        }
    },

    updateCartBadge(count) {
        const badge = document.querySelector('.cart-count');
        if (badge) {
            badge.textContent = count;
            badge.style.display = count > 0 ? 'flex' : 'none';
        }
    },

    // =========================================================================
    // Reviews
    // =========================================================================
    async submitReview(productId, rating, comment) {
        try {
            const response = await fetch(`${this.contextPath}/api/v1/reviews`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ productId: Number(productId), rating: Number(rating), comment: comment })
            });

            const data = await response.json();
            if (response.ok && data.success) {
                this.showToast('Thank you! Your verified review has been published.', 'success');
                setTimeout(() => window.location.reload(), 1200);
            } else {
                this.showToast(data.error ? data.error.message : 'Failed to submit review', 'danger');
            }
        } catch (error) {
            console.error('Error submitting review:', error);
            this.showToast('Network error submitting review', 'danger');
        }
    },

    // =========================================================================
    // AI Chatbot
    // =========================================================================
    initChatbot() {
        const trigger = document.getElementById('chat-trigger-btn');
        const drawer = document.getElementById('chat-drawer');
        const closeBtn = document.getElementById('chat-close-btn');
        const form = document.getElementById('chat-form');
        const input = document.getElementById('chat-input');

        if (!trigger || !drawer) return;

        trigger.addEventListener('click', () => {
            drawer.classList.toggle('open');
            if (drawer.classList.contains('open') && input) {
                input.focus();
            }
        });

        if (closeBtn) {
            closeBtn.addEventListener('click', () => drawer.classList.remove('open'));
        }

        if (form && input) {
            form.addEventListener('submit', async (e) => {
                e.preventDefault();
                const msg = input.value.trim();
                if (!msg) return;

                this.appendChatMessage(msg, 'user');
                input.value = '';

                // Typing placeholder
                const loadingId = this.appendChatMessage('Thinking...', 'bot typing');

                try {
                    const response = await fetch(`${this.contextPath}/api/v1/chat`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ message: msg })
                    });

                    const data = await response.json();
                    this.removeChatMessage(loadingId);

                    if (response.ok && data.success) {
                        this.appendChatMessage(data.data.reply, 'bot');
                    } else {
                        const errMsg = data.error ? data.error.message : "I am temporarily having trouble responding. Please ask again in a moment.";
                        this.appendChatMessage(errMsg, 'bot');
                    }
                } catch (err) {
                    this.removeChatMessage(loadingId);
                    this.appendChatMessage("Network error connecting to assistant.", 'bot');
                }
            });
        }
    },

    appendChatMessage(text, type) {
        const container = document.getElementById('chat-messages');
        if (!container) return null;

        const id = 'msg-' + Date.now();
        const bubble = document.createElement('div');
        bubble.id = id;
        bubble.className = `chat-bubble ${type}`;
        bubble.textContent = text;

        container.appendChild(bubble);
        container.scrollTop = container.scrollHeight;
        return id;
    },

    removeChatMessage(id) {
        if (!id) return;
        const elem = document.getElementById(id);
        if (elem) elem.remove();
    },

    // =========================================================================
    // UI Helpers (Toast notification)
    // =========================================================================
    showToast(message, type = 'info') {
        let toastContainer = document.getElementById('toast-container');
        if (!toastContainer) {
            toastContainer = document.createElement('div');
            toastContainer.id = 'toast-container';
            toastContainer.style.cssText = 'position: fixed; top: 20px; right: 20px; z-index: 9999; display: flex; flex-direction: column; gap: 8px;';
            document.body.appendChild(toastContainer);
        }

        const toast = document.createElement('div');
        const bg = type === 'success' ? '#10b981' : (type === 'danger' ? '#ef4444' : '#2563eb');
        toast.style.cssText = `background: ${bg}; color: white; padding: 12px 20px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.15); font-size: 0.9rem; font-weight: 500; transition: opacity 0.3s;`;
        toast.textContent = message;

        toastContainer.appendChild(toast);
        setTimeout(() => {
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    }
};
