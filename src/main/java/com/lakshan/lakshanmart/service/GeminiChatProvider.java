package com.lakshan.lakshanmart.service;

import com.google.gson.JsonObject;
import com.lakshan.lakshanmart.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Gemini Chat Provider integrating with Google Gemini API when configured.
 * Strictly implements Section 11 & 17 requirements:
 * <ul>
 *   <li>API key is never committed; read strictly from environment variable or properties</li>
 *   <li>Calls wrapped in try-catch with graceful fallback to MockChatProvider</li>
 *   <li>Strict domain scope instruction prompt</li>
 * </ul>
 */
public class GeminiChatProvider implements ChatProvider {

    private static final Logger logger = LoggerFactory.getLogger(GeminiChatProvider.class);

    private final String apiKey;
    private final MockChatProvider fallback = new MockChatProvider();
    private final HttpClient httpClient;

    public GeminiChatProvider(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public String getReply(String userMessage, String context) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return fallback.getReply(userMessage, context);
        }

        try {
            String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

            String systemPrompt = "You are the AI shopping assistant for LakshanMart, a multi-seller e-commerce marketplace. " +
                    "Assist users with product discovery, order tracking, shipping, returns, and seller onboarding. " +
                    "Keep responses concise (1-3 sentences) and helpful. Do not mention external competing stores.";

            String fullPrompt = systemPrompt + "\nUser question: " + userMessage;

            JsonObject textPart = new JsonObject();
            textPart.addProperty("text", fullPrompt);

            com.google.gson.JsonArray partsArray = new com.google.gson.JsonArray();
            partsArray.add(textPart);

            JsonObject contentObj = new JsonObject();
            contentObj.add("parts", partsArray);

            com.google.gson.JsonArray contentsArray = new com.google.gson.JsonArray();
            contentsArray.add(contentObj);

            JsonObject root = new JsonObject();
            root.add("contents", contentsArray);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(8))
                    .POST(HttpRequest.BodyPublishers.ofString(JsonUtil.toJson(root)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject respJson = JsonUtil.fromJson(response.body(), JsonObject.class);
                String reply = respJson.getAsJsonArray("candidates")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("content")
                        .getAsJsonArray("parts")
                        .get(0).getAsJsonObject()
                        .get("text").getAsString();
                return reply.trim();
            } else {
                logger.warn("Gemini API returned status {}: {}. Falling back to canned response.", response.statusCode(), response.body());
                return fallback.getReply(userMessage, context);
            }
        } catch (Exception e) {
            logger.warn("Failed calling Gemini API ({}). Returning static degraded response.", e.getMessage());
            return fallback.getReply(userMessage, context);
        }
    }
}
