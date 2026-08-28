package com.harbor.hotel.app.order.transfer;

import com.harbor.hotel.app.order.dto.*;
import com.harbor.hotel.infrastructure.persistence.po.*;

import java.util.*;
import java.util.stream.Collectors;

public final class OrderDetailReadTransfer {
    private OrderDetailReadTransfer() {}

    public static OrderDetailDTO toDTO(OrderSummaryPO order, List<RegisteredGuestPO> rows) {
        Map<String, List<RegisteredGuestPO>> grouped =
                rows.stream()
                        .collect(
                                Collectors.groupingBy(
                                        RegisteredGuestPO::roomId,
                                        LinkedHashMap::new,
                                        Collectors.toList()));
        List<RegisteredRoomDTO> rooms =
                grouped.values().stream()
                        .map(
                                list ->
                                        new RegisteredRoomDTO(
                                                list.getFirst().roomId(),
                                                list.getFirst().roomNo(),
                                                list.getFirst().checkinTime(),
                                                list.stream()
                                                        .map(
                                                                g ->
                                                                        new RegisteredGuestDTO(
                                                                                g.name(),
                                                                                g.phone()))
                                                        .toList()))
                        .toList();
        return new OrderDetailDTO(OrderSummaryReadTransfer.toDTO(order), rooms);
    }
}
