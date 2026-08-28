package com.harbor.hotel.domain.shared;

public final class DomainException extends RuntimeException {
    private final String code;

    public DomainException(String code) {
        super(code);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
