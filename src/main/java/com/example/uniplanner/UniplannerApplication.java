package com.example.uniplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {
		UserDetailsServiceAutoConfiguration.class
})
public class UniplannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UniplannerApplication.class, args);
	}
}
