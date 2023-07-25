package com.fw.config;

/**
 * @author Sumit Srivastava
 *
 */

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fw.bean.LoginResultBean;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;

@Component
public class ResponseFilter extends OncePerRequestFilter {

	private Logger log = Logger.getLogger(ResponseFilter.class);

	@Autowired
	private AuthUserDetails authUser;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(
				response);
		try {
			LoginResultBean user = authUser.getAuthUserDetails();
			if (null != user) {
				wrapper.addHeader(PintailerConstants.USER_DETAIL_HEADER_STRING,
						createJsonRespone(user));
			} else {
				wrapper.addHeader(PintailerConstants.USER_DETAIL_HEADER_STRING,
						"");
			}

		} catch (APIExceptions e) {
			String message = "Error occured : " + e.getMessage();
			log.error(message);
		}
		filterChain.doFilter(request, wrapper);
	}

	// private String getHeaderValue(LoginResultBean user) throws APIExceptions
	// {
	// return user.getUniqueLDAPId() + "," + user.getEmail()
	// + "," + user.getUserOrganizations() + ","
	// + user.getUserOrganizationalRole();
	// }

	private String createJsonRespone(LoginResultBean user) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(user);
		} catch (JsonProcessingException e) {
			System.out.println("Error occured while fethcing logged in user "
					+ "details in response");
			e.printStackTrace();
			return null;
		}
	}

}
