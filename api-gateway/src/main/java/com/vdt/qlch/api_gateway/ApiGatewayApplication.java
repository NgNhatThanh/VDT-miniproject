package com.vdt.qlch.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@SpringBootApplication
@EnableWebFluxSecurity
/*
Có một số vấn đề lằng nhằng khi chuyển từ gateway mvc sang webflux (mvc không hỗ trợ websocket nên đành chuyển)

Dính lỗi UnknownHost, tức là gateway không lấy được đúng host từ eureka về, thế là đành thêm cái prop
eureka.instance.hostname = localhost ở mọi service tới gateway
https://github.com/spring-cloud/spring-cloud-gateway/issues/2091
Có vẻ như prefer-ip-address: true cũng oke, nhưng tạm thời thêm cái trên thì hết lỗi
Mình hiểu đại khái là gateway không rõ các service dùng host nào, nên cần thống nhất 1 cái cho dễ lấy
 */
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
