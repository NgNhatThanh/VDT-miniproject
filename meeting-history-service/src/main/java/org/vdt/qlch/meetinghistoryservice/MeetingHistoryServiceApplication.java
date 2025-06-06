package org.vdt.qlch.meetinghistoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(
        scanBasePackages = {"org.vdt.qlch.meetinghistoryservice", "org.vdt.commonlib"}
)
@EnableKafka
public class MeetingHistoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetingHistoryServiceApplication.class, args);
    }

}
