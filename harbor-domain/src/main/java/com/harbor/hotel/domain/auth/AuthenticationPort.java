package com.harbor.hotel.domain.auth;

public interface AuthenticationPort {
    EmployeeSessionIdentity authenticate(String username, String password, String source);
}
