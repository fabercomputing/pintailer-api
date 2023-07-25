package com.fw.dao;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.List;
import java.util.Map;

import com.fw.domain.TestScenarioStep;
import com.fw.domain.TestStep;
import com.fw.domain.TestStepVersion;
import com.fw.exceptions.APIExceptions;

public interface ITestStepManager {

	TestStep persistTestStep(TestStep logEntity) throws APIExceptions;

	int updateTestStepById(TestStep logEntity) throws APIExceptions;

	List<TestStep> getAllTestStepRowMapper(int clientProjectId)
			throws APIExceptions;

	TestStep getTestStepById(long Id, int clientProjectId) throws APIExceptions;

	void deleteTestStepById(long Id) throws APIExceptions;

	TestStep getTestStepIdByHashCode(String hashCode) throws APIExceptions;

	Map<String, Long> getTestStepHashCode() throws APIExceptions;

	List<TestStep> getTestStepsByScenarioSteps(
			List<TestScenarioStep> testScenarioSteps) throws APIExceptions;

	void updateTestStepById(TestStep logEntity, boolean applicableFlg)
			throws APIExceptions;

	List<TestStep> getTestStepsByScenarioId(long testScenarioId)
			throws APIExceptions;

	List<TestStep> getTestStepsByIds(String testStepIds) throws APIExceptions;

	int getTestStepProjectId(long testStepId) throws APIExceptions;

	List<TestStepVersion> getTestStepsVersionByStepId(long stepId)
			throws APIExceptions;

	TestStepVersion getTestStepsVersionByStepIdAndVersionId(long stepId,
			int versionId) throws APIExceptions;
}
