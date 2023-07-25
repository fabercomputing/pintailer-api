package com.fw.controller;

import com.fw.domain.TestScenarios;
import com.fw.domain.TestStep;
import com.fw.exceptions.APIExceptions;
/**
 * 
 * @author Sumit Srivastava
 *
 */
import org.springframework.http.ResponseEntity;

public interface ITestStepController {

	ResponseEntity<?> addTestStep(TestStep testStep) throws APIExceptions;

	ResponseEntity<?> getTestStep(int clientProjectId) throws APIExceptions;

	ResponseEntity<?> updateTestStepById(TestStep testStep)
			throws APIExceptions;

	ResponseEntity<?> getTestStepByScenario(TestScenarios testScenarios)
			throws APIExceptions;

	ResponseEntity<?> getTestStepVersion(long testStepId) throws APIExceptions;

	ResponseEntity<?> deleteTestStep(long testStepId, int clientProjectId)
			throws APIExceptions;
}
