package com.example.remember_springai;

import org.springframework.boot.SpringApplication;

public class TestDemoApplication {

	public static void main(String[] args) {
		SpringApplication.from(RememberSpringaiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
