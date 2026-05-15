package com.quang.smart_recipe.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // ── Auth ─────────────────────────────────────────────────
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"),
    USERNAME_EXISTED(HttpStatus.CONFLICT, "Tên đăng nhập đã tồn tại"),
    EMAIL_EXISTED(HttpStatus.CONFLICT, "Email này đã được sử dụng"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Sai tên đăng nhập hoặc mật khẩu"),
    GOOGLE_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "Đăng nhập bằng Google thất bại"),
    INVALID_OTP(HttpStatus.BAD_REQUEST, "Mã OTP không hợp lệ hoặc đã hết hạn"),

    // ── Recipe ───────────────────────────────────────────────
    RECIPE_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy công thức"),
    RECIPE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện thao tác này với công thức"),

    // ── Fridge ───────────────────────────────────────────────
    FRIDGE_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy nguyên liệu trong tủ lạnh"),

    // ── Shopping Cart ─────────────────────────────────────────
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy mặt hàng trong giỏ"),

    // ── File ─────────────────────────────────────────────────
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể tải file lên"),

    // ── Generic ──────────────────────────────────────────────
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Yêu cầu không hợp lệ"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện thao tác này"),
    MEAL_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy kế hoạch bữa ăn"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống, vui lòng thử lại sau");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() { return status; }
    public String getMessage() { return message; }
}
