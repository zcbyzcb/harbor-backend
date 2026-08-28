package com.harbor.hotel.app.booking.generator;

import com.harbor.hotel.domain.shared.DomainException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/** Generates UO + epoch-millisecond timestamp + three-digit worker id + random two-digit suffix. */
@Component
public final class OrderNoGenerator {
    private final Clock clock;
    private final int machineId;
    private final boolean[] usedSuffixes = new boolean[100];
    private long currentMillis = Long.MIN_VALUE;

    public OrderNoGenerator(Clock clock, @Value("${hotel.order-no.machine-id:1}") int machineId) {
        if (machineId < 0 || machineId > 999)
            throw new IllegalArgumentException("hotel.order-no.machine-id must be between 0 and 999");
        this.clock = clock;
        this.machineId = machineId;
    }

    public synchronized String next() {
        long timestamp = clock.millis();
        if (timestamp != currentMillis) {
            currentMillis = timestamp;
            Arrays.fill(usedSuffixes, false);
        }
        int start = ThreadLocalRandom.current().nextInt(usedSuffixes.length);
        for (int offset = 0; offset < usedSuffixes.length; offset++) {
            int suffix = (start + offset) % usedSuffixes.length;
            if (!usedSuffixes[suffix]) {
                usedSuffixes[suffix] = true;
                return String.format(Locale.ROOT, "UO%d%03d%02d", timestamp, machineId, suffix);
            }
        }
        throw new DomainException("ORDER_NO_GENERATION_EXHAUSTED");
    }
}
