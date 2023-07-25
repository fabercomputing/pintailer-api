package com.fw.controller.impl;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fw.bean.AllGroups;
import com.fw.bean.ContactDetailUserInfo;
import com.fw.bean.GetAllUserBean;
import com.fw.bean.LDAPUserResponseBean;
import com.fw.bean.LoginResultBean;
import com.fw.bean.UserBean;
import com.fw.bean.UserDetailInfo;
import com.fw.config.JwtTokenUtil;
import com.fw.controller.IUsersController;
import com.fw.domain.UserManagement;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;
import com.fw.services.IUserManagementService;
import com.fw.services.IUserService;
import com.fw.utils.ApplicationCommonUtil;
import com.fw.utils.LocalUtils;
import com.fw.utils.ValueValidations;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class UsersControllerImpl implements IUsersController {

	private Logger log = Logger.getLogger(UsersControllerImpl.class);

	@Autowired
	IUserService userService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	// @Autowired
	// IUserManagementManager userManagementManager;

	@Autowired
	IUserManagementService userManagementService;

	@Override
	@ResponseBody
	@RequestMapping(value = "/public/users/loginUser", method = { POST })
	public ResponseEntity<?> loginUser(@RequestBody UserBean userBean, @RequestParam String gcaptha) {

		if (ApplicationCommonUtil.isCaptchaValid(PintailerConstants.GCAPTCHA, gcaptha)) {
			try {
				return new ResponseEntity<LoginResultBean>(login(userBean), HttpStatus.OK);
			} catch (APIExceptions e) {
				String message = e.getMessage();
				log.error(message);
				return new ResponseEntity<String>(message, HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity<String>(
					"Don't be naughty !! You are not authorized to login inside the application.",
					HttpStatus.UNAUTHORIZED);
		}
	}

	@Override
	public LoginResultBean login(UserBean userBean) throws APIExceptions {
		LDAPUserResponseBean LDAPUserResponseBean = userService.loginUser(userBean);
		LoginResultBean loginUser = getUserData(LDAPUserResponseBean);
		loginUser = updateUserOrgProjectInfo(loginUser);
		final String token = jwtTokenUtil.generateToken(loginUser);
		loginUser.setToken(token);
		updateUserToken(loginUser);
		return loginUser;
	}

	private void updateUserToken(LoginResultBean loginUser) throws APIExceptions {
		List<UserManagement> userOrganizationInfo = userManagementService
				.getUserOrganizationInfoFromDB(loginUser.getUserName());
		for (UserManagement userMgmt : userOrganizationInfo) {
			userMgmt.setUserToken(loginUser.getToken());
			if (userManagementService.updateUserManagementInfoById(userMgmt) <= 0) {
				throw new APIExceptions("Error occured whle upting the user " + "login info. Please try again later.");
			}
		}
	}

	private LoginResultBean updateUserOrgProjectInfo(LoginResultBean loginUser) throws APIExceptions {
		List<UserManagement> userOrganizationInfo = userManagementService
				.getUserOrganizationInfoFromDB(loginUser.getUserName());
		boolean firstTime = true;
		// Checking if the information of the user is available or not. In case
		// of no or partial organization info is available, following loop will
		// execute to enter rest of the assigned organization details and set
		// default organization and project
		if (null == userOrganizationInfo || userOrganizationInfo.size() <= 0
				|| loginUser.getUserOrganizations().size() > userOrganizationInfo.size()) {
			boolean defaultOrgAlreadyExist = false;
			List<String> userOrganizations = loginUser.getUserOrganizations();
			for (String orgName : userOrganizations) {

				// In case of partial organization info i.e. user is assigned to
				// 3
				// projects but only 2 project info is available, the check is
				// applied if the already presented organizations are default or
				// not
				if (!defaultOrgAlreadyExist && userOrganizationInfo.size() > 0
						&& (loginUser.getUserOrganizations().size() > userOrganizationInfo.size())) {
					String defaultOrg = userManagementService.getDefaultOrganizationOfUser(loginUser.getUserName());
					if (ValueValidations.isValueValid(defaultOrg)) {
						defaultOrgAlreadyExist = true;
						loginUser.setDefaultOrganization(defaultOrg);
					}
				}
				if (null != userManagementService.getUserOrganizationInfoFromDB(orgName, loginUser.getUserName())) {
					firstTime = false;
					continue;
				}
				UserManagement userManagement = new UserManagement();
				userManagement.setUserName(loginUser.getUserName());
				userManagement.setUserOrg(orgName);
				userManagement.setUserEmail(loginUser.getEmail());
				userManagement.setApplicable(true);
				userManagement.setCreatedBy(loginUser.getUserName());
				userManagement.setModifiedBy(loginUser.getUserName());
				userManagement.setDeleted(false);
				if (firstTime && !defaultOrgAlreadyExist) {
					userManagement.setDefaultOrg(true);
					// applicationCommonUtil.updateContext(orgName);
					firstTime = false;
					loginUser.setDefaultOrganization(orgName);
				}
				if (null == userManagementService.persistUserManagementInfo(userManagement)) {
					throw new APIExceptions("Error occured while adding the user Organization info "
							+ "for the first tie as no user organization "
							+ "information is available in the DB previously. "
							+ "Please contact the admin and try again.");
				}
			}
			loginUser.setAssignedProjectIds("");
			return loginUser;
		}
		String defaultOrg = null;
		String assignedProjectIds = "";
		boolean isDefaultOrgSelected = false;
		for (UserManagement userMgmt : userOrganizationInfo) {
			if (!loginUser.getUserOrganizations().contains(userMgmt.getUserOrg())) {
				// Check if the assigned organizations in DB is a valid
				// organization
				// stored in LDAP. If both information mismatch, it means that
				// the wrong data was sent from client due to some security loop
				// hole. In case this exception occurs, we have to fix it.
				throw new APIExceptions("User is assigned to unauthorized organization [" + userMgmt.getUserOrg()
						+ "]. Please contact the admin and try again later.");
			} else {
				// organization which is default selected will be returned
				if (isDefaultOrgSelected && userMgmt.isDefaultOrg()) {
					throw new APIExceptions("Two different organizations cannot be made default "
							+ "at the same time. Please contact admin and " + "try again later");
				}
				if (!isDefaultOrgSelected && userMgmt.isDefaultOrg()) {
					isDefaultOrgSelected = true;
					defaultOrg = userMgmt.getUserOrg();
					if (null != userMgmt.getUserProject()) {
						assignedProjectIds = userMgmt.getUserProject();
					}
					// applicationCommonUtil.updateContext(userMgmt.getUserOrg());
				}
			}
		}
		if (!isDefaultOrgSelected) {
			// In case the default organization is not selected, the first
			// organization will be selected as the default organization
			defaultOrg = userOrganizationInfo.get(0).getUserOrg();
			UserManagement userManagement = userOrganizationInfo.get(0);
			userManagement.setDefaultOrg(true);
			// applicationCommonUtil.updateContext(userManagement.getUserOrg());
			if (userManagementService.updateUserManagementInfoById(userManagement) <= 0) {
				throw new APIExceptions("The default organization cannot be selected due to some "
						+ "error. Please contant the admin and try again later.");
			}
		}
		loginUser.setDefaultOrganization(defaultOrg);
		loginUser.setAssignedProjectIds(assignedProjectIds);
		return loginUser;
	}

	@Override
	@RequestMapping(value = "/private/users/isUserLoggedIn", method = { GET })
	public boolean isUserLoggedIn() {
		return true;
	}

	public LoginResultBean getUserData(LDAPUserResponseBean user) {
		ArrayList<String> groupList = new ArrayList<String>(Arrays.asList(user.getGroups().toString().split(",")));
		List<String> userOrganizationsList = new ArrayList<String>();
		List<String> userOrganizationsRoleList = new ArrayList<String>();
		List<String> userApplicationsList = new ArrayList<String>();
		log.debug("=====groupList===== " + groupList);
		for (String array : groupList) {
			if (array.startsWith("client")) {
				array = array.substring(array.lastIndexOf("_") + 1);
				userOrganizationsList.add(array);
			}
			if (array.startsWith("team_")) {
				array = array.substring(array.lastIndexOf("_") + 1);
				userOrganizationsRoleList.add(array);
			}
			if (array.startsWith("application_")) {
				array = array.substring(array.lastIndexOf("_") + 1);
				userApplicationsList.add(array);
			}
		}
		log.debug("ClientList " + userOrganizationsList);
		log.debug("RoleList " + userOrganizationsRoleList);
		log.debug("ApplicationList " + userApplicationsList);
		// List<ApplicationBean> applicationBeanList = new
		// ArrayList<ApplicationBean>();
		// for (String bean : userApplicationsList) {
		// ApplicationBean applicationBean = new ApplicationBean();
		// Map<String, String> map = new HashMap<String, String>();
		// String[] array = bean.split("-");
		// map.put("application", array[0]);
		// map.put("role", array[1]);
		// ObjectMapper mapper = new ObjectMapper();
		// applicationBean = mapper.convertValue(map, ApplicationBean.class);
		// applicationBeanList.add(applicationBean);
		// }
		LoginResultBean authDetails = new LoginResultBean();
		authDetails.setUniqueLDAPId(user.getUniqueLDAPId());
		authDetails.setEmployeeNumber(user.getEmployeeNumber());
		authDetails.setUserName(user.getUserName());
		authDetails.setEmail(user.getEmail());
		authDetails.setUserOrganizations(userOrganizationsList);
		authDetails.setUserOrganizationalRole(userOrganizationsRoleList);
		authDetails.setUserApplicationsAndRoles(userApplicationsList);
		return authDetails;
	}

	@Override
	@RequestMapping(value = "/private/users/getUserList", method = { GET })
	public ResponseEntity<List<?>> getUserList(@RequestParam("group") String groupValue)
			throws NamingException, APIExceptions, Exception {
		try {
			List<GetAllUserBean> getAllUserBean = null;
			getAllUserBean = userService.getUserList(groupValue);
			return new ResponseEntity<List<?>>(getAllUserBean, HttpStatus.OK);
		} catch (NamingException e) {
			log.error(
					"Error occured while fetching the list of users for group[" + groupValue + "] : " + e.getMessage());
			throw new NamingException(LocalUtils.getStringLocale("fw_test_mgmt_locale", "ConnectionIssueWithLDAP"));
		} catch (APIExceptions e) {
			log.error(
					"Error occured while fetching the list of users for group[" + groupValue + "] : " + e.getMessage());
			throw new NamingException(LocalUtils.getStringLocale("fw_test_mgmt_locale", "UserList"));
		}
	}

	@Override
	@RequestMapping(value = "/private/users/getGroups", method = { GET })
	public ResponseEntity<List<?>> getGroups() throws APIExceptions, NamingException, Exception {
		try {
			List<AllGroups> getAllUserBean = userService.getGroups();
			return new ResponseEntity<List<?>>(getAllUserBean, HttpStatus.OK);
		} catch (NamingException e) {
			String message = LocalUtils.getStringLocale("fw_test_mgmt_locale", "ConnectionIssueWithLDAP");
			log.error(message);
			throw new NamingException(message);
		} catch (APIExceptions e) {
			String message = LocalUtils.getStringLocale("fw_test_mgmt_locale", "UserList");
			log.error(message);
			throw new NamingException(message);
		}
	}

	@Override
	@RequestMapping(value = "/private/users/getUserDetailByID", method = { GET })
	public ResponseEntity<List<?>> getUserDetail(@RequestParam("userID") String userID)
			throws NamingException, APIExceptions, Exception {
		List<UserDetailInfo> getAllUserBean = userService.getUserDetailsByUserName(userID);
		return new ResponseEntity<List<?>>(getAllUserBean, HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/users/getContactDetailByID", method = { GET })
	public ResponseEntity<List<?>> getContactDetail(@RequestParam("userID") String userID)
			throws NamingException, APIExceptions, Exception {
		List<ContactDetailUserInfo> getAllUserBean = userService.getUserContactDetailsByUserName(userID);
		return new ResponseEntity<List<?>>(getAllUserBean, HttpStatus.OK);
	}

	@Override
	@ResponseBody
	@RequestMapping(value = "/public/users/updateUserPassword", method = { POST })
	public ResponseEntity<String> updateUserPassword(@RequestParam("username") String username,
			@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword,
			@RequestParam String gcaptha) throws APIExceptions {
		if (ApplicationCommonUtil.isCaptchaValid(PintailerConstants.GCAPTCHA, gcaptha)) {
			return new ResponseEntity<String>(userService.updateUserPassword(username, oldPassword, newPassword),
					HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("Don't be naughty !! You are not authorized to update the password.",
					HttpStatus.UNAUTHORIZED);
		}
	}

	@Override
	@RequestMapping(value = "/private/users/createUser", method = { POST })
	public void createUser(@RequestParam("adminUsername") final String adminUsername,
			@RequestParam("adminPass") final String adminPass, @RequestParam("firstName") final String firstName,
			@RequestParam("middleName") final String middleName, @RequestParam("lastName") final String lastName,
			@RequestParam("email") final String email, @RequestParam("userName") final String userName,
			@RequestParam("postalAddress") final String postalAddress,
			@RequestParam("telephoneNo") final String telephoneNo, @RequestParam("password") final String password)
			throws APIExceptions {
		userService.createUser(adminUsername, adminPass, firstName, middleName, lastName, email, userName,
				postalAddress, telephoneNo, password);
	}

}
