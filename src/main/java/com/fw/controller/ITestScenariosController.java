package com.fw.controller;

import com.fw.domain.TestScenarios;
import com.fw.exceptions.APIExceptions;
/**
 * 
 * @author Sumit Srivastava
 *
 */
import org.springframework.http.ResponseEntity;

public interface ITestScenariosController {

	ResponseEntity<?> addTestScenarios(TestScenarios testscScenarios)
			throws APIExceptions;

	ResponseEntity<?> updateTestScenariosById(TestScenarios testscScenarios)
			throws APIExceptions;

//	ResponseEntity<?> getTestScenariosById(int testScenarioId)
//			throws APIExceptions;

	ResponseEntity<?> getTestScenariosByFeatureName(int clientProjectId,
			String featureName) throws APIExceptions;

	ResponseEntity<?> getFeatureFileList(int clientProjectId)
			throws APIExceptions;

//	ResponseEntity<?> removeTestScenarios(int testScenariosId)
//			throws APIExceptions;

	ResponseEntity<?> getTestScenariosVersionByHashCode(int clientProjectId,
			int testScenariosId) throws APIExceptions;

	ResponseEntity<?> getTestScenariosById(int clientProjectId,
			int testScenariosId, String testScenariosVersionId)
			throws APIExceptions;
}
