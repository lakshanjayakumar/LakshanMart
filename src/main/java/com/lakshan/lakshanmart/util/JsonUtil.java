package com.lakshan.lakshanmart.util;

import com.google.gson.*;
import com.lakshan.lakshanmart.dto.ApiResponse;

import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility for JSON serialization/deserialization using Google Gson.
 */
public final class JsonUtil {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private static final Gson GSON = new GsonBuilder()
            .setDateFormat(DATE_FORMAT)
            .registerTypeAdapter(Timestamp.class, (JsonSerializer<Timestamp>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(new SimpleDateFormat(DATE_FORMAT).format(src)))
            .registerTypeAdapter(Timestamp.class, (JsonDeserializer<Timestamp>) (json, typeOfT, context) -> {
                try {
                    return new Timestamp(new SimpleDateFormat(DATE_FORMAT).parse(json.getAsString()).getTime());
                } catch (Exception e) {
                    return new Timestamp(json.getAsLong());
                }
            })
            .serializeNulls()
            .create();

    private JsonUtil() {
    }

    public static Gson getGson() {
        return GSON;
    }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static <T> T fromJson(BufferedReader reader, Class<T> clazz) {
        return GSON.fromJson(reader, clazz);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return GSON.fromJson(json, typeOfT);
    }

    /**
     * Serializes an ApiResponse envelope to the HttpServletResponse output stream.
     *
     * @param response    servlet HTTP response
     * @param statusCode  HTTP status code (200, 201, 400, 401, 403, 404, 500, etc.)
     * @param apiResponse ApiResponse payload envelope
     * @throws IOException on I/O error
     */
    public static void writeResponse(HttpServletResponse response, int statusCode, ApiResponse<?> apiResponse)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(GSON.toJson(apiResponse));
            writer.flush();
        }
    }
}
