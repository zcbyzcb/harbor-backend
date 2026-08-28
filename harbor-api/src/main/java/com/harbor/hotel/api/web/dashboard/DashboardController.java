package com.harbor.hotel.api.web.dashboard;

import com.harbor.hotel.api.web.ApiResponse;
import com.harbor.hotel.api.web.dashboard.transfer.DashboardTransfer;
import com.harbor.hotel.api.web.dashboard.vo.DashboardVO;
import com.harbor.hotel.app.dashboard.qurier.GetDashboardQurier;

import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    @Resource private GetDashboardQurier getDashboardQurier;

    @GetMapping
    public ApiResponse<DashboardVO> get() {
        return ApiResponse.success(DashboardTransfer.toVO(getDashboardQurier.query()));
    }
}
