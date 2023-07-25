package com.fw.config;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public JwtAuthorizationFilter authorizationTokenFilterBean()
			throws Exception {
		return new JwtAuthorizationFilter();
	}

	static SessionRegistry SR;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf()
				.disable()
				.authorizeRequests()
				.antMatchers(HttpMethod.OPTIONS, "/fwTestManagement/**",
						"/fwTestManagement/***", "/fwTestManagement/****")
				.permitAll()
				// allow CORS option calls
				.antMatchers("/fwTestManagement/public/**",
						"/fwTestManagement/public/***",
						"/fwTestManagement/private/**",
						"/fwTestManagement/private/***", "/v2/api-docs/**",
						"/configuration/ui", "/swagger-resources/**",
						"/configuration/**", "/swagger-ui.html/**",
						"/webjars/**", "/fwTestManagement-WebSocket/**")
				.permitAll()
				.anyRequest()
				.authenticated()
				.and()
				.exceptionHandling()
				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.addFilterBefore(authorizationTokenFilterBean(),
						UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	public Md5PasswordEncoder encoder() {
		return new Md5PasswordEncoder();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.ldapAuthentication().contextSource()
				.url("ldap://ldap.faberwork.com/dc=faberwork,dc=com").and()
				.userSearchBase("ou=people").userSearchFilter("(uid={0})")
				.ldapAuthoritiesPopulator(new LdapAuthoritiesPopulator() {
					@Override
					public Collection<? extends GrantedAuthority> getGrantedAuthorities(
							DirContextOperations userData, String username) {
						return Collections
								.singleton(new SimpleGrantedAuthority(
										"ROLE_USER"));
					}
				});
	}
}
