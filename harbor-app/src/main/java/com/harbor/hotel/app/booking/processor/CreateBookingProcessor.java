package com.harbor.hotel.app.booking.processor;

import com.harbor.hotel.app.booking.generator.OrderNoGenerator;
import com.harbor.hotel.app.booking.dto.BookingCommandDTO;
import com.harbor.hotel.app.booking.transfer.BookingCommandTransfer;
import com.harbor.hotel.domain.booking.factory.BookingFactory;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class CreateBookingProcessor {
    @Resource
    private BookingFactory bookingFactory;
    @Resource
    private OrderNoGenerator orderNoGenerator;
    @Resource
    private TransactionTemplate transactionTemplate;

    public Long process(BookingCommandDTO command) {
        try {
            return execute(command);
        } catch (DuplicateKeyException ignored) {
            return execute(command);
        }
    }

    private Long execute(BookingCommandDTO command) {
        return transactionTemplate.execute(
                status ->
                        bookingFactory
                                .createReservation()
                                .book(
                                        BookingCommandTransfer.toDomain(command),
                                        command.employeeId(),
                                        command.requestId(),
                                        orderNoGenerator.next()));
    }
}
