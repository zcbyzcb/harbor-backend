package com.harbor.hotel.domain.auth;

public record EmployeeSessionIdentity(Long employeeId, String username, String displayName) {}
