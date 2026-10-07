<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- AI Chatbot Floating Trigger Button -->
<button id="chat-trigger-btn" class="chat-widget-btn" title="Chat with LakshanMart AI Assistant" aria-label="Open AI Chat Assistant">
    💬
</button>

<!-- Sliding AI Chat Drawer Panel -->
<div id="chat-drawer" class="chat-drawer">
    <div class="chat-header">
        <div style="display: flex; align-items: center; gap: 0.5rem;">
            <span style="font-size: 1.25rem;">🤖</span>
            <div>
                <strong style="display: block; font-size: 0.95rem;">LakshanMart AI</strong>
                <span style="font-size: 0.75rem; color: #a5f3fc;">Instant Shopping Assistant</span>
            </div>
        </div>
        <button id="chat-close-btn" style="background: none; border: none; color: #ffffff; font-size: 1.25rem; cursor: pointer;">✕</button>
    </div>

    <!-- Suggested Quick Question Chips -->
    <div style="padding: 0.5rem 0.75rem; background: #f1f5f9; display: flex; gap: 0.35rem; overflow-x: auto; white-space: nowrap; font-size: 0.75rem; border-bottom: 1px solid var(--border-color);">
        <button type="button" class="btn btn-outline btn-sm" style="padding: 2px 8px; font-size: 0.75rem;" onclick="sendSuggestedQuestion('What are your return policies?')">Returns</button>
        <button type="button" class="btn btn-outline btn-sm" style="padding: 2px 8px; font-size: 0.75rem;" onclick="sendSuggestedQuestion('How do I track my order?')">Track Orders</button>
        <button type="button" class="btn btn-outline btn-sm" style="padding: 2px 8px; font-size: 0.75rem;" onclick="sendSuggestedQuestion('How can I become a seller?')">Sell on Mart</button>
        <button type="button" class="btn btn-outline btn-sm" style="padding: 2px 8px; font-size: 0.75rem;" onclick="sendSuggestedQuestion('What payment methods do you accept?')">Payment</button>
    </div>

    <div id="chat-messages" class="chat-messages">
        <div class="chat-bubble bot">
            Hello! I am your LakshanMart AI Assistant. How can I help with your shopping or order today?
        </div>
    </div>

    <form id="chat-form" class="chat-input-area">
        <input type="text" id="chat-input" class="chat-input" placeholder="Ask about products, orders, returns..." maxlength="500" required />
        <button type="submit" class="btn btn-primary btn-sm" style="border-radius: var(--radius-full); padding: 0.4rem 0.9rem;">Send</button>
    </form>
</div>

<script>
    function sendSuggestedQuestion(q) {
        const input = document.getElementById('chat-input');
        if (input) {
            input.value = q;
            document.getElementById('chat-form').dispatchEvent(new Event('submit'));
        }
    }
</script>
