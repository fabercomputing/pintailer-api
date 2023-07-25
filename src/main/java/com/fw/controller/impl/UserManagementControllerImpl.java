package com.fw.controller.impl;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fw.bean.LoginResultBean;
import com.fw.controller.IUserManagementController;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.TempUser;
import com.fw.domain.UserManagement;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;
import com.fw.services.IUserManagementService;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class UserManagementControllerImpl implements IUserManagementController {

	@Autowired
	IUserManagementService userManagementService;

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/userManagement/addUserManagement", method = { POST })
	public ResponseEntity<UserManagement> addUserManagement(
			@RequestBody UserManagement userManagement) throws APIExceptions {
		updateDataSource();
		return new ResponseEntity<UserManagement>(
				userManagementService.persistUserManagementInfo(userManagement),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/userManagement/updateUserManagementById", method = { PATCH })
	public ResponseEntity<Integer> updateUserManagementById(
			@RequestBody UserManagement userManagement) throws APIExceptions {
		updateDataSource();
		return new ResponseEntity<Integer>(
				userManagementService
						.updateUserManagementInfoById(userManagement),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/userManagement/deleteUserManagement/{userId}", method = { DELETE })
	public ResponseEntity<?> removeUserManagement(
			@PathVariable("userId") long userId) throws APIExceptions {
		updateDataSource();
		userManagementService.deleteUserManagementInfoById(userId);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/userManagement/getUserManagementsWithFilters", method = { GET })
	public ResponseEntity<List<UserManagement>> getAllUserManagements(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("applicable") String applicable,
			@RequestParam("testCaseIds") String userIds,
			@RequestParam("userSearchTxt") String userSearchTxt,
			@RequestParam("sortByColumn") String sortByColumn,
			@RequestParam("ascOrDesc") String ascOrDesc,
			@RequestParam("limit") int limit,
			@RequestParam("pageNumber") int pageNumber,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) throws APIExceptions {
		return new ResponseEntity<List<UserManagement>>(
				userManagementService.getAllUserManagements(clientProjectId,
						applicable, userIds, userSearchTxt, sortByColumn,
						ascOrDesc, limit, pageNumber, startDate, endDate),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/userManagement/getUserManagementById", method = { GET })
	public ResponseEntity<UserManagement> getUserManagementById(long Id,
			boolean applicable, boolean isDeleted) throws APIExceptions {
		return new ResponseEntity<UserManagement>(
				userManagementService.getUserManagementById(Id, applicable,
						isDeleted), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/userManagement/getAllUserManagements", method = { GET })
	public ResponseEntity<List<UserManagement>> getAllUserManagements()
			throws APIExceptions {
		return new ResponseEntity<List<UserManagement>>(
				userManagementService.getAllUserManagements(), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/userManagement/getUserAssignedOrg", method = { GET })
	public ResponseEntity<List<TempUser>> getUserAssignedOrg(
			@RequestParam("userName") String userName) throws APIExceptions {
		return new ResponseEntity<List<TempUser>>(
				userManagementService.getUserAssignedOrg(userName),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/userManagement/setDefaultOrg", method = { POST })
	public ResponseEntity<String> setUserDefaultOrg(@RequestParam String org,
			@RequestBody LoginResultBean loginResultBean) throws APIExceptions {
		updateDataSource();
		return new ResponseEntity<String>(
				userManagementService.setUserDefaultOrg(org, loginResultBean),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/userManagement/assignUserToProject", method = { POST })
	public ResponseEntity<String> assignUserToProject(
			@RequestParam("organizationName") String organizationName,
			@RequestParam("projectList") String projectList,
			@RequestParam("userList") String userList) throws APIExceptions {
		updateDataSource();
		return new ResponseEntity<String>(
				userManagementService.assignUserToProject(organizationName,
						projectList, userList), HttpStatus.OK);
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(PintailerConstants.COMMON_ORG);
	}

}
