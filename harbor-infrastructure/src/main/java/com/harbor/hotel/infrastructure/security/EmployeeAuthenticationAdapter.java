package com.harbor.hotel.infrastructure.security;

import com.harbor.hotel.domain.auth.AuthenticationPort;
import com.harbor.hotel.domain.auth.EmployeeActivityPort;
import com.harbor.hotel.domain.auth.EmployeeSessionIdentity;
import com.harbor.hotel.domain.shared.DomainException;
import com.harbor.hotel.domain.shared.ErrorCode;
import com.harbor.hotel.infrastructure.persistence.mapper.EmployeeAuthMapper;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EmployeeAuthenticationAdapter implements AuthenticationPort, EmployeeActivityPort {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(EmployeeAuthenticationAdapter.class);
    @Resource
    private EmployeeAuthMapper employeeAuthMapper;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private LoginAttemptLimiter loginAttemptLimiter;

    @Override
    public EmployeeSessionIdentity authenticate(String username, String password, String source) {
        loginAttemptLimiter.requireAllowed(username, source);
        EmployeeAuthMapper.EmployeeAuthPO employee =
                employeeAuthMapper.findActiveByUsername(username.trim());
        if (employee == null || !passwordEncoder.matches(password, employee.passwordHash())) {
            loginAttemptLimiter.recordFailure(username, source);
            LOGGER.warn("operation=LOGIN result=FAILED username={}", maskUsername(username));
            throw new DomainException(ErrorCode.LOGIN_FAILED);
        }
        loginAttemptLimiter.recordSuccess(username);
        EmployeeSessionIdentity identity =
                new EmployeeSessionIdentity(
                        employee.id(), employee.username(), employee.displayName());
        return identity;
    }

    @Override
    public boolean isActive(Long employeeId) {
        return employeeId != null && employeeAuthMapper.findActiveById(employeeId) != null;
    }

    private String maskUsername(String username) {
        if (username == null || username.length() < 3) return "***";
        return username.substring(0, 1) + "***" + username.substring(username.length() - 1);
    }
}
