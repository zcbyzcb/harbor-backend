package com.harbor.hotel.app.booking.processor;

import com.harbor.hotel.app.booking.dto.CheckInCommandDTO;
import com.harbor.hotel.app.booking.transfer.BookingCommandTransfer;
import com.harbor.hotel.domain.booking.factory.BookingFactory;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CheckInOrderProcessor {
    @Resource private BookingFactory bookingFactory;

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public Long process(CheckInCommandDTO command) {
        return bookingFactory
                .order(command.orderId())
                .checkIn(
                        command.employeeId(),
                        command.requestId(),
                        BookingCommandTransfer.toDomain(command));
    }
}
