package com.fw;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FwTestManagementApplication {
	private final static Logger logger = Logger
			.getLogger(FwTestManagementApplication.class);

	public static void main(String[] args) {
//		System.setProperty("spring.devtools.restart.enabled", "false");
		ApplicationContext applicationContext = SpringApplication.run(
				FwTestManagementApplication.class, args);
		logger.info("Server Started ...."
				+ applicationContext.getApplicationName());
	}
}
