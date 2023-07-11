package com.mbb.bts.casa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan(basePackages = { "com.mbb.bts" })
@Slf4j
@EnableSwagger2
public class CasaApplication extends SpringBootServletInitializer {

	// Start for external server deployment
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		log.info("Hello! This is CASA Services Application...Environment : " + System.getProperty("bts.env"));

		return application.profiles(System.getProperty("bts.env")).sources(CasaApplication.class);
	}
	// End for external server deployment

	public static void main(String[] args) {

		SpringApplication app = new SpringApplication(CasaApplication.class);
		app.setAdditionalProfiles("dev");
		app.run();

		log.info("Hello! This is CASA Services Application...");
	}

}
