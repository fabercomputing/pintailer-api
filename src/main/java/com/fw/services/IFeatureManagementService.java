package com.fw.services;

import java.util.List;
import java.util.Map;

import com.fw.domain.TestScenarios;
import com.fw.exceptions.APIExceptions;

public interface IFeatureManagementService {
	List<TestScenarios> getEntireFeature(int clientProjectId,
			String featureName) throws APIExceptions;

	String downloadFeatureFile(int clientProjectId, String featureName,
			String reportFilePath) throws APIExceptions;

	boolean updateFeature(int clientProjectId,
			List<TestScenarios> testScenariosList) throws APIExceptions;

	Map<String, List<TestScenarios>> getFeatureFileVersions(int clientProjectId,
			String featureFileName) throws APIExceptions;

	void saveFeatureVersionInfo(int clientProjectId, String featureFileName,
			String scenarioInfo) throws APIExceptions;
}
