package com.harbor.hotel.api.web.inventory;

import com.harbor.hotel.api.web.ApiResponse;
import com.harbor.hotel.api.web.inventory.transfer.AvailabilityTransfer;
import com.harbor.hotel.api.web.inventory.vo.AvailableRoomTypeVO;
import com.harbor.hotel.app.inventory.query.AvailabilityQueryDTO;
import com.harbor.hotel.app.inventory.qurier.ListAvailableRoomTypeQurier;

import jakarta.annotation.Resource;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/room-types")
public class AvailabilityController {
    @Resource private ListAvailableRoomTypeQurier listAvailableRoomTypeQurier;

    @GetMapping("/availability")
    public ApiResponse<List<AvailableRoomTypeVO>> list(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkinDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkoutDate,
            @RequestParam(defaultValue = "1") int roomCount) {
        List<AvailableRoomTypeVO> result =
                listAvailableRoomTypeQurier
                        .query(new AvailabilityQueryDTO(checkinDate, checkoutDate, roomCount))
                        .stream()
                        .map(AvailabilityTransfer::toVO)
                        .toList();
        return ApiResponse.success(result);
    }
}
