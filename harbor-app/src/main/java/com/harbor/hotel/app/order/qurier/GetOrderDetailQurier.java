package com.harbor.hotel.app.order.qurier;

import com.harbor.hotel.app.order.dto.OrderDetailDTO;
import com.harbor.hotel.app.order.transfer.OrderDetailReadTransfer;
import com.harbor.hotel.domain.shared.DomainException;
import com.harbor.hotel.domain.shared.ErrorCode;
import com.harbor.hotel.infrastructure.persistence.mapper.OrderReadMapper;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.*;

@Component
public class GetOrderDetailQurier {
    @Resource
    private OrderReadMapper mapper;

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public OrderDetailDTO query(Long id) {
        com.harbor.hotel.infrastructure.persistence.po.OrderSummaryPO order = mapper.detail(id);
        if (order == null) throw new DomainException(ErrorCode.ORDER_NOT_FOUND);
        return OrderDetailReadTransfer.toDTO(order, mapper.registeredGuests(id));
    }
}
