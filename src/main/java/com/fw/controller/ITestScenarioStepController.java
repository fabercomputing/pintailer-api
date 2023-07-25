package com.fw.controller;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import org.springframework.http.ResponseEntity;

import com.fw.domain.TestScenarioStep;
import com.fw.exceptions.APIExceptions;

public interface ITestScenarioStepController {

	ResponseEntity<?> addTestScenarioStep(TestScenarioStep testScenarioStep)
			throws APIExceptions;

	ResponseEntity<?> getTestScenarioStep(int clientProjectId)
			throws APIExceptions;

	ResponseEntity<?> updateTestScenarioStepById(int clientProjectId,
			TestScenarioStep testScenarioStep) throws APIExceptions;

	ResponseEntity<?> getTestScenarioStepById(int clientProjectId,
			long testScenarioStepId) throws APIExceptions;

//	ResponseEntity<?> removeTestScenarioStep(long testScenarioStepId)
//			throws APIExceptions;

	ResponseEntity<?> getScenarioStepMappingVersion(int clientProjectId,
			String featureFileName, String scenarioName,
			int testScenariosVersionId) throws APIExceptions;

	ResponseEntity<?> getScenarioStepMappingVersionByScenarioId(
			int clientProjectId, int testScenarioId) throws APIExceptions;

	ResponseEntity<?> getScenarioStepMappingById(int clientProjectId,
			int testScenarioStepVersionId) throws APIExceptions;
}
