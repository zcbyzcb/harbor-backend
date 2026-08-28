package com.harbor.hotel.domain.booking.repository;

import com.harbor.hotel.domain.booking.model.*;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository {
    RoomType lockType(Long id);

    Order findRequest(Long employeeId, String key);

    Long orderType(Long orderId);

    Order lockOrder(Long id);

    Long insertOrder(NewOrder order);

    List<Lock> lockReservations(Long orderId);

    void insertReservation(Long orderId, Long inventoryId, int count);

    void reserveInventory(Long inventoryId, int roomCount);

    void cancelReservation(Long inventoryId, int roomCount);

    void convertReservationToCheckin(Long inventoryId, int roomCount);

    void transitionReservation(Long id, String status);

    List<Room> lockRooms(List<Long> ids);

    List<Detail> lockDetails(List<Long> inventories, List<Long> rooms);

    List<Checkin> checkins(Long orderId);

    Long insertCheckin(NewCheckin checkin);

    void insertGuest(Long checkinId, int seq, Guest guest);

    void occupyDetail(Long id, Long checkinId);

    void occupyRoom(Long id);

    void markCheckedIn(Long orderId);

    void markCancelled(Long orderId, Long employeeId, LocalDateTime now, String reason);

    void audit(
            Long orderId,
            String operation,
            String fromStatus,
            String toStatus,
            Long employeeId,
            String requestId);

    int auditCount(Long orderId, String operation);

}
