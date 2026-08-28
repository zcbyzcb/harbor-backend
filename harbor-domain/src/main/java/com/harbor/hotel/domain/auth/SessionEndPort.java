package com.harbor.hotel.domain.auth;

/** Session invalidation is implemented at the HTTP security boundary. */
public interface SessionEndPort {
    void endCurrentSession();
}
