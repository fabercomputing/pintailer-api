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
import com.fw.controller.ITestScenarioStepController;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.TestScenarioStep;
import com.fw.domain.TestScenarioStepVersion;
import com.fw.exceptions.APIExceptions;
import com.fw.services.ITestScenarioStepService;
import com.fw.utils.ApplicationCommonUtil;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class TestScenarioStepControllerImpl
		implements ITestScenarioStepController {

	private static Logger log = Logger
			.getLogger(TestScenarioStepControllerImpl.class);

	@Autowired
	ITestScenarioStepService testScenarioStepService;

	@Autowired
	AuthorizeUser authorizeUser;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/testScenarioStep/addTestScenarioStep", method = {
			POST })
	public ResponseEntity<?> addTestScenarioStep(
			@RequestBody TestScenarioStep testScenarioStep)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<TestScenarioStep>(
				testScenarioStepService.addTestScenarioStep(testScenarioStep),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testScenarioStep/updateTestScenarioStep", method = {
			PATCH })
	public ResponseEntity<?> updateTestScenarioStepById(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestBody TestScenarioStep testScenarioStep)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		try {
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}

		testScenarioStepService.updateTestScenarioStepById(clientProjectId,
				testScenarioStep);
		return new ResponseEntity<TestScenarioStep>(testScenarioStep,
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testScenarioStep/getTestScenarioStepByScenarioId", method = {
			GET })
	public ResponseEntity<?> getTestScenarioStepById(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("testScenarioStepId") long testScenarioStepId)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<TestScenarioStep>(testScenarioStepService
				.getTestScenarioStepById(clientProjectId, testScenarioStepId),
				HttpStatus.OK);
	}

//	@Override
//	@RequestMapping(value = "/private/testScenarioStep/deleteTestScenarioStep", method = {
//			DELETE })
//	public ResponseEntity<?> removeTestScenarioStep(
//			@RequestParam("testScenarioStepId") long testScenarioStepId)
//			throws APIExceptions {
//		try {
//			authorizeUser.authorizeUserForTokenString();
//		} catch (APIExceptions e) {
//			log.info(e.getMessage());
//			return new ResponseEntity<String>(e.getMessage(),
//					HttpStatus.UNAUTHORIZED);
//		}
//		updateDataSource();
//		testScenarioStepService.deleteTestScenarioStepById(testScenarioStepId);
//		return new ResponseEntity<Void>(HttpStatus.OK);
//	}

	@Override
	@RequestMapping(value = "/private/testScenarioStep/getTestScenarioStep", method = {
			GET })
	public ResponseEntity<?> getTestScenarioStep(
			@RequestParam("clientProjectId") int clientProjectId)
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
		return new ResponseEntity<List<TestScenarioStep>>(
				testScenarioStepService.getTestScenarioStep(clientProjectId),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testScenarioStep/getScenarioStepMappingVersion", method = {
			GET })
	public ResponseEntity<?> getScenarioStepMappingVersion(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("featureFileName") String featureFileName,
			@RequestParam("scenarioName") String scenarioName,
			@RequestParam("testScenariosVersionId") int testScenariosVersionId)
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
		return new ResponseEntity<List<TestScenarioStepVersion>>(
				testScenarioStepService.getScenarioStepMappingVersion(
						clientProjectId, featureFileName, scenarioName,
						testScenariosVersionId),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testScenarioStep/getScenarioStepMappingVersionByScenarioId", method = {
			GET })
	public ResponseEntity<?> getScenarioStepMappingVersionByScenarioId(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("testScenarioId") int testScenarioId)
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
		return new ResponseEntity<List<TestScenarioStepVersion>>(
				testScenarioStepService
						.getScenarioStepMappingVersionByScenarioId(
								clientProjectId, testScenarioId),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testScenarioStep/getScenarioStepMappingById", method = {
			GET })
	public ResponseEntity<?> getScenarioStepMappingById(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("testScenarioStepVersionId") int testScenarioStepVersionId)
			throws APIExceptions {
		updateDataSource();
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<TestScenarioStepVersion>(
				testScenarioStepService.getScenarioStepMappingById(
						clientProjectId, testScenarioStepVersionId),
				HttpStatus.OK);
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}

}
