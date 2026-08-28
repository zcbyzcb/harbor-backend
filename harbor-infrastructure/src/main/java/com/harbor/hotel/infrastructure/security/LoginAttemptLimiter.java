package com.harbor.hotel.infrastructure.security;

import com.harbor.hotel.domain.shared.DomainException;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory limits suit the documented single-instance MVP. Trust only the direct peer address. */
@Component
public class LoginAttemptLimiter {
    private static final int MAX_KEYS = 10000;
    private final ConcurrentHashMap<String, Attempt> attempts = new ConcurrentHashMap<>();
    @Resource
    private Clock clock;

    @Value("${hotel.auth.max-failures:5}")
    private int userLimit;

    @Value("${hotel.auth.source-max-failures:50}")
    private int sourceLimit;

    @Value("${hotel.auth.lock-minutes:5}")
    private int lockMinutes;

    public void requireAllowed(String username, String source) {
        requireKey("user:" + normalize(username));
        requireKey("source:" + source);
    }

    private void requireKey(String key) {
        Instant now = clock.instant();
        Attempt value = attempts.get(key);
        if (value != null && value.lockedUntil().isAfter(now))
            throw new DomainException("LOGIN_RATE_LIMITED");
        if (attempts.size() >= MAX_KEYS) {
            attempts.entrySet().removeIf(entry -> !entry.getValue().expires().isAfter(now));
            if (attempts.size() >= MAX_KEYS && !attempts.containsKey(key))
                throw new DomainException("LOGIN_RATE_LIMITED");
        }
    }

    public void recordFailure(String username, String source) {
        fail("user:" + normalize(username), userLimit);
        fail("source:" + source, sourceLimit);
    }

    private void fail(String key, int limit) {
        Instant now = clock.instant();
        Duration duration = Duration.ofMinutes(lockMinutes);
        attempts.compute(
                key,
                (ignored, old) -> {
                    boolean expired = old == null || !old.expires().isAfter(now);
                    int count = expired ? 1 : old.failures() + 1;
                    Instant lockedUntil = count >= limit ? now.plus(duration) : Instant.EPOCH;
                    Instant expires =
                            count >= limit || expired ? now.plus(duration) : old.expires();
                    return new Attempt(count, lockedUntil, expires);
                });
    }

    public void recordSuccess(String username) {
        attempts.remove("user:" + normalize(username));
    }

    private String normalize(String username) {
        return username == null ? "" : username.trim().toLowerCase(java.util.Locale.ROOT);
    }

    private record Attempt(int failures, Instant lockedUntil, Instant expires) {}
}
