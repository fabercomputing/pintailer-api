package com.fw.controller.impl;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.util.List;

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
import com.fw.controller.ITestScenariosController;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.TestScenarios;
import com.fw.domain.TestScenariosVersion;
import com.fw.exceptions.APIExceptions;
import com.fw.services.ITestScenariosService;
import com.fw.utils.ApplicationCommonUtil;
import com.fw.utils.ValueValidations;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class TestScenariosControllerImpl implements ITestScenariosController {

	private static Logger log = Logger
			.getLogger(TestScenariosControllerImpl.class);

	@Autowired
	ITestScenariosService testScenariosService;

	@Autowired
	AuthorizeUser authorizeUser;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/testScenarios/addTestScenarios", method = {
			POST })
	public ResponseEntity<?> addTestScenarios(
			@RequestBody TestScenarios testScenarios) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(
					testScenarios.getClientProjectId());
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<TestScenarios>(
				testScenariosService.addTestScenarios(testScenarios),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testScenarios/updateTestScenarios", method = {
			PATCH })
	public ResponseEntity<?> updateTestScenariosById(
			@RequestBody TestScenarios testScenarios) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(
					testScenarios.getClientProjectId());
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		testScenariosService.updateTestScenariosById(testScenarios);
		return new ResponseEntity<TestScenarios>(testScenarios, HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testScenarios/getTestScenarios", method = {
			GET })
	public ResponseEntity<?> getTestScenariosById(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("testScenariosId") int testScenariosId,
			@RequestParam("testScenariosVersionId") String testScenariosVersionId)
			throws APIExceptions {
		updateDataSource();
		TestScenarios testScenarios = null;
		if (ValueValidations.isValueValid(testScenariosVersionId)) {
			testScenarios = testScenariosService
					.getTestScenariosByScenarioIdAndVersionId(clientProjectId,
							testScenariosId, testScenariosVersionId);
			if (null == testScenarios
					&& testScenariosVersionId.equalsIgnoreCase("V1")) {
				testScenarios = testScenariosService
						.getTestScenariosById(testScenariosId, "false");
			}
		} else {
			testScenarios = testScenariosService
					.getTestScenariosById(testScenariosId, "false");
		}
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(
					testScenarios.getClientProjectId());
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<TestScenarios>(testScenarios, HttpStatus.OK);
	}

//	@Override
//	@RequestMapping(value = "/private/testScenarios/deleteTestScenarios", method = {
//			DELETE })
//	public ResponseEntity<?> removeTestScenarios(
//			@RequestParam("testScenariosId") int testScenariosId)
//			throws APIExceptions {
//		TestScenarios testScenarios = null;
//		try {
//			authorizeUser.authorizeUserForTokenString();
//			updateDataSource();
//			testScenarios = testScenariosService
//					.getTestScenariosById(testScenariosId);
//			authorizeUser.authorizeUserForProjectId(
//					testScenarios.getClientProjectId());
//		} catch (APIExceptions e) {
//			log.info(e.getMessage());
//			return new ResponseEntity<String>(e.getMessage(),
//					HttpStatus.UNAUTHORIZED);
//		}
//		updateDataSource();
//		testScenariosService.deleteTestScenariosById(testScenariosId);
//		return new ResponseEntity<Void>(HttpStatus.OK);
//
//	}

	@Override
	@RequestMapping(value = "/private/testScenarios/getTestScenariosByFeatureName", method = {
			GET })
	public ResponseEntity<?> getTestScenariosByFeatureName(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("featureName") String featureName)
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
		return new ResponseEntity<List<TestScenarios>>(testScenariosService
				.getTestScenariosByFeatureName(clientProjectId, featureName),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testScenarios/getFeatureNames", method = {
			GET })
	public ResponseEntity<?> getFeatureFileList(
			@RequestParam("clientProjectId") int clientProjectId)
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
		return new ResponseEntity<List<?>>(
				testScenariosService.getFeatureFileList(clientProjectId),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testScenarios/getTestScenariosVersion", method = {
			GET })
	public ResponseEntity<?> getTestScenariosVersionByHashCode(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("testScenariosId") int testScenariosId)
			throws APIExceptions {
		updateDataSource();
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<TestScenariosVersion>>(
				testScenariosService.getTestScenariosVersionByScenarioId(
						clientProjectId, testScenariosId),
				HttpStatus.OK);
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
