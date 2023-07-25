package com.fw.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.fw.bean.LoginResultBean;
import com.fw.domain.TempUser;
import com.fw.domain.UserManagement;
import com.fw.exceptions.APIExceptions;

public interface IUserManagementController {

	ResponseEntity<UserManagement> addUserManagement(
			UserManagement userManagement) throws APIExceptions;

	ResponseEntity<Integer> updateUserManagementById(
			UserManagement userManagement) throws APIExceptions;

	ResponseEntity<?> removeUserManagement(long userManagementId)
			throws APIExceptions;

	ResponseEntity<List<UserManagement>> getAllUserManagements(
			int clientProjectId, String applicable, String userIds,
			String searchTxt, String sortByColumn, String ascOrDesc, int limit,
			int pageNumber, String startDate, String endDate)
			throws APIExceptions;

	ResponseEntity<UserManagement> getUserManagementById(long Id,
			boolean applicable, boolean isDeleted) throws APIExceptions;

	ResponseEntity<List<UserManagement>> getAllUserManagements()
			throws APIExceptions;

	ResponseEntity<List<TempUser>> getUserAssignedOrg(String username)
			throws APIExceptions;

	ResponseEntity<String> setUserDefaultOrg(String org,
			LoginResultBean loginResultBean) throws APIExceptions;

	ResponseEntity<String> assignUserToProject(String org, String projectList,
			String userList) throws APIExceptions;
}
