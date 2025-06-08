package org.vdt.qlch.voteservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.vdt.qlch.voteservice.config.ServiceUrlConfig;

@SpringBootApplication(
        scanBasePackages = {"org.vdt.qlch.voteservice", "org.vdt.commonlib"}
)
@EnableConfigurationProperties({ServiceUrlConfig.class})
@EnableScheduling
public class VoteServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VoteServiceApplication.class, args);
    }

}
