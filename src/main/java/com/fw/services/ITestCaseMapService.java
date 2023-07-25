package com.fw.services;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.fw.domain.TestCaseMap;
import com.fw.domain.TestCaseMapVersion;
import com.fw.exceptions.APIExceptions;

public interface ITestCaseMapService {

//	int addTestCaseMap(int clientProjectId, int releaseId, int testCaseId,
//			String testStepIds, int testScenarioId) throws APIExceptions;

	List<TestCaseMap> getTestCaseMap(int clientProjectId) throws APIExceptions;

	void updateTestCaseMapById(TestCaseMap testCaseMap) throws APIExceptions;

	TestCaseMap getTestCaseMapById(long testCaseMapId) throws APIExceptions;

	ResponseEntity<Void> deleteTestCaseMap(long testCaseMapId)
			throws APIExceptions;

//	List<TestCaseMap> getTestStepByTestCaseId(int testCaseId)
//			throws APIExceptions;

	ResponseEntity<Void> deleteTestStepByTestCaseId(int clientProjectId,
			int releaseId, int testCaseId, int testScenarioID)
			throws APIExceptions;

	void automaticMap(ArrayList<Object> data, int clientProjectId)
			throws APIExceptions;

	Map<Integer, List<TestCaseMap>> getTestStepByTestCaseIds(String testCaseIds)
			throws APIExceptions;

	List<TestCaseMapVersion> getTestCaseMapVersion(int testCaseId,
			int testCaseVersionId) throws APIExceptions;

	int addTestCaseMap(int clientProjectId, int releaseId, int testCaseId,
			String testCaseVersionId, String selectedTestStepIds,
			int testScenarioId, int testScenarioStepVersionId)
			throws APIExceptions;

//	List<TestCaseMap> getTestStepByTestCaseId(int clientProjectId,
//			int releaseId, int testCaseId, String testCaseVersionId)
//			throws APIExceptions;

	TestCaseMapVersion getTestCaseExistingMappedScenarioInfo(
			int clientProjectId, int releaseId, int testCaseId,
			String testCaseVersionId) throws APIExceptions;

	List<TestCaseMapVersion> getTestCaseMapMaxVersionForSpecificPeriod(
			int clientProjectId, String startDate, String endDate)
			throws APIExceptions;

	List<TestCaseMap> getTestStepByTestCaseId(int clientProjectId,
			int releaseId, int testCaseId, String testCaseVersionId,
			boolean isReleaseTCMapMapping) throws APIExceptions;
}
