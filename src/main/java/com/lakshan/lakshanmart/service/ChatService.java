package com.lakshan.lakshanmart.service;

/**
 * Service interface managing chatbot interaction, rate limiting, and question caching.
 */
public interface ChatService {

    /**
     * Processes an incoming chat message with rate limiting and security guardrails.
     *
     * @param sessionId   user's HTTP session ID
     * @param userMessage user's question
     * @return AI reply
     */
    String processChat(String sessionId, String userMessage);
}
