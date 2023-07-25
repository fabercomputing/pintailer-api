package com.fw.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fw.bean.LoginResultBean;
import com.fw.dao.IUserManagementManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.TempUser;
import com.fw.domain.UserManagement;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;
import com.fw.services.IUserManagementService;
import com.fw.utils.ValueValidations;

@Service
public class UserManagementServiceImpl implements IUserManagementService {

	@Autowired
	IUserManagementManager userManagementManager;

	@Override
	@Transactional
	public UserManagement persistUserManagementInfo(UserManagement logEntity)
			throws APIExceptions {
		updateDataSource();
		return userManagementManager.persistUserManagementInfo(logEntity);
	}

	@Override
	@Transactional
	public int updateUserManagementInfoById(UserManagement logEntity)
			throws APIExceptions {
		updateDataSource();
		return userManagementManager.updateUserManagementInfoById(logEntity);
	}

	@Override
	@Transactional
	public void deleteUserManagementInfoById(long Id) throws APIExceptions {
		updateDataSource();
		userManagementManager.deleteUserManagementInfoById(Id);
	}

	@Override
	public List<UserManagement> getAllUserManagements(int clientProjectId,
			String applicable, String userIds, String userSearchTxt,
			String sortByColumn, String ascOrDesc, int limit, int pageNumber,
			String startDate, String endDate) throws APIExceptions {
		return userManagementManager.getAllUserManagements(clientProjectId,
				applicable, userIds, userSearchTxt, sortByColumn, ascOrDesc,
				limit, pageNumber, startDate, endDate);
	}

	@Override
	public UserManagement getUserManagementById(long Id, boolean applicable,
			boolean isDeleted) throws APIExceptions {
		return userManagementManager.getUserManagementById(Id, applicable,
				isDeleted);
	}

	@Override
	public UserManagement getUserManagementByUserAndOrgName(String userName,
			String orgName) throws APIExceptions {
		return userManagementManager.getUserManagementByUserAndOrgName(
				userName, orgName);
	}

	@Override
	public List<UserManagement> getAllUserManagements() throws APIExceptions {
		return userManagementManager.getAllUserManagements();
	}

	@Override
	public String getDefaultOrganizationOfUser(String userName)
			throws APIExceptions {
		return userManagementManager.getDefaultOrg(userName);
	}

	@Override
	public List<TempUser> getUserAssignedOrg(String userName)
			throws APIExceptions {
		List<UserManagement> userOrganizations = userManagementManager
				.getUserOrganizationInfoFromDB(userName);
		ArrayList<TempUser> assignedOrgs = new ArrayList<TempUser>();
		for (UserManagement userManagement : userOrganizations) {
			TempUser tempUser = new TempUser();
			tempUser.orgName = userManagement.getUserOrg();
			assignedOrgs.add(tempUser);
		}
		return assignedOrgs;
	}

	@Override
	public List<UserManagement> getUserOrganizationInfoFromDB(String userName)
			throws APIExceptions {
		return userManagementManager.getUserOrganizationInfoFromDB(userName);
	}

	@Override
	public UserManagement getUserOrganizationInfoFromDB(String orgName,
			String userName) throws APIExceptions {
		return userManagementManager.getUserOrganizationInfoFromDB(orgName,
				userName);
	}

	@Override
	@Transactional
	public String setUserDefaultOrg(String orgName,
			LoginResultBean loginResultBean) throws APIExceptions {
		updateDataSource();
		if (userManagementManager.isUserOrgInfoExist(orgName,
				loginResultBean.getUserName())) {

			// Re set the default flag to false for all the current organization
			// of the given user. this is to maintain single default org
			List<UserManagement> userOrganizations = userManagementManager
					.getUserOrganizationInfoFromDB(loginResultBean
							.getUserName());
			for (UserManagement userManagement : userOrganizations) {
				userManagement.setDefaultOrg(false);
				if (userManagementManager
						.updateUserManagementInfoById(userManagement) <= 0) {
					throw new APIExceptions(
							"Some error occured while reset the default organization for user ["
									+ loginResultBean.getUserName() + "]");
				}

			}
			// ///

			UserManagement userManagement = userManagementManager
					.getUserOrganizationInfoFromDB(orgName,
							loginResultBean.getUserName());
			userManagement.setDefaultOrg(true);
			if (userManagementManager
					.updateUserManagementInfoById(userManagement) <= 0) {
				throw new APIExceptions(
						"Some error occured while setting the organization ["
								+ orgName + "] as default for user ["
								+ loginResultBean.getUserName() + "]");
			}
		}
		return null;
	}

	@Override
	@Transactional
	public String assignUserToProject(String org, String projectList,
			String userList) throws APIExceptions {
		updateDataSource();
		if (!ValueValidations.isValueValid(org)) {
			throw new APIExceptions("Organization is not provided.");
		} else if (!ValueValidations.isValueValid(userList)) {
			throw new APIExceptions(
					"At least one User is required to assign it to project "
							+ "and organization.");
		}
		// else if (!ValueValidations.isValueValid(projectList)) {
		// throw new APIExceptions(
		// "At least one project is required to assign it to user.");
		// }

		// Iterator user list and assign it to project or organization
		String[] orgs = org.split(",");
		for (String orgName : orgs) {
			String[] users = userList.split(",");
			for (String user : users) {
				boolean isUserInfoExist = true;
				// Fetching the existing info from DB to update the assigned
				// project
				UserManagement userManagement = userManagementManager
						.getUserOrganizationInfoFromDB(orgName, user);
				if (null == userManagement) {
					isUserInfoExist = false;
					// Adding the user info in the user management table if not
					// available. This information will be added by Admin of
					// that
					// organization only
					userManagement = new UserManagement();
					userManagement.setUserName(user);
					userManagement.setUserOrg(orgName);
					userManagement.setCreatedBy("test");
					userManagement.setModifiedBy("test");
					userManagement.setApplicable(true);
					userManagement.setDeleted(false);
					userManagement.setUserProject(projectList.equals("") ? null
							: projectList);
					userManagement.setDefaultOrg(false);
				} else {
					userManagement.setUserProject(projectList.equals("") ? null
							: projectList);
					// userManagement.setDefaultOrg(false);
				}

				// Setting up the default organization by Admin.. User can
				// change it later in settings

				// Fetching the organization info of user to check if there is
				// already default organization because at a time, single
				// organization can be set as default
				List<UserManagement> userOrganizations = userManagementManager
						.getUserOrganizationInfoFromDB(user);
				boolean alreadyDefaultSelected = false;
				for (UserManagement userMgmt : userOrganizations) {
					if (userMgmt.isDefaultOrg()) {
						alreadyDefaultSelected = true;
						break;
					}
				}

				// If no existing organization is set as default, the
				// organization will be set as default
				if (!alreadyDefaultSelected) {
					userManagement.setDefaultOrg(true);
				}

				if (!isUserInfoExist) {
					if (null == userManagementManager
							.persistUserManagementInfo(userManagement)) {
						throw new APIExceptions(
								"Error occureded while assiging the user ["
										+ user + "] to organization ["
										+ orgName + "] and its project");
					}
				} else {
					if (userManagementManager
							.updateUserManagementInfoById(userManagement) <= 0) {
						throw new APIExceptions(
								"Error occureded while assiging the user ["
										+ user + "] to organization ["
										+ orgName + "] and its project");
					}
				}

			}
		}
		return "Succesfully assigned";
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(PintailerConstants.COMMON_ORG);
	}
}
