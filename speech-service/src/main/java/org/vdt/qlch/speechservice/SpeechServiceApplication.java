package org.vdt.qlch.speechservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.vdt.qlch.speechservice.config.ServiceUrlConfig;

@SpringBootApplication(
        scanBasePackages = {"org.vdt.qlch.speechservice", "org.vdt.commonlib"}
)
@EnableConfigurationProperties({ServiceUrlConfig.class})
public class SpeechServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpeechServiceApplication.class, args);
    }

}
