package com.harbor.hotel.app.auth.transfer;

import com.harbor.hotel.app.auth.dto.EmployeeDTO;
import com.harbor.hotel.domain.auth.EmployeeSessionIdentity;

public final class EmployeeTransfer {
    private EmployeeTransfer() {}

    public static EmployeeDTO toDTO(EmployeeSessionIdentity identity) {
        return new EmployeeDTO(identity.employeeId(), identity.username(), identity.displayName());
    }
}
