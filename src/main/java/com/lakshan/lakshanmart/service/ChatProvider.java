package com.lakshan.lakshanmart.service;

/**
 * AI Chatbot provider interface strictly matching Section 17 requirement.
 */
public interface ChatProvider {

    /**
     * Obtains an AI or rule-based response for a customer query within the LakshanMart domain.
     *
     * @param userMessage user's question or message
     * @param context     current store or catalog context
     * @return response string
     */
    String getReply(String userMessage, String context);
}
