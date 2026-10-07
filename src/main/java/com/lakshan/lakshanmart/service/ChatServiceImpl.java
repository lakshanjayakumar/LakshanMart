package com.lakshan.lakshanmart.service;

import com.lakshan.lakshanmart.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of ChatService enforcing Section 17 guardrails:
 * <ul>
 *   <li>Per-session rate limit (10 messages per minute)</li>
 *   <li>Input length cap (500 characters)</li>
 *   <li>Repeated identical questions cached in-memory per session</li>
 *   <li>Configurable provider (gemini or mock)</li>
 * </ul>
 */
public class ChatServiceImpl implements ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);

    private static final int MAX_INPUT_LENGTH = 500;
    private static final int MAX_MESSAGES_PER_MINUTE = 10;
    private static final long ONE_MINUTE_MILLIS = 60_000L;

    private final ChatProvider provider;

    // Per-session message timestamps for sliding window rate limiting
    private final Map<String, SessionRateTracker> rateLimitMap = new ConcurrentHashMap<>();

    // Per-session query cache for repeated queries
    private final Map<String, Map<String, String>> sessionCacheMap = new ConcurrentHashMap<>();

    public ChatServiceImpl() {
        String providerType = System.getenv("AI_CHATBOT_PROVIDER");
        if (providerType == null || providerType.isBlank()) {
            providerType = System.getProperty("ai.chatbot.provider", "mock");
        }

        String geminiApiKey = System.getenv("GEMINI_API_KEY");
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            geminiApiKey = System.getProperty("ai.gemini.api.key", "");
        }

        if ("gemini".equalsIgnoreCase(providerType) && !geminiApiKey.isBlank()) {
            this.provider = new GeminiChatProvider(geminiApiKey);
            logger.info("ChatService initialized with GeminiChatProvider");
        } else {
            this.provider = new MockChatProvider();
            logger.info("ChatService initialized with MockChatProvider");
        }
    }

    public ChatServiceImpl(ChatProvider provider) {
        this.provider = provider;
    }

    @Override
    public String processChat(String sessionId, String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Please type a question or request to get started!";
        }

        String trimmed = userMessage.trim();

        // 1. Guardrail: Input length cap
        if (trimmed.length() > MAX_INPUT_LENGTH) {
            throw new ValidationException("Message exceeds maximum allowed length of " + MAX_INPUT_LENGTH + " characters.");
        }

        // 2. Guardrail: Per-session rate limit (10 messages/minute)
        String sessionKey = (sessionId != null) ? sessionId : "anonymous";
        SessionRateTracker tracker = rateLimitMap.computeIfAbsent(sessionKey, k -> new SessionRateTracker());
        if (!tracker.allowMessage()) {
            throw new ValidationException("Rate limit exceeded: maximum 10 messages per minute. Please wait a moment before sending another message.");
        }

        // 3. Guardrail: In-memory cache for repeated identical questions per session
        Map<String, String> cache = sessionCacheMap.computeIfAbsent(sessionKey, k -> new ConcurrentHashMap<>());
        String normalizedQuery = trimmed.toLowerCase();
        if (cache.containsKey(normalizedQuery)) {
            logger.debug("Serving cached chatbot response for query: {}", normalizedQuery);
            return cache.get(normalizedQuery);
        }

        String reply = provider.getReply(trimmed, "Store Catalog: LakshanMart Multi-Seller Marketplace");
        cache.put(normalizedQuery, reply);
        return reply;
    }

    private static class SessionRateTracker {
        private long windowStart = System.currentTimeMillis();
        private int count = 0;

        synchronized boolean allowMessage() {
            long now = System.currentTimeMillis();
            if (now - windowStart > ONE_MINUTE_MILLIS) {
                windowStart = now;
                count = 1;
                return true;
            }
            if (count < MAX_MESSAGES_PER_MINUTE) {
                count++;
                return true;
            }
            return false;
        }
    }
}
