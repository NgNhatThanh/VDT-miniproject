package org.vdt.commonlib.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class TaskScheduleConfig {

    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}

