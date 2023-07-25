package com.fw.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.google.common.base.Predicate;

/**
 * 
 * @author Narendra Gurjar
 *
 */

@Configuration
@EnableSwagger2
@ComponentScan(basePackages = { "com.fw.controller.impl" })
@Profile("dev")
public class SwaggerConfig {
	private ApiKey apiKey() {
		return new ApiKey("authkey", "Authorization", "header");
	}

	@Bean
	public Docket testMgmtToolAllAPIs() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("All APIs")
				.select().apis(RequestHandlerSelectors
						.basePackage("com.fw.controller.impl"))
//				.paths(excludePath("delete")).paths(excludePath("remove"))
//				.paths(excludePath("update")).paths(excludePath("add"))
//				.paths(excludePath("download")).paths(excludePath("insert"))
//				.paths(excludePath("login"))
				.build().apiInfo(apiInfo())
				.securitySchemes(Arrays.asList(apiKey()));
	}

	@Bean
	public Docket testMgmtToolAuditAPIs() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("Audit APIs")
				.select()
				.apis(RequestHandlerSelectors
						.basePackage("com.fw.controller.impl"))
				.paths(includePath("appAudit")).build().apiInfo(apiInfo())
				.securitySchemes(Arrays.asList(apiKey()));
	}

	private ApiInfo apiInfo() {
		return new ApiInfo("fwTestManagement APIs", "API documentation",
				"API TOS", "Terms of service",
				new Contact("Faberwork", "http://dev.tm.fwia.site/",
						"team@faberwork.com"),
				"License of API", "www.fwTestManagement.biz",
				Collections.emptyList());
	}

	private Predicate<String> includePath(final String path) {
		return new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				return input.contains(path);
			}
		};
	}

//	private Predicate<String> excludePath(final String path) {
//		return new Predicate<String>() {
//			@Override
//			public boolean apply(String input) {
//				return !input.contains(path);
//			}
//		};
//	}
}
