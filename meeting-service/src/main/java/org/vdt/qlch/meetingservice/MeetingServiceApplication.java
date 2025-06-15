package org.vdt.qlch.meetingservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
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
public class MeetingServiceApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MeetingServiceApplication.class, args);
    }

    @Value("${spring.kafka.bootstrap-servers}")
    private String avc;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(avc);
    }
}
