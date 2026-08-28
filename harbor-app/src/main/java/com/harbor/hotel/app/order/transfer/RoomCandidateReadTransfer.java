package com.harbor.hotel.app.order.transfer;

import com.harbor.hotel.app.order.dto.RoomCandidateDTO;
import com.harbor.hotel.infrastructure.persistence.po.RoomCandidatePO;

public final class RoomCandidateReadTransfer {
    private RoomCandidateReadTransfer() {}

    public static RoomCandidateDTO toDTO(RoomCandidatePO p) {
        return p == null ? null : new RoomCandidateDTO(p.roomId(), p.roomNo(), p.floorLabel());
    }
}
