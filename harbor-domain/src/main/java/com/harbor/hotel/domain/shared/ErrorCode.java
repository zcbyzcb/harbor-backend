package com.harbor.hotel.domain.shared;

/**
 * Stable error codes returned by the hotel API.
 *
 * <p>Codes are part of the frontend/backend contract. Add new codes here before using them in a
 * domain exception or HTTP response.
 */
public final class ErrorCode {
    public static final String SUCCESS = "SUCCESS";
    public static final String INVALID_ARGUMENT = "INVALID_ARGUMENT";
    public static final String INVALID_AMOUNT = "INVALID_AMOUNT";
    public static final String INVALID_GUESTS = "INVALID_GUESTS";
    public static final String INVALID_IDEMPOTENCY_KEY = "INVALID_IDEMPOTENCY_KEY";
    public static final String INVALID_ORDER_NO = "INVALID_ORDER_NO";
    public static final String INVALID_STAY_PERIOD = "INVALID_STAY_PERIOD";
    public static final String ROOM_COUNT_MISMATCH = "ROOM_COUNT_MISMATCH";
    public static final String BOOKING_WINDOW_INVALID = "BOOKING_WINDOW_INVALID";
    public static final String CHECKIN_TIME_INVALID = "CHECKIN_TIME_INVALID";
    public static final String PRICE_CHANGED = "PRICE_CHANGED";
    public static final String ORDER_NOT_FOUND = "ORDER_NOT_FOUND";
    public static final String ROOM_TYPE_NOT_FOUND = "ROOM_TYPE_NOT_FOUND";
    public static final String ORDER_STATUS_CONFLICT = "ORDER_STATUS_CONFLICT";
    public static final String ORDER_STATE_CONFLICT = "ORDER_STATE_CONFLICT";
    public static final String IDEMPOTENCY_CONFLICT = "IDEMPOTENCY_CONFLICT";
    public static final String INVENTORY_NOT_READY = "INVENTORY_NOT_READY";
    public static final String INVENTORY_NOT_AVAILABLE = "INVENTORY_NOT_AVAILABLE";
    public static final String INVENTORY_STATE_CONFLICT = "INVENTORY_STATE_CONFLICT";
    public static final String INVENTORY_DATA_INCONSISTENT = "INVENTORY_DATA_INCONSISTENT";
    public static final String ROOM_NOT_AVAILABLE = "ROOM_NOT_AVAILABLE";
    public static final String ORDER_NO_GENERATION_EXHAUSTED = "ORDER_NO_GENERATION_EXHAUSTED";
    public static final String LOGIN_FAILED = "LOGIN_FAILED";
    public static final String LOGIN_RATE_LIMITED = "LOGIN_RATE_LIMITED";
    public static final String UNAUTHENTICATED = "UNAUTHENTICATED";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String CSRF_INVALID = "CSRF_INVALID";
    public static final String RETRY_SAME_REQUEST = "RETRY_SAME_REQUEST";
    public static final String RESULT_UNKNOWN = "RESULT_UNKNOWN";

    private ErrorCode() {}
}
