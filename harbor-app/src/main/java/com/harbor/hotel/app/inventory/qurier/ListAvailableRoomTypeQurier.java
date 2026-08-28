package com.harbor.hotel.app.inventory.qurier;

import com.harbor.hotel.app.inventory.dto.AvailableRoomTypeDTO;
import com.harbor.hotel.app.inventory.query.AvailabilityQueryDTO;
import com.harbor.hotel.app.inventory.transfer.AvailableRoomTypeReadTransfer;
import com.harbor.hotel.domain.shared.DomainException;
import com.harbor.hotel.domain.shared.ErrorCode;
import com.harbor.hotel.infrastructure.persistence.mapper.AvailabilityReadMapper;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class ListAvailableRoomTypeQurier {
    @Resource
    private AvailabilityReadMapper availabilityReadMapper;
    @Resource
    private Clock clock;

    @Value("${hotel.inventory-window-days}")
    private int windowDays;

    public List<AvailableRoomTypeDTO> query(AvailabilityQueryDTO query) {
        if (query == null
                || query.roomCount() <= 0
                || query.checkinDate() == null
                || query.checkoutDate() == null
                || !query.checkoutDate().isAfter(query.checkinDate()))
            throw new DomainException(ErrorCode.INVALID_ARGUMENT);
        LocalDate today = LocalDate.now(clock);
        if (query.checkinDate().isBefore(today)
                || query.checkoutDate().isAfter(today.plusDays(windowDays))) {
            throw new DomainException(ErrorCode.BOOKING_WINDOW_INVALID);
        }
        int nights =
                Math.toIntExact(
                        query.checkoutDate().toEpochDay() - query.checkinDate().toEpochDay());
        List<LocalDate> stayDates =
                IntStream.range(0, nights).mapToObj(query.checkinDate()::plusDays).toList();
        return availabilityReadMapper.list(stayDates, nights).stream()
                .map(p -> AvailableRoomTypeReadTransfer.toDTO(p, nights, query.roomCount()))
                .toList();
    }
}
