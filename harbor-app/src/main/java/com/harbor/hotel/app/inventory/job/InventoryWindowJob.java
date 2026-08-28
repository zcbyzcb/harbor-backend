package com.harbor.hotel.app.inventory.job;

import com.harbor.hotel.app.inventory.dto.InitializeDailyInventoryDTO;
import com.harbor.hotel.app.inventory.processor.InitializeDailyInventoryProcessor;
import com.harbor.hotel.app.inventory.qurier.ListRoomTypeIdsQurier;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class InventoryWindowJob {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryWindowJob.class);
    @Resource
    private InitializeDailyInventoryProcessor processor;
    @Resource
    private ListRoomTypeIdsQurier types;
    @Resource
    private Clock clock;

    @Value("${hotel.inventory-window-days:7}")
    private int days;

    @Value("${hotel.inventory.initialize-on-start:true}")
    private boolean initializeOnStart;

    @Value("${hotel.xxl-job.enabled:false}")
    private boolean xxlEnabled;

    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
        if (initializeOnStart) runSafely();
    }

    @Scheduled(
            cron = "${hotel.inventory.cron:0 5 0 * * *}",
            zone = "${hotel.zone-id:Asia/Shanghai}")
    public void localSchedule() {
        if (!xxlEnabled) runSafely();
    }

    @XxlJob("prepareInventoryWindow")
    public void execute() {
        List<String> failures = runWindow();
        if (!failures.isEmpty())
            XxlJobHelper.handleFail("Failed inventory units: " + String.join(",", failures));
        else XxlJobHelper.handleSuccess("Inventory window ready");
    }

    private void runSafely() {
        try {
            runWindow();
        } catch (RuntimeException ex) {
            LOG.error(
                    "operation=INVENTORY_SYNC result=FAILED type={}",
                    ex.getClass().getSimpleName());
        }
    }

    public List<String> runWindow() {
        LocalDate today = LocalDate.now(clock);
        List<String> failures = new ArrayList<>();
        int created = 0, skipped = 0;
        var ids = types.query();
        for (Long id : ids)
            for (int offset = 0; offset < days; offset++) {
                var command = new InitializeDailyInventoryDTO(id, today.plusDays(offset));
                try {
                    if (initializeWithRetry(command) == 1) created++;
                    else skipped++;
                } catch (RuntimeException ex) {
                    failures.add(id + "/" + command.stayDate());
                    LOG.error(
                            "operation=INVENTORY_SYNC roomTypeId={} stayDate={} result=FAILED"
                                    + " type={}",
                            id,
                            command.stayDate(),
                            ex.getClass().getSimpleName());
                }
            }
        LOG.info(
                "operation=INVENTORY_SYNC start={} created={} skipped={} failed={}",
                today,
                created,
                skipped,
                failures.size());
        return failures;
    }

    private int initializeWithRetry(InitializeDailyInventoryDTO command) {
        long[] delays = {1000, 3000, 10000};
        for (int attempt = 0; ; attempt++) {
            try {
                return processor.process(command);
            } catch (CannotAcquireLockException ex) {
                if (attempt == delays.length) throw ex;
                try {
                    Thread.sleep(delays[attempt]);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Inventory retry interrupted");
                }
            }
        }
    }
}
