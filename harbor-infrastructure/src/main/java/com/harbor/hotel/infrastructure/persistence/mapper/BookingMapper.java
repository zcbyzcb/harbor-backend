package com.harbor.hotel.infrastructure.persistence.mapper;

import com.harbor.hotel.infrastructure.persistence.po.*;

import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingMapper {
    RoomTypePO lockType(@Param("id") Long id);

    OrderPO findRequest(@Param("employeeId") Long employeeId, @Param("key") String key);

    Long orderType(@Param("id") Long id);

    OrderPO lockOrder(@Param("id") Long id);

    int insertOrder(NewOrderPO row);

    List<LockPO> lockReservations(@Param("orderId") Long id);

    int insertReservation(
            @Param("orderId") Long orderId,
            @Param("inventoryId") Long inventoryId,
            @Param("count") int count);

    int updateInventory(
            @Param("id") Long id,
            @Param("booked") int booked,
            @Param("checkedIn") int checkedIn,
            @Param("available") int available);

    int transitionReservation(@Param("id") Long id, @Param("status") String status);

    List<RoomPO> lockRooms(@Param("ids") List<Long> ids);

    List<DetailPO> lockDetails(
            @Param("inventories") List<Long> inventories, @Param("rooms") List<Long> rooms);

    List<CheckinPO> checkins(@Param("orderId") Long orderId);

    int insertCheckin(NewCheckinPO row);

    int insertGuest(
            @Param("checkinId") Long checkinId,
            @Param("seq") int seq,
            @Param("name") String name,
            @Param("phone") String phone);

    int occupyDetail(@Param("id") Long id, @Param("checkinId") Long checkinId);

    int occupyRoom(@Param("id") Long id);

    int markCheckedIn(@Param("id") Long id);

    int markCancelled(
            @Param("id") Long id,
            @Param("employeeId") Long employeeId,
            @Param("now") LocalDateTime now,
            @Param("reason") String reason);

    int audit(
            @Param("id") Long orderId,
            @Param("operation") String operation,
            @Param("fromStatus") String fromStatus,
            @Param("toStatus") String toStatus,
            @Param("employeeId") Long employeeId,
            @Param("key") String requestId);

    int auditCount(@Param("id") Long orderId, @Param("operation") String operation);
}
