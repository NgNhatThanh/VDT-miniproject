package com.vdt.qlch.auth_service;

//import com.vdt.qlch.auth_service.entity.User;
import com.vdt.qlch.auth_service.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class AuthServiceApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

	@Autowired
	private JwtService jwtService;

	@Override
	public void run(String... args) throws Exception {
//		User u = new User();
//		u.setAvatarUrl("sfsfd");
//		u.setFullName("Sdfsdf");
//		Map<String, Object> mp = new HashMap<>();
//		mp.put("iss", "http://localhost:8081");
//		mp.put("role", "MEMBER");
//		String token = jwtService.generateAccessToken(u, mp);
//		System.out.println(token);
	}
}
