package org.vdt.qlch.meetingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.vdt.qlch.meetingservice.config.ServiceUrlConfig;

@SpringBootApplication(
        scanBasePackages = {"org.vdt.qlch.meetingservice", "org.vdt.commonlib"}
)
@EnableConfigurationProperties({ServiceUrlConfig.class})
@EnableCaching
public class MeetingServiceApplication{

    public static void main(String[] args) {
        SpringApplication.run(MeetingServiceApplication.class, args);
    }

}
