package com.mytutor;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class MyTutorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyTutorApplication.class, args);
	}

	@PostConstruct
	public void init() {
		// Setting default time zone to Vietnamese time zone
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
	}
}
