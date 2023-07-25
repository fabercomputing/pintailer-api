package com.fw.services;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.util.List;

import com.fw.domain.TestScenarioStep;
import com.fw.domain.TestScenarioStepVersion;
import com.fw.exceptions.APIExceptions;

public interface ITestScenarioStepService {

	TestScenarioStep addTestScenarioStep(TestScenarioStep unit)
			throws APIExceptions;

	List<TestScenarioStep> getTestScenarioStep(int clientProjectId)
			throws APIExceptions;

	void updateTestScenarioStepById(int clientProjectId, TestScenarioStep unit)
			throws APIExceptions;

	TestScenarioStep getTestScenarioStepById(int clientProjectId, long unit)
			throws APIExceptions;

//	ResponseEntity<Void> deleteTestScenarioStepById(long id)
//			throws APIExceptions;

	List<TestScenarioStepVersion> getScenarioStepMappingVersion(
			int clientProjectId, String featureFileName, String scenarioName,
			int testScenariosVersionId) throws APIExceptions;

	List<TestScenarioStepVersion> getScenarioStepMappingVersionByScenarioId(
			int clientProjectId, int testScenarioId) throws APIExceptions;

	TestScenarioStepVersion getScenarioStepMappingById(int clientProjectId,
			int testScenarioStepVersionId) throws APIExceptions;;
}
