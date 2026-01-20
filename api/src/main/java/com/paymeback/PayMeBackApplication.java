package com.paymeback;

import com.paymeback.domain.security.jwt.JwtProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.TimeZone;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class PayMeBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayMeBackApplication.class, args);
	}

	@PostConstruct
	public void init() {
		// 서버 내부 및 DB 저장은 UTC 기준
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
}
