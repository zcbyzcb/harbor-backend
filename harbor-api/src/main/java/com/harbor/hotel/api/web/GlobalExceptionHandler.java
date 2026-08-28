package com.harbor.hotel.api.web;

import com.harbor.hotel.domain.shared.DomainException;
import com.harbor.hotel.domain.shared.ErrorCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Void>> domain(DomainException ex) {
        int status =
                switch (ex.code()) {
                    case ErrorCode.LOGIN_FAILED, ErrorCode.UNAUTHENTICATED -> 401;
                    case ErrorCode.LOGIN_RATE_LIMITED -> 429;
                    case ErrorCode.ORDER_NOT_FOUND, ErrorCode.ROOM_TYPE_NOT_FOUND -> 404;
                    case ErrorCode.INVENTORY_NOT_READY, ErrorCode.INVENTORY_DATA_INCONSISTENT -> 503;
                    case ErrorCode.ORDER_STATUS_CONFLICT,
                            ErrorCode.ORDER_STATE_CONFLICT,
                            ErrorCode.IDEMPOTENCY_CONFLICT,
                            ErrorCode.INVENTORY_NOT_AVAILABLE,
                            ErrorCode.INVENTORY_STATE_CONFLICT,
                            ErrorCode.ROOM_NOT_AVAILABLE,
                            ErrorCode.PRICE_CHANGED ->
                            409;
                    default -> 400;
                };
        return response(status, ex.code());
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        ServletRequestBindingException.class,
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiResponse<Void>> invalid(Exception ex) {
        return response(400, ErrorCode.INVALID_ARGUMENT);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResponse<Void>> duplicate(DuplicateKeyException ex) {
        return response(409, ErrorCode.IDEMPOTENCY_CONFLICT);
    }

    @ExceptionHandler(CannotAcquireLockException.class)
    public ResponseEntity<ApiResponse<Void>> busy(CannotAcquireLockException ex) {
        return response(409, ErrorCode.RETRY_SAME_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> unexpected(Exception ex) {
        // Database exception messages can contain guest data or credentials; log safe type and
        // location only.
        LOG.error(
                "operation=HTTP_REQUEST result=FAILED type={} location={}",
                ex.getClass().getSimpleName(),
                ex.getStackTrace().length == 0 ? "unknown" : ex.getStackTrace()[0]);
        return response(503, ErrorCode.RESULT_UNKNOWN);
    }

    private ResponseEntity<ApiResponse<Void>> response(int status, String code) {
        String message =
                switch (code) {
                    case ErrorCode.LOGIN_FAILED -> "账号或密码错误";
                    case ErrorCode.UNAUTHENTICATED -> "会话已失效，请重新登录";
                    case ErrorCode.LOGIN_RATE_LIMITED -> "登录尝试过多，请5分钟后重试";
                    case ErrorCode.INVALID_ARGUMENT,
                            ErrorCode.INVALID_GUESTS,
                            ErrorCode.ROOM_COUNT_MISMATCH ->
                            "请检查输入信息和房间数量";
                    case ErrorCode.INVALID_IDEMPOTENCY_KEY -> "请求标识无效，请重新确认操作";
                    case ErrorCode.BOOKING_WINDOW_INVALID -> "日期超出未来7晚的预订范围";
                    case ErrorCode.CHECKIN_TIME_INVALID -> "未到计划入住日12点，或已超过计划离店时间";
                    case ErrorCode.INVENTORY_NOT_AVAILABLE -> "库存不足，请重新查询房型";
                    case ErrorCode.ROOM_NOT_AVAILABLE -> "所选房间已不可入住，请刷新候选房间";
                    case ErrorCode.PRICE_CHANGED -> "房价已变化，请刷新报价后重新确认";
                    case ErrorCode.ORDER_STATUS_CONFLICT, ErrorCode.ORDER_STATE_CONFLICT -> "订单状态已变化，请刷新订单";
                    case ErrorCode.IDEMPOTENCY_CONFLICT -> "请求标识已被使用，请先查询原操作结果";
                    case ErrorCode.INVENTORY_NOT_READY -> "库存待同步，请稍后重试";
                    case ErrorCode.INVENTORY_DATA_INCONSISTENT,
                            ErrorCode.INVENTORY_STATE_CONFLICT ->
                            "库存数据异常，请联系管理员核查";
                    case ErrorCode.ORDER_NOT_FOUND -> "订单不存在";
                    case ErrorCode.RETRY_SAME_REQUEST -> "并发操作冲突，请使用原请求重试";
                    default -> "结果待确认，请查询原订单或使用原请求重试";
                };
        return ResponseEntity.status(status)
                .header("Cache-Control", "no-store")
                .body(
                        new ApiResponse<>(
                                code, message, null, MDC.get("requestId"), MDC.get("traceId")));
    }
}
