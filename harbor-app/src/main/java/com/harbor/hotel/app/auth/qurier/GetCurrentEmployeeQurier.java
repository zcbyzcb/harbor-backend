package com.harbor.hotel.app.auth.qurier;

import com.harbor.hotel.app.auth.dto.EmployeeDTO;
import com.harbor.hotel.app.auth.transfer.EmployeeTransfer;
import com.harbor.hotel.domain.auth.EmployeeSessionIdentity;
import com.harbor.hotel.domain.shared.DomainException;
import com.harbor.hotel.domain.shared.ErrorCode;

import org.springframework.stereotype.Component;

@Component
public class GetCurrentEmployeeQurier {
    public EmployeeDTO query(EmployeeSessionIdentity identity) {
        if (identity == null) throw new DomainException(ErrorCode.UNAUTHENTICATED);
        return EmployeeTransfer.toDTO(identity);
    }
}
