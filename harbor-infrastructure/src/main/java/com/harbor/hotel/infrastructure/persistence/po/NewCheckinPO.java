package com.harbor.hotel.infrastructure.persistence.po;

import java.time.LocalDateTime;

public final class NewCheckinPO {
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private final Long orderId;

    public Long getOrderId() {
        return orderId;
    }

    private final Long roomTypeId;

    public Long getRoomTypeId() {
        return roomTypeId;
    }

    private final Long roomId;

    public Long getRoomId() {
        return roomId;
    }

    private final String roomNo;

    public String getRoomNo() {
        return roomNo;
    }

    private final LocalDateTime now;

    public LocalDateTime getNow() {
        return now;
    }

    private final Long employeeId;

    public Long getEmployeeId() {
        return employeeId;
    }

    private final String requestId;

    public String getRequestId() {
        return requestId;
    }

    private final byte[] requestHash;

    public byte[] getRequestHash() {
        return requestHash;
    }

    public NewCheckinPO(
            Long orderId,
            Long roomTypeId,
            Long roomId,
            String roomNo,
            LocalDateTime now,
            Long employeeId,
            String requestId,
            byte[] requestHash) {
        this.orderId = orderId;
        this.roomTypeId = roomTypeId;
        this.roomId = roomId;
        this.roomNo = roomNo;
        this.now = now;
        this.employeeId = employeeId;
        this.requestId = requestId;
        this.requestHash = requestHash;
    }
}
