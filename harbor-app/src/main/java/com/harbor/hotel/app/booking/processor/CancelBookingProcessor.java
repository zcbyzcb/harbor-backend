package com.harbor.hotel.app.booking.processor;

import com.harbor.hotel.app.booking.dto.CancelCommandDTO;
import com.harbor.hotel.app.booking.transfer.BookingCommandTransfer;
import com.harbor.hotel.domain.booking.factory.BookingFactory;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CancelBookingProcessor {
    @Resource
    private BookingFactory bookingFactory;

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public Long process(CancelCommandDTO command) {
        return bookingFactory
                .order(command.orderId())
                .cancel(
                        command.employeeId(),
                        command.requestId(),
                        BookingCommandTransfer.clean(command.reason()));
    }
}
