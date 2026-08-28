package com.harbor.hotel.api.web.booking.transfer;

import com.harbor.hotel.api.web.booking.request.*;
import com.harbor.hotel.api.web.booking.vo.MutationVO;
import com.harbor.hotel.app.booking.dto.*;

public final class BookingTransfer {
    private BookingTransfer() {}

    public static BookingCommandDTO toDTO(BookingRequest v, Long employeeId, String key) {
        return new BookingCommandDTO(
                v.roomTypeId(),
                v.checkinDate(),
                v.checkoutDate(),
                v.roomCount(),
                v.bookerName(),
                v.bookerPhone(),
                v.confirmedPrice(),
                v.remark(),
                employeeId,
                key);
    }

    public static CheckInCommandDTO toDTO(
            CheckInRequest v, Long orderId, Long employeeId, String key) {
        return new CheckInCommandDTO(
                orderId,
                employeeId,
                key,
                v.rooms().stream()
                        .map(
                                r ->
                                        new CheckInCommandDTO.AllocationDTO(
                                                r.roomId(),
                                                r.guests().stream()
                                                        .map(
                                                                g ->
                                                                        new CheckInCommandDTO
                                                                                .GuestDTO(
                                                                                g.name(),
                                                                                g.phone()))
                                                        .toList()))
                        .toList());
    }

    public static CancelCommandDTO toDTO(
            CancelRequest v, Long orderId, Long employeeId, String key) {
        return new CancelCommandDTO(orderId, employeeId, key, v.reason());
    }

    public static MutationVO toVO(Long id) {
        return new MutationVO(id.toString());
    }
}
