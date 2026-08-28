package com.harbor.hotel.app.order.qurier;

import com.harbor.hotel.app.order.dto.*;
import com.harbor.hotel.app.order.transfer.*;
import com.harbor.hotel.domain.shared.DomainException;
import com.harbor.hotel.infrastructure.persistence.mapper.OrderReadMapper;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.*;

@Component
public class PageOrderQurier {
    @Resource
    private OrderReadMapper mapper;

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public OrderPageDTO query(OrderSearchDTO q) {
        if (q.pageNo() < 1
                || q.pageSize() < 1
                || q.pageSize() > 100
                || (q.arrivalFrom() != null
                        && q.arrivalTo() != null
                        && q.arrivalTo().isBefore(q.arrivalFrom()))
                || (q.status() != null
                        && !q.status().isEmpty()
                        && !java.util.Set.of("PENDING", "CHECKED_IN", "CANCELLED")
                                .contains(q.status())))
            throw new DomainException("INVALID_ARGUMENT");
        com.harbor.hotel.infrastructure.persistence.po.OrderSearchPO po =
                OrderSearchTransfer.toPO(q);
        return new OrderPageDTO(
                mapper.page(po).stream().map(OrderSummaryReadTransfer::toDTO).toList(),
                mapper.count(po),
                q.pageNo(),
                q.pageSize());
    }
}
