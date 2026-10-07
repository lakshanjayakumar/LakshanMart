package com.lakshan.lakshanmart.dto;

/**
 * Standard API response envelope adhering strictly to Section 13 API Contract Standards:
 * <pre>
 * { "success": true, "data": { ... }, "error": null }
 * { "success": false, "data": null, "error": { "code": "VALIDATION_ERROR", "message": "..." } }
 * </pre>
 *
 * @param <T> data payload type
 */
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private ApiError error;

    public ApiResponse() {
    }

    private ApiResponse(boolean success, T data, ApiError error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, null, new ApiError(code, message));
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ApiError getError() {
        return error;
    }

    public void setError(ApiError error) {
        this.error = error;
    }
}
