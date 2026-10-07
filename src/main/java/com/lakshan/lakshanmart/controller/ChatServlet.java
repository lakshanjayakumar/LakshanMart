package com.lakshan.lakshanmart.controller;

import com.google.gson.JsonObject;
import com.lakshan.lakshanmart.service.ChatService;
import com.lakshan.lakshanmart.service.ChatServiceImpl;
import com.lakshan.lakshanmart.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * Backend proxy servlet for the AI Chatbot widget (R2025 Section 11 & 17).
 * Validates input, enforces per-session rate limits, and returns JSON.
 * Endpoint: /api/v1/chat
 */
@WebServlet(name = "ChatServlet", urlPatterns = "/api/v1/chat")
public class ChatServlet extends BaseServlet {

    private final ChatService chatService;

    public ChatServlet() {
        this.chatService = new ChatServiceImpl();
    }

    public ChatServlet(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            JsonObject reqJson = JsonUtil.fromJson(req.getReader(), JsonObject.class);
            if (reqJson == null || !reqJson.has("message")) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_INPUT", "Missing 'message' field in request body.");
                return;
            }

            String userMessage = reqJson.get("message").getAsString();
            HttpSession session = req.getSession(true);
            String sessionId = session.getId();

            String reply = chatService.processChat(sessionId, userMessage);
            sendSuccess(resp, HttpServletResponse.SC_OK, Map.of("reply", reply));
        } catch (Exception e) {
            handleException(resp, e);
        }
    }
}
