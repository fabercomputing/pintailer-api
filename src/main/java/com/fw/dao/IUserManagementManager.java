package com.fw.dao;

import java.util.List;

import com.fw.domain.UserManagement;
import com.fw.exceptions.APIExceptions;

public interface IUserManagementManager {

	UserManagement persistUserManagementInfo(UserManagement logEntity)
			throws APIExceptions;

	int updateUserManagementInfoById(UserManagement logEntity)
			throws APIExceptions;

	void deleteUserManagementInfoById(long Id) throws APIExceptions;

	List<UserManagement> getAllUserManagements(int clientProjectId,
			String applicable, String userIds, String userSearchTxt,
			String sortByColumn, String ascOrDesc, int limit, int pageNumber,
			String startDate, String endDate) throws APIExceptions;

	UserManagement getUserManagementById(long Id, boolean applicable,
			boolean isDeleted) throws APIExceptions;

	List<UserManagement> getAllUserManagements() throws APIExceptions;

	List<UserManagement> getUserOrganizationInfoFromDB(String userName)
			throws APIExceptions;

	boolean isUserOrgInfoExist(String orgName, String userName)
			throws APIExceptions;

	UserManagement getUserOrganizationInfoFromDB(String orgName, String userName)
			throws APIExceptions;

	String getDefaultOrg(String userName) throws APIExceptions;

	UserManagement getUserManagementByUserAndOrgName(String userName,
			String orgName) throws APIExceptions;
}
