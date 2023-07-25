package com.fw.controller.impl;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fw.config.AuthorizeUser;
import com.fw.controller.ITestCaseExecutionsController;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.TestCaseExecutions;
import com.fw.exceptions.APIExceptions;
import com.fw.exceptions.EmptyDataException;
import com.fw.exceptions.ImportDuplicateDataException;
import com.fw.services.ITestCaseExecutionsService;
import com.fw.utils.ApplicationCommonUtil;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class TestCaseExecutionsControllerImpl
		implements ITestCaseExecutionsController {

	private Logger log = Logger
			.getLogger(TestCaseExecutionsControllerImpl.class);

	@Autowired
	ITestCaseExecutionsService testCaseExecutionsService;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Autowired
	AuthorizeUser authorizeUser;

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/testCaseExecutions/addTestCaseExecutions", method = {
			POST })
	public ResponseEntity<?> addTestCaseExecutions(
			@RequestBody TestCaseExecutions testCaseExecutions)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		testCaseExecutionsService.addTestCaseExecutions(testCaseExecutions);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testCaseExecutions/getAllTestCaseExecutions", method = {
			GET })
	public ResponseEntity<?> getTestCaseExecutions() throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<TestCaseExecutions>>(
				testCaseExecutionsService.getTestCaseExecutions(),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testCaseExecutions/getSpecificTestCaseExecutionsDetails", method = {
			GET })
	public ResponseEntity<?> getTestCaseExecutionsById(
			@RequestParam("testCaseExecutionsId") long testCaseExecutionsId,
			@RequestParam("testCaseId") int testCaseId,
			@RequestParam("releaseId") int releaseId,
			@RequestParam("environmentId") int environmentId)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<TestCaseExecutions>(testCaseExecutionsService
				.getTestCaseExecutionsById(testCaseExecutionsId, testCaseId,
						releaseId, environmentId),
				HttpStatus.OK);
	}

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/testCaseExecutions/importTestExecutionOld", method = {
			POST }, consumes = { "multipart/form-data" })
	public ResponseEntity<?> importTestCase(
			@RequestPart("file") @Valid @NotNull @NotBlank MultipartFile uploadfile,
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("environmentId") int environmentId,
			@RequestParam("releaseId") int releaseId,
			@RequestParam("isSync") boolean isSync)
			throws APIExceptions, ImportDuplicateDataException,
			NullPointerException, EmptyDataException, Exception {
		updateDataSource();
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		testCaseExecutionsService.importTestCaseExecutions(uploadfile,
				clientProjectId, environmentId, releaseId, isSync);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/testCaseExecutions/importTestExecution", method = {
			POST }, consumes = { "multipart/form-data" })
	public ResponseEntity<?> importTestExecution(
			@RequestPart("file") @Valid @NotNull @NotBlank MultipartFile uploadfile,
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("environmentId") int environmentId,
			@RequestParam("releaseId") int releaseId,
			@RequestParam("isSync") boolean isSync)
			throws APIExceptions, ImportDuplicateDataException,
			NullPointerException, EmptyDataException, Exception {
		updateDataSource();
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		testCaseExecutionsService.importTestCaseExecutionsNew(uploadfile,
				clientProjectId, environmentId, releaseId, isSync);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
