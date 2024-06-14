package com.mytutor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MyTutorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyTutorApplication.class, args);
	}

}
