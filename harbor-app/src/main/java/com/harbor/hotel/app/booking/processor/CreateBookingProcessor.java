package com.harbor.hotel.app.booking.processor;

import com.harbor.hotel.app.booking.dto.BookingCommandDTO;
import com.harbor.hotel.app.booking.transfer.BookingCommandTransfer;
import com.harbor.hotel.domain.booking.model.BookingFactory;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateBookingProcessor {
    @Resource private BookingFactory bookingFactory;

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public Long process(BookingCommandDTO command) {
        return bookingFactory
                .createReservation()
                .book(
                        BookingCommandTransfer.toDomain(command),
                        command.employeeId(),
                        command.requestId());
    }
}
