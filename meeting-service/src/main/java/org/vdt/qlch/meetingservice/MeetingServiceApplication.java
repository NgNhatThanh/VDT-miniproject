package org.vdt.qlch.meetingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.vdt.commonlib.config.CorsConfig;
import org.vdt.qlch.meetingservice.config.ServiceUrlConfig;

@SpringBootApplication(
        scanBasePackages = {"org.vdt.qlch.meetingservice", "org.vdt.commonlib"}
)
@EnableConfigurationProperties({ServiceUrlConfig.class})
public class MeetingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetingServiceApplication.class, args);
    }

}
