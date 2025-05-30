package org.vdt.qlch.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.vdt.commonlib.config.CorsConfig;

@SpringBootApplication(
		scanBasePackages = {"org.vdt.qlch.userservice", "org.vdt.commonlib"}
)
@EnableConfigurationProperties(CorsConfig.class)
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
