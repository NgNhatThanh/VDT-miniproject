package org.vdt.qlch.meetinghistoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.annotation.EnableKafka;
import org.vdt.qlch.meetinghistoryservice.config.ServiceUrlConfig;

@SpringBootApplication(
        scanBasePackages = {"org.vdt.qlch.meetinghistoryservice", "org.vdt.commonlib"}
)
@EnableKafka
@EnableConfigurationProperties({ServiceUrlConfig.class})
public class MeetingHistoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetingHistoryServiceApplication.class, args);
    }

}
