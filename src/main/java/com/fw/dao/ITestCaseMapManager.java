package com.fw.dao;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.List;
import java.util.Map;

import com.fw.domain.TestCaseMap;
import com.fw.domain.TestCaseMapVersion;
import com.fw.exceptions.APIExceptions;

public interface ITestCaseMapManager {

	int persistTestCaseMap(int testCaseId, String testStepIds,
			int testScenarioId) throws APIExceptions;

	void updateTestCaseMapById(TestCaseMap logEntity) throws APIExceptions;

	TestCaseMap getTestCaseMapById(long testCaseMapId) throws APIExceptions;

	void deleteTestCaseMapById(long testCaseMapId) throws APIExceptions;

	List<TestCaseMap> getTestCaseMapByTestCaseId(int testCaseId)
			throws APIExceptions;

	void deleteTestCaseMapByTestCaseIdAndScenarioId(int testCaseId,
			int testScenarioId) throws APIExceptions;

	List<TestCaseMap> getTestCaseMappings(int clientProjectId)
			throws APIExceptions;

	List<TestCaseMap> getTestCaseMapByTestCaseIdAndTestStepId(int testCaseId,
			long testStepId, int clinetProjectId) throws APIExceptions;

	TestCaseMap getMapByCaseIdStepIdAndScenarioId(int testCaseId,
			long testStepId, int testScenarioId, int clientProjectId)
			throws APIExceptions;

	List<TestCaseMap> getInfo(int clinetProjectId, String stepHashCode,
			int testScenarioId, int testStepSequence) throws APIExceptions;

	void deleteTestCaseMapByScenarioId(int testScenarioId, long testStepId,
			int testCaseId) throws APIExceptions;

	Map<Integer, List<TestCaseMap>> getTestCaseMapByTestCaseIds(
			String testCaseIds) throws APIExceptions;

	List<TestCaseMapVersion> getTestCaseMapVersion(int testCaseId,
			int testCaseVersionId) throws APIExceptions;

	TestCaseMapVersion persistTestCaseMapVersion(
			TestCaseMapVersion testCaseMapVersion) throws APIExceptions;

	TestCaseMapVersion getTestCaseMapVersion(int testCaseMapVersionId)
			throws APIExceptions;

	List<TestCaseMapVersion> getTestCaseMapMaxVersionForSpecificPeriod(
			int clientProjectId, String startDate, String endDate)
			throws APIExceptions;
}
