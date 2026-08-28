package com.harbor.hotel.api.web.order.transfer;

import com.harbor.hotel.api.web.order.vo.*;
import com.harbor.hotel.api.web.order.vo.OrderSummaryVO;
import com.harbor.hotel.api.web.order.vo.RoomCandidateVO;
import com.harbor.hotel.app.order.dto.*;
import com.harbor.hotel.app.order.dto.RoomCandidateDTO;

public final class OrderTransfer {
    private OrderTransfer() {}

    public static OrderSummaryVO toVO(OrderSummaryDTO v) {
        return new OrderSummaryVO(
                v.id(),
                v.orderNo(),
                v.bookerName(),
                v.bookerPhone(),
                v.status(),
                v.roomCount(),
                v.roomTypeName(),
                v.plannedCheckinTime(),
                v.plannedCheckoutTime(),
                v.nights(),
                v.nightlyPrice(),
                v.totalAmount(),
                v.remark(),
                v.cancelTime(),
                v.cancelReason(),
                v.maxGuests());
    }

    public static RoomCandidateVO toVO(RoomCandidateDTO v) {
        return new RoomCandidateVO(v.roomId(), v.roomNo(), v.floorLabel());
    }

    public static OrderPageVO toVO(OrderPageDTO p) {
        return new OrderPageVO(
                p.items().stream().map(OrderTransfer::toVO).toList(),
                p.total(),
                p.pageNo(),
                p.pageSize());
    }

    public static OrderDetailVO toVO(OrderDetailDTO d) {
        return new OrderDetailVO(
                toVO(d.order()),
                d.rooms().stream()
                        .map(
                                r ->
                                        new RegisteredRoomVO(
                                                r.roomId(),
                                                r.roomNo(),
                                                r.checkinTime(),
                                                r.guests().stream()
                                                        .map(
                                                                g ->
                                                                        new RegisteredGuestVO(
                                                                                g.name(),
                                                                                g.phone()))
                                                        .toList()))
                        .toList());
    }
}
