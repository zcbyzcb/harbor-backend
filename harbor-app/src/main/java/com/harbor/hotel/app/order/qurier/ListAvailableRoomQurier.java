package com.harbor.hotel.app.order.qurier;

import com.harbor.hotel.app.order.dto.RoomCandidateDTO;
import com.harbor.hotel.app.order.transfer.RoomCandidateReadTransfer;
import com.harbor.hotel.domain.shared.DomainException;
import com.harbor.hotel.infrastructure.persistence.mapper.OrderReadMapper;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListAvailableRoomQurier {
    @Resource
    private OrderReadMapper orderReadMapper;

    public List<RoomCandidateDTO> query(Long id) {
        if (orderReadMapper.detail(id) == null) {
            throw new DomainException("ORDER_NOT_FOUND");
        }
        if (!"PENDING".equals(orderReadMapper.detail(id).status()))
            throw new DomainException("ORDER_STATUS_CONFLICT");
        if (orderReadMapper.missingInventory(id) > 0)
            throw new DomainException("INVENTORY_NOT_READY");
        return orderReadMapper.availableRooms(id).stream()
                .map(RoomCandidateReadTransfer::toDTO)
                .toList();
    }
}
