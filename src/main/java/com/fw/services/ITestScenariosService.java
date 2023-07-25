package com.fw.services;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.util.List;

import com.fw.bean.FeatureFileNameBean;
import com.fw.domain.TestScenarios;
import com.fw.domain.TestScenariosVersion;
import com.fw.exceptions.APIExceptions;

public interface ITestScenariosService {

	TestScenarios addTestScenarios(TestScenarios testScenarios)
			throws APIExceptions;

	void updateTestScenariosById(TestScenarios testScenarios)
			throws APIExceptions;

	TestScenarios getTestScenariosById(int testScenarioId, String isDeleted)
			throws APIExceptions;

	List<TestScenarios> getTestScenariosByFeatureName(int clientProjectId,
			String featureName) throws APIExceptions;

	List<FeatureFileNameBean> getFeatureFileList(int clientProjectId)
			throws APIExceptions;

//	ResponseEntity<Void> deleteTestScenariosById(int testScenariosId)
//			throws APIExceptions;

	List<TestScenariosVersion> getTestScenariosVersionByHashCode(
			int clientProjectId, String featureFileName, String scenarioName)
			throws APIExceptions;

	List<TestScenariosVersion> getTestScenariosVersionByScenarioId(
			int clientProjectId, int testScenariosId) throws APIExceptions;

	void saveScenarioStepVersionInfo(int testScenariosId,
			String scenarioHashCode, String scenarioSelectedVersion,
			String stepInfo) throws APIExceptions;

	TestScenarios getTestScenariosByScenarioIdAndVersionId(int clientProjectId,
			int testScenariosId, String testScenariosVersionId)
			throws APIExceptions;
}
