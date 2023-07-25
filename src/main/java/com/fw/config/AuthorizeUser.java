package com.fw.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fw.dao.IClientProjectsManager;
import com.fw.domain.ClientProjects;
import com.fw.domain.Modules;
import com.fw.domain.UserManagement;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IModulesService;
import com.fw.services.IUserManagementService;
import com.fw.utils.ApplicationCommonUtil;
import com.fw.utils.ValueValidations;

@Service
public class AuthorizeUser {

	private static Logger log = Logger.getLogger(AuthorizeUser.class);

	@Autowired
	IClientProjectsManager clientProjectsManagerImpl;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Autowired
	IModulesService modulesService;

	@Autowired
	IUserManagementService userManagementService;

	/**
	 * Below methods will authorized the user based on the list of the
	 * organization coming from the LDap after successful login during user
	 * login in the application from the UI
	 */
	public boolean authorizeUserForOrganization(String organization)
			throws APIExceptions {
		if (!ValueValidations.isValueValid(organization)) {
			String message = "Invalid organization is selected. "
					+ "Please relogin and try again later.";
			log.info(message);
			throw new APIExceptions(message);
		}
		String defaultOrg = applicationCommonUtil.getDefaultOrgInOriginalCase();
		if (!organization.equalsIgnoreCase(defaultOrg)) {
			String message = "User is not authorize for the organization ["
					+ organization + "]. Please contant administrator";
			log.info(message);
			throw new APIExceptions(message);
		}
		return true;
	}

	public boolean authorizeAdminUserForOrganization(String organization)
			throws APIExceptions {
		if (!ValueValidations.isValueValid(organization)) {
			String message = "Invalid organization is selected. "
					+ "Please relogin and try again later.";
			log.info(message);
			throw new APIExceptions(message);
		}
		CustomUser customUser = applicationCommonUtil.getUserDetail();
		String allowedOrgsName = customUser.getUserOrganizations();
		List<String> orgsList = (List<String>) Arrays.asList(allowedOrgsName
				.replaceAll("^\\[|]$", "").split(","));
		if (!orgsList.contains(organization)) {
			String message = "Admin user is not authorize for the organization ["
					+ organization + "]. Please contant support";
			log.info(message);
			throw new APIExceptions(message);
		}
		return true;
	}

	public boolean authorizeUserOrganizationalRole(
			ArrayList<String> organizationalRoles) throws APIExceptions {
		CustomUser customUser = applicationCommonUtil.getUserDetail();
		String userOrganizationsRole = customUser.getUserOrganizationalRole();
		for (String organizationRole : organizationalRoles) {
			if (!userOrganizationsRole.contains(organizationRole)) {
				String message = "User is not authorize for the organization role ["
						+ organizationRole
						+ "] to continue using the application. "
						+ "Please contant administrator";
				log.info(message);
				throw new APIExceptions(message);
			}
		}
		return true;
	}

	/*********************************************************/

	/**
	 * Below methods will authorized the user based on the unique id of the
	 * client project passed in the API from the UI
	 */
	public boolean authorizeUserForProjectId(int clientProjectId)
			throws APIExceptions {
		if (clientProjectId == 0) {
			log.info("Request is made for all the projects");
			return true;
		}
		if (clientProjectId <= 0) {
			throw new APIExceptions(
					"Invalid project information is given. Contact administrator");
		}
		ClientProjects clientProject = clientProjectsManagerImpl
				.getClientProjectsById(clientProjectId);
		if (null == clientProject) {
			throw new APIExceptions(
					"Invalid project information is given. Contact administrator");
		}
		return authorizeUserForOrganization(clientProject
				.getClientOrganization());
	}

	/*********************************************************/

	/**
	 * Below methods will authorized the user based on the unique id of the test
	 * case module passed in the API from the UI
	 */
	public boolean authorizeUserForModuleId(long moduleId) throws APIExceptions {
		if (moduleId <= 0) {
			throw new APIExceptions(
					"Invalid module info is given. Contact administrator");
		}
		Modules modules = modulesService.getModulesById(moduleId);
		return authorizeUserForProjectId(modules.getClientProjectsId());
	}

	/*********************************************************/

	/**
	 * Below methods will authorized the user based on the generated token for
	 * the application. This will control the single sign on of the user. Every
	 * time the user will login its token will be updated in the DB.
	 */
	public boolean authorizeUserForTokenString() throws APIExceptions {
		String latestToken = applicationCommonUtil.getUserDetail().getToken();
		if (!ValueValidations.isValueValid(latestToken)) {
			throw new APIExceptions(
					"Invalid token is given. Contact administrator");
		}

		UserManagement userMgmt = userManagementService
				.getUserManagementByUserAndOrgName(
						applicationCommonUtil.getCurrentUser(),
						applicationCommonUtil.getDefaultOrgInOriginalCase());
		String existingToken = userMgmt.getUserToken();
		if (!latestToken.equals(existingToken)) {
			throw new APIExceptions("You are logged in on different location. "
					+ "Only single login is allowed for the user. "
					+ "This session is no longer valid.");
		}
		return true;
	}

	/*********************************************************/
}
