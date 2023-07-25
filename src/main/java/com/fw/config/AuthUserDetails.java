package com.fw.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fw.bean.LoginResultBean;
import com.fw.controller.impl.UsersControllerImpl;
import com.fw.exceptions.APIExceptions;
import com.fw.utils.ApplicationCommonUtil;
import com.fw.utils.LDAPDirContextUtil;

/**
 **
 * 
 * @author Sumit Srivastava
 *
 */
@Service
public class AuthUserDetails {

	LDAPDirContextUtil ldapUtil = new LDAPDirContextUtil();
	UsersControllerImpl usersController = new UsersControllerImpl();
	ApplicationCommonUtil applicationCommonUtil = new ApplicationCommonUtil();

	public LoginResultBean getAuthUserDetails() throws APIExceptions {
		LoginResultBean loginUser = null;
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();

		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			CustomUser userDetail = applicationCommonUtil.getUserDetail();
			loginUser = new LoginResultBean();
			loginUser.setDefaultOrganization(userDetail.getDefaultOrg());
			loginUser.setEmail(userDetail.getEmail());
			loginUser.setEmployeeNumber("");
			loginUser.setToken("");
			loginUser.setUniqueLDAPId("");

			loginUser.setUserName(userDetail.getUsername());
			loginUser.setUserOrganizationalRole((List<String>) Arrays
					.asList(userDetail.getUserOrganizationalRole()
							.replaceAll("^\\[|]$", "").split(",")));
			loginUser.setUserOrganizations((List<String>) Arrays
					.asList(userDetail.getUserOrganizations()
							.replaceAll("^\\[|]$", "").split(",")));
			loginUser.setUserApplicationsAndRoles((List<String>) Arrays
					.asList(userDetail.getUserProjectRole()
							.replaceAll("^\\[|]$", "").split(",")));
		}
		return loginUser;
	}

	// public String getLanguageHeader() throws InvalidUsernameException {
	// CustomUser customUser = (CustomUser) SecurityContextHolder.getContext()
	// .getAuthentication().getPrincipal();
	// String language = customUser.getLanguage();
	// return language;
	// }
}
