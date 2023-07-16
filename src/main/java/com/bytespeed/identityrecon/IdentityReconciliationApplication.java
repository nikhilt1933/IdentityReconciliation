package com.bytespeed.identityrecon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@ComponentScan(basePackages={"com.bytespeed.*"})
public class IdentityReconciliationApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdentityReconciliationApplication.class, args);
	}

}
