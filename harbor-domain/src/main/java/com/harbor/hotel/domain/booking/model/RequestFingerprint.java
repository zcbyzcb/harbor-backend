package com.harbor.hotel.domain.booking.model;

import com.harbor.hotel.domain.shared.DomainException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/** Length-prefixed fields prevent ambiguous concatenation; no personal data is logged. */
final class RequestFingerprint {
    private RequestFingerprint() {}

    static String key(String key) {
        try {
            if (key == null
                    || key.length() != 36
                    || !UUID.fromString(key).toString().equalsIgnoreCase(key))
                throw new IllegalArgumentException();
            return key.toLowerCase(java.util.Locale.ROOT);
        } catch (IllegalArgumentException ex) {
            throw new DomainException("INVALID_IDEMPOTENCY_KEY");
        }
    }

    static byte[] hash(Object... values) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            for (Object value : values) {
                byte[] data = String.valueOf(value).getBytes(StandardCharsets.UTF_8);
                digest.update(java.nio.ByteBuffer.allocate(4).putInt(data.length).array());
                digest.update(data);
            }
            return digest.digest();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

    static void same(byte[] expected, byte[] actual) {
        if (!MessageDigest.isEqual(expected, actual))
            throw new DomainException("IDEMPOTENCY_CONFLICT");
    }
}
