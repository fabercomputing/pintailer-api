package com.fw.dao;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.List;

import com.fw.domain.FeatureVersion;
import com.fw.domain.TestScenarioStep;
import com.fw.domain.TestScenarioStepVersion;
import com.fw.exceptions.APIExceptions;

public interface ITestScenarioStepManager {

	TestScenarioStep persistTestScenarioStep(TestScenarioStep testScenarioStep)
			throws APIExceptions;

	void updateTestScenarioStepById(TestScenarioStep testScenarioStep)
			throws APIExceptions;

	List<TestScenarioStep> getAllTestScenarioStep(int clientProjectId)
			throws APIExceptions;

	TestScenarioStep getTestScenarioStepById(int clientProjectId,
			long testStepId) throws APIExceptions;

	void deleteTestScenarioStepById(long testScenarioStepId)
			throws APIExceptions;

	List<TestScenarioStep> getTestStepIdByScenarioId(int clientProjectId,
			int scenarioId) throws APIExceptions;

	void deleteTestScenarioStepByScenarioId(int scenarioId)
			throws APIExceptions;

	void updateTestScenarioKeywordByStepStepId(long stepId, String keyword)
			throws APIExceptions;

	List<TestScenarioStepVersion> getScenarioStepMappingVersion(
			int clientProjectId, String featureFileName, String scenarioName,
			int testScenariosVersionId) throws APIExceptions;

	TestScenarioStep isDataExist(int clientProjectId, int scenarioId,
			long stepId) throws APIExceptions;

	TestScenarioStep getDataIfExist(int clientProjectId, int scenarioId,
			long stepId, int sequence) throws APIExceptions;

	List<TestScenarioStep> getDataIfExist(int clientProjectId, int scenarioId,
			long stepId) throws APIExceptions;

	List<TestScenarioStep> getDataIfExist(int clientProjectId, int scenarioId,
			long stepId, int sequence, String condition) throws APIExceptions;

	TestScenarioStepVersion persistTestScenarioStepVersion(
			TestScenarioStepVersion testScenarioStepVersion)
			throws APIExceptions;

	TestScenarioStepVersion isVersionDataExist(String scenarioHashCode,
			int testScenariosVersionId, String testStepInfo)
			throws APIExceptions;

	List<TestScenarioStepVersion> getScenarioStepMappingVersion(
			String scenarioHashCode, int testScenariosVersionId)
			throws APIExceptions;

	FeatureVersion persistFeatureVersion(FeatureVersion featureVersion)
			throws APIExceptions;

	List<FeatureVersion> getFeatureVersion(int clientProjectId,
			String featureFileName) throws APIExceptions;

	TestScenarioStepVersion getScenarioStepMappingVersion(
			String scenarioHashCode, int testScenariosVersionId,
			int testStepScenarioVersionId) throws APIExceptions;

	List<TestScenarioStepVersion> getScenarioStepVersionData(
			String scenarioHashCode, int testScenariosVersionId)
			throws APIExceptions;

	List<TestScenarioStepVersion> getScenarioStepMappingVersion(int testScenariosId,
			int testScenariosVersionId) throws APIExceptions;

	TestScenarioStepVersion getScenarioStepMappingVersion(
			int testScenarioStepVersionId) throws APIExceptions;

	List<TestScenarioStepVersion> getAllScenarioStepMappingVersion(
			int clientProjectId) throws APIExceptions;
}
