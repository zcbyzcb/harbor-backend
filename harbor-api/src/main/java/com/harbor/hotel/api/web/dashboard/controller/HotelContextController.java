package com.harbor.hotel.api.web.dashboard.controller;

import com.harbor.hotel.api.web.ApiResponse;
import com.harbor.hotel.api.web.dashboard.vo.HotelContextVO;
import com.harbor.hotel.app.dashboard.dto.HotelContextDTO;
import com.harbor.hotel.app.dashboard.qurier.GetHotelContextQurier;

import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.*;

@RestController
public class HotelContextController {
    @Resource
    private GetHotelContextQurier qurier;

    @GetMapping("/api/hotel-context")
    public ApiResponse<HotelContextVO> context() {
        HotelContextDTO d = qurier.query();
        return ApiResponse.success(
                new HotelContextVO(
                        d.hotelDate(), d.serverTime(), d.lastCheckoutDate(), d.windowDays()));
    }

}
