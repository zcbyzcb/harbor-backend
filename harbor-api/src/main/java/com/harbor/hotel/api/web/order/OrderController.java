package com.harbor.hotel.api.web.order;

import com.harbor.hotel.api.web.ApiResponse;
import com.harbor.hotel.api.web.order.transfer.OrderTransfer;
import com.harbor.hotel.api.web.order.vo.*;
import com.harbor.hotel.app.order.dto.OrderSearchDTO;
import com.harbor.hotel.app.order.qurier.*;
import com.harbor.hotel.domain.auth.EmployeeSessionIdentity;

import jakarta.annotation.Resource;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Resource private PageOrderQurier page;
    @Resource private GetOrderDetailQurier detail;
    @Resource private ListAvailableRoomQurier candidates;

    @GetMapping
    public ApiResponse<OrderPageVO> page(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate arrivalFrom,
            @RequestParam(required = false) LocalDate arrivalTo,
            @RequestParam(required = false) String requestId,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize,
            @AuthenticationPrincipal EmployeeSessionIdentity employee) {
        return ApiResponse.success(
                OrderTransfer.toVO(
                        page.query(
                                new OrderSearchDTO(
                                        orderNo,
                                        phone,
                                        name,
                                        status,
                                        arrivalFrom,
                                        arrivalTo,
                                        requestId,
                                        employee.employeeId(),
                                        pageNo,
                                        pageSize))));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDetailVO> detail(@PathVariable Long id) {
        return ApiResponse.success(OrderTransfer.toVO(detail.query(id)));
    }

    @GetMapping("/{id}/available-rooms")
    public ApiResponse<List<RoomCandidateVO>> rooms(@PathVariable Long id) {
        return ApiResponse.success(candidates.query(id).stream().map(OrderTransfer::toVO).toList());
    }
}
