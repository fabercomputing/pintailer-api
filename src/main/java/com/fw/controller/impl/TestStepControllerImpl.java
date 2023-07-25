package com.fw.controller.impl;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fw.config.AuthorizeUser;
import com.fw.controller.ITestStepController;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.TestScenarios;
import com.fw.domain.TestStep;
import com.fw.domain.TestStepVersion;
import com.fw.exceptions.APIExceptions;
import com.fw.services.ITestStepService;
import com.fw.utils.ApplicationCommonUtil;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class TestStepControllerImpl implements ITestStepController {

	private static Logger log = Logger.getLogger(TestStepControllerImpl.class);

	@Autowired
	ITestStepService testStepService;

	@Autowired
	AuthorizeUser authorizeUser;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/testStep/addTestStep", method = { POST })
	public ResponseEntity<?> addTestStep(@RequestBody TestStep testStep)
			throws APIExceptions {
		updateDataSource();
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser
					.authorizeUserForProjectId(testStep.getClientProjectId());
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		TestStep testStepdata = null;
		testStepdata = testStepService.addTestStep(testStep);
		return new ResponseEntity<TestStep>(testStepdata, HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testStep/updateTestStep", method = {
			PATCH })
	public ResponseEntity<?> updateTestStepById(@RequestBody TestStep testStep)
			throws APIExceptions {
		updateDataSource();
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser
					.authorizeUserForProjectId(testStep.getClientProjectId());
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		testStepService.updateTestStepById(testStep);
		return new ResponseEntity<TestStep>(testStep, HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testStep/deleteTestStep", method = {
			DELETE })
	public ResponseEntity<?> deleteTestStep(
			@PathVariable("testStepId") long testStepId,
			@RequestParam("clientProjectId") int clientProjectId)
			throws APIExceptions {
		updateDataSource();
//		TestStep testStep = null;
		try {
			authorizeUser.authorizeUserForTokenString();
//			testStep = testStepService.getTestStepById(testStepId,
//					clientProjectId);
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		testStepService.deleteTestStepById(testStepId);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testStep/getTestStep", method = { GET })
	public ResponseEntity<?> getTestStep(
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
		return new ResponseEntity<List<TestStep>>(
				testStepService.getTestStep(clientProjectId), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testStep/getTestStepVersion", method = {
			GET })
	public ResponseEntity<?> getTestStepVersion(
			@RequestParam("testStepId") long testStepId) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<TestStepVersion>>(
				testStepService.getTestStepsVersionByStepId(testStepId),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testStep/getTestStepByScenario", method = {
			POST })
	public ResponseEntity<?> getTestStepByScenario(
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
		return new ResponseEntity<Map<Integer, TestScenarios>>(
				testStepService.getTestStepByScenarios(testScenarios),
				HttpStatus.OK);
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
