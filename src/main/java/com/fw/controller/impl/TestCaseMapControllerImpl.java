package com.fw.controller.impl;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fw.config.AuthorizeUser;
import com.fw.controller.ITestCaseMapController;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.TestCaseMap;
import com.fw.domain.TestCaseMapVersion;
import com.fw.exceptions.APIExceptions;
import com.fw.services.ITestCaseMapService;
import com.fw.utils.ApplicationCommonUtil;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.util.List;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class TestCaseMapControllerImpl implements ITestCaseMapController {

	private static Logger log = Logger
			.getLogger(TestCaseMapControllerImpl.class);

	@Autowired
	ITestCaseMapService testCaseMapService;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Autowired
	AuthorizeUser authorizeUser;

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/testCaseMap/addTestCaseMap", method = {
			POST })
	public ResponseEntity<?> addTestCaseMap(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("releaseId") int releaseId,
			@RequestParam("testCaseId") int testCaseId,
			@RequestParam("testCaseVersionId") String testCaseVersionId,
			@RequestParam("selectedTestStepIds") String selectedTestStepIds,
			@RequestParam("testScenarioId") int testScenarioId,
			@RequestParam("testScenarioStepVersionId") int testScenarioStepVersionId)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<Integer>(
				testCaseMapService.addTestCaseMap(clientProjectId, releaseId,
						testCaseId, testCaseVersionId, selectedTestStepIds,
						testScenarioId, testScenarioStepVersionId),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testCaseMap/updateTestCaseMap", method = {
			PATCH })
	public ResponseEntity<?> updateTestCaseMapById(
			@RequestBody TestCaseMap testCaseMapdata) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		testCaseMapService.updateTestCaseMapById(testCaseMapdata);
		return new ResponseEntity<TestCaseMap>(testCaseMapdata, HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testCaseMap/getTestCaseMap", method = {
			GET })
	public ResponseEntity<?> getTestCaseMapById(
			@RequestParam("testCaseMapId") int testCaseMapId)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<TestCaseMap>(
				testCaseMapService.getTestCaseMapById(testCaseMapId),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testCaseMap/deleteTestCaseMap", method = {
			DELETE })
	public ResponseEntity<?> removeTestCaseMap(
			@RequestParam("testCaseMapId") int testCaseMapId)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		testCaseMapService.deleteTestCaseMap(testCaseMapId);
		return new ResponseEntity<Void>(HttpStatus.OK);

	}

	@Override
	@RequestMapping(value = "/private/testCaseMap/getAllTestCaseMapForProject", method = {
			GET })
	public ResponseEntity<?> getTestCaseMap(
			@RequestParam("clientProjectID") int clientProjectId)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<List<TestCaseMap>>(
				testCaseMapService.getTestCaseMap(clientProjectId),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testCaseMap/getTestStepByTestCaseId", method = {
			GET })
	public ResponseEntity<?> getTestStepByTestCaseId(
			@RequestParam("clientProjectID") int clientProjectId,
			@RequestParam("releaseId") int releaseId,
			@RequestParam("testCaseId") int testCaseId,
			@RequestParam("testCaseVersionId") String testCaseVersionId)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<List<TestCaseMap>>(
				testCaseMapService.getTestStepByTestCaseId(clientProjectId,
						releaseId, testCaseId, testCaseVersionId, false),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testCaseMap/deleteTestStepByTestCaseId", method = {
			DELETE })
	public ResponseEntity<?> deleteTestStepByTestCaseId(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("releaseId") int releaseId,
			@RequestParam("testCaseId") int testCaseId,
			@RequestParam("scenarioID") int testScenarioID)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		testCaseMapService.deleteTestStepByTestCaseId(clientProjectId,
				releaseId, testCaseId, testScenarioID);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testCaseMap/getTestCaseMapVersion", method = {
			GET })
	public ResponseEntity<?> getTestCaseMapVersion(
			@RequestParam("testCaseId") int testCaseId,
			@RequestParam("testCaseVersionId") int testCaseVersionId)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<List<TestCaseMapVersion>>(testCaseMapService
				.getTestCaseMapVersion(testCaseId, testCaseVersionId),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testCaseMap/getTestCaseExistingMappedScenarioInfo", method = {
			GET })
	public ResponseEntity<?> getTestCaseExistingMappedScenarioInfo(
			@RequestParam("clientProjectID") int clientProjectId,
			@RequestParam("releaseId") int releaseId,
			@RequestParam("testCaseId") int testCaseId,
			@RequestParam("testCaseVersionId") String testCaseVersionId)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<TestCaseMapVersion>(testCaseMapService
				.getTestCaseExistingMappedScenarioInfo(clientProjectId,
						releaseId, testCaseId, testCaseVersionId),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testCaseMap/getTestCaseMapMaxVersionForSpecificPeriod", method = {
			GET })
	public ResponseEntity<?> getTestCaseMapMaxVersionForSpecificPeriod(
			@RequestParam("clientProjectID") int clientProjectId,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity<List<TestCaseMapVersion>>(
				testCaseMapService.getTestCaseMapMaxVersionForSpecificPeriod(
						clientProjectId, startDate, endDate),
				HttpStatus.OK);
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
