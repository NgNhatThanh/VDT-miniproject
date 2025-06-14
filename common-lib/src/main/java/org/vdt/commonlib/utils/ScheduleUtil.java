package org.vdt.commonlib.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class ScheduleUtil {

    private final TaskScheduler taskScheduler;

    public void scheduleTask(LocalDateTime time, Runnable task) {
        Instant instant = time.atZone(ZoneId.systemDefault()).toInstant();
        taskScheduler.schedule(task, instant);
    }

}
