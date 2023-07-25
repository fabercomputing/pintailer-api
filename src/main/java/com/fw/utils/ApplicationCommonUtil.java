package com.fw.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fw.bean.LDAPUserResponseBean;
import com.fw.bean.LoginResultBean;
import com.fw.config.CustomUser;
import com.fw.config.JwtTokenUtil;
import com.fw.controller.impl.UsersControllerImpl;
import com.fw.exceptions.APIExceptions;

@Service
public class ApplicationCommonUtil {

	private Logger log = Logger.getLogger(ApplicationCommonUtil.class);

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	public String getCurrentUser() throws APIExceptions {
		String userName = null;
		CustomUser user = getUserDetail();
		if (null != user) {
			userName = user.getUsername();
			if (!ValueValidations.isValueValid(userName)) {
				throw new APIExceptions(
						"Current user information is not available. "
								+ "Application will behave unexpected. "
								+ "Please re login the application and "
								+ "if the problem still persist, contact the admin.");
			}
		}
		return userName;
	}

	public String getDefaultOrg() throws APIExceptions {
		return getDefaultOrgInOriginalCase().replaceAll("\\s+", "_")
				.toUpperCase();
	}

	public String getDefaultOrgInOriginalCase() throws APIExceptions {
		String defaultOrg = "";
		CustomUser user = getUserDetail();
		if (null != user) {
			defaultOrg = user.getDefaultOrg();
			if (!ValueValidations.isValueValid(defaultOrg)) {
				throw new APIExceptions(
						"Default organization information is not available. "
								+ "Application will behave unexpected. "
								+ "Please re login the application and "
								+ "if the problem still persist, contact the admin.");
			}
			defaultOrg = defaultOrg.trim();
		}
		return defaultOrg;
	}

	public String getAssignedProjectIds() throws APIExceptions {
		String assignedProjectIds = null;
		CustomUser user = getUserDetail();
		if (null != user) {
			assignedProjectIds = user.getAssignedProjectIds();
			if (!ValueValidations.isValueValid(assignedProjectIds)) {
				log.info(
						"User is not assigned to any project. Atleast one project "
								+ "is required to continue.");
				return null;
			}
			assignedProjectIds = assignedProjectIds.trim();
		}
		return assignedProjectIds;
	}

	public CustomUser getUserDetail() throws APIExceptions {
		CustomUser user = null;
		try {
			Authentication authentication = SecurityContextHolder.getContext()
					.getAuthentication();

			if (!(authentication instanceof AnonymousAuthenticationToken)) {
				user = (CustomUser) authentication.getPrincipal();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new APIExceptions(
					"Error occured while fetching the current user details");
		}
		return user;
	}

	/**
	 * Validates Google reCAPTCHA V2 or Invisible reCAPTCHA.
	 * 
	 * @param secretKey Secret key (key given for communication between your
	 *                  site and Google)
	 * @param response  reCAPTCHA response from client side.
	 *                  (g-recaptcha-response)
	 * @return true if validation successful, false otherwise.
	 */
	public static boolean isCaptchaValid(String secretKey, String response) {
		try {
			String url = "https://www.google.com/recaptcha/api/siteverify?"
					+ "secret=" + secretKey + "&response=" + response;
			InputStream res = new URL(url).openStream();
			BufferedReader rd = new BufferedReader(
					new InputStreamReader(res, Charset.forName("UTF-8")));

			StringBuilder sb = new StringBuilder();
			int cp;
			while ((cp = rd.read()) != -1) {
				sb.append((char) cp);
			}
			String jsonText = sb.toString();
			res.close();

			JSONObject json = new JSONObject(jsonText);
			return json.getBoolean("success");
		} catch (Exception e) {
			return false;
		}
	}

	// This method is suppose to use while setting up default org and assigning
	// user to project API. This will logout the user. Currently its not in used
	// as the is handled on UI itself
	public void verifyToken(String userName, String defaultOrg,
			String assignedProjectIds) throws APIExceptions {
		LDAPDirContextUtil ldapUtil = new LDAPDirContextUtil();
		final ObjectMapper mapper = new ObjectMapper();
		UsersControllerImpl usersController = new UsersControllerImpl();

		Map<String, String> userAttr = ldapUtil
				.getUserDetailsByUserName(userName);
		LDAPUserResponseBean lDAPUserResponseBean = mapper
				.convertValue(userAttr, LDAPUserResponseBean.class);
		LoginResultBean userData = usersController
				.getUserData(lDAPUserResponseBean);
		userData.setDefaultOrganization(defaultOrg);
		userData.setAssignedProjectIds(assignedProjectIds);
		String newToken = jwtTokenUtil.generateToken(userData);
		String oldToken = getUserDetail().getToken();

		if (!newToken.equals(oldToken)) {
			throw new APIExceptions(
					"The user information is upated. Please relogin to use the application.");
		}
	}

	public long convertDateTimeInMillisec(final String dateTime)
			throws APIExceptions {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			Date date = sdf.parse(dateTime);
			return date.getTime();
		} catch (ParseException px) {
			throw new APIExceptions(
					"Invalid audit date is given. Try again with valid value in [yyyy/MM/dd HH:mm:ss] format");
		}
	}

	public String concatString(String separator, String... args) {
		boolean firstTime = true;
		StringBuilder result = new StringBuilder();
		for (String arg : args) {
			if (firstTime) {
				result.append(arg);
				firstTime = false;
			} else {
				result.append(separator).append(arg);
			}
		}
		return result.toString();
	}
}
