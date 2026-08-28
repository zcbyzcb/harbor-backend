package com.harbor.hotel.api.web.auth.transfer;

import com.harbor.hotel.api.web.auth.EmployeeVO;
import com.harbor.hotel.app.auth.EmployeeDTO;
import com.harbor.hotel.domain.auth.EmployeeSessionIdentity;

public final class AuthTransfer {
    private AuthTransfer() {}

    public static EmployeeVO toVO(EmployeeDTO d) {
        return new EmployeeVO(d.employeeId().toString(), d.username(), d.displayName());
    }

    public static EmployeeSessionIdentity toIdentity(EmployeeDTO d) {
        return new EmployeeSessionIdentity(d.employeeId(), d.username(), d.displayName());
    }
}
