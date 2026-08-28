package com.harbor.hotel.app.auth.processor;

import com.harbor.hotel.app.auth.dto.EmployeeDTO;
import com.harbor.hotel.app.auth.dto.LoginDTO;
import com.harbor.hotel.app.auth.transfer.EmployeeTransfer;
import com.harbor.hotel.domain.auth.*;
import com.harbor.hotel.domain.shared.DomainException;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;

@Component
public class LoginProcessor {
    @Resource
    private AuthenticationPort authenticationPort;

    public EmployeeDTO process(LoginDTO command) {
        if (command.username() == null
                || command.username().isBlank()
                || command.password() == null
                || command.password().isBlank()
                || command.password().getBytes(java.nio.charset.StandardCharsets.UTF_8).length
                        > 72) {
            throw new DomainException("LOGIN_FAILED");
        }
        return EmployeeTransfer.toDTO(
                authenticationPort.authenticate(
                        command.username(), command.password(), command.source()));
    }
}
