package com.harbor.hotel.api.web.booking;

import com.harbor.hotel.api.web.ApiResponse;
import com.harbor.hotel.api.web.booking.transfer.BookingTransfer;
import com.harbor.hotel.app.booking.processor.*;
import com.harbor.hotel.domain.auth.EmployeeSessionIdentity;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/booking_orders")
public class BookingController {
    private static final Logger LOG = LoggerFactory.getLogger(BookingController.class);
    @Resource private CreateBookingProcessor create;
    @Resource private CheckInOrderProcessor checkIn;
    @Resource private CancelBookingProcessor cancel;

    @PostMapping
    public ApiResponse<MutationVO> create(
            @Valid @RequestBody BookingRequest body,
            @RequestHeader("Idempotency-Key") String key,
            @AuthenticationPrincipal EmployeeSessionIdentity employee) {
        Long id = create.process(BookingTransfer.toDTO(body, employee.employeeId(), key));
        LOG.info(
                "operation=CREATE_BOOKING orderId={} operatorId={} result=COMMITTED",
                id,
                employee.employeeId());
        return ApiResponse.success(BookingTransfer.toVO(id));
    }

    @PostMapping("/{id}/check-in")
    public ApiResponse<MutationVO> checkIn(
            @PathVariable Long id,
            @Valid @RequestBody CheckInRequest body,
            @RequestHeader("Idempotency-Key") String key,
            @AuthenticationPrincipal EmployeeSessionIdentity employee) {
        Long result = checkIn.process(BookingTransfer.toDTO(body, id, employee.employeeId(), key));
        LOG.info(
                "operation=CHECK_IN orderId={} operatorId={} result=COMMITTED",
                id,
                employee.employeeId());
        return ApiResponse.success(BookingTransfer.toVO(result));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<MutationVO> cancel(
            @PathVariable Long id,
            @Valid @RequestBody CancelRequest body,
            @RequestHeader("Idempotency-Key") String key,
            @AuthenticationPrincipal EmployeeSessionIdentity employee) {
        Long result = cancel.process(BookingTransfer.toDTO(body, id, employee.employeeId(), key));
        LOG.info(
                "operation=CANCEL orderId={} operatorId={} result=COMMITTED",
                id,
                employee.employeeId());
        return ApiResponse.success(BookingTransfer.toVO(result));
    }
}
