package com.fw.dao;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.List;
import java.util.Map;

import com.fw.domain.TestScenarios;
import com.fw.domain.TestScenariosVersion;
import com.fw.exceptions.APIExceptions;

public interface ITestScenariosManager {

	TestScenarios persistTestScenarios(TestScenarios testScenarios)
			throws APIExceptions;

	int updateTestScenariosById(TestScenarios testScenarios)
			throws APIExceptions;

	TestScenarios getTestScenariosById(int testScenariosId, String isDeleted)
			throws APIExceptions;

	List<TestScenarios> getTestScenariosByFeatureName(int clientProjectId,
			String featureName) throws APIExceptions;

	List<String> getFeatureFileList(int clientProjectId) throws APIExceptions;

//	void deleteTestScenariosById(int testScenariosId) throws APIExceptions;

	int getTestScenariosIdByHashCode(int clientProjectId, String hashCode)
			throws APIExceptions;

	int getTestScenarioIdByScenarioAndFeatureFile(int clientProjectId,
			String scenarioName, String featureFileName) throws APIExceptions;

	Map<String, Integer> getTestScenarioHashCode(int clientProjectId)
			throws APIExceptions;

	Map<String, Integer> getTestScenarioHashCode(int clientProjectId,
			String featureFileName) throws APIExceptions;

	List<TestScenarios> getEntireFeature(int clientProjectId,
			String featureName) throws APIExceptions;

	void updateTestScenariosSequenceById(TestScenarios testScenarios)
			throws APIExceptions;

	List<TestScenarios> getAllTestScenarios() throws APIExceptions;

	List<TestScenariosVersion> getTestScenariosVersionByHashCode(
			int clientProjectId, String featureFileName, String scenarioName)
			throws APIExceptions;

	TestScenarios getTestScenariosByHashCode(String testScenariosHashCode)
			throws APIExceptions;

	TestScenarios getSpecificTestScenariosVersion(int clientProjectId,
			String scenarioHashCode, int version_id) throws APIExceptions;

	List<TestScenariosVersion> getTestScenariosVersionByScenarioId(
			int clientProjectId, int testScenariosId) throws APIExceptions;

	TestScenarios getSpecificTestScenariosVersion(int clientProjectId,
			int testScenarioId, int testScenarioVersionId) throws APIExceptions;
}
