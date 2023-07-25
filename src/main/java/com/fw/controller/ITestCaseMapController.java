package com.fw.controller;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import org.springframework.http.ResponseEntity;

import com.fw.domain.TestCaseMap;
import com.fw.exceptions.APIExceptions;

public interface ITestCaseMapController {

//	ResponseEntity<?> addTestCaseMap(int clientProjectId, int releaseId,
//			int testCaseId, String testStepIds, int testScenarioId)
//			throws APIExceptions;

	ResponseEntity<?> getTestCaseMap(int clientProjectID) throws APIExceptions;

	ResponseEntity<?> updateTestCaseMapById(TestCaseMap testCaseMap)
			throws APIExceptions;

	ResponseEntity<?> getTestCaseMapById(int testCaseMapId)
			throws APIExceptions;

	ResponseEntity<?> removeTestCaseMap(int testCaseMapId) throws APIExceptions;

//	ResponseEntity<?> getTestStepByTestCaseId(int testCaseId)
//			throws APIExceptions;

	ResponseEntity<?> deleteTestStepByTestCaseId(int clientProjectId,
			int releaseId, int testCaseId, int testScenarioID)
			throws APIExceptions;

	ResponseEntity<?> getTestCaseMapVersion(int testCaseId,
			int testCaseVersionId) throws APIExceptions;

	ResponseEntity<?> addTestCaseMap(int clientProjectId, int releaseId,
			int testCaseId, String testCaseVersionId,
			String selectedTestStepIds, int testScenarioId,
			int testScenarioStepVersionId) throws APIExceptions;

	ResponseEntity<?> getTestStepByTestCaseId(int clientProjectId,
			int releaseId, int testCaseId, String testCaseVersionId)
			throws APIExceptions;

	ResponseEntity<?> getTestCaseExistingMappedScenarioInfo(int clientProjectId,
			int releaseId, int testCaseId, String testCaseVersionId)
			throws APIExceptions;

	ResponseEntity<?> getTestCaseMapMaxVersionForSpecificPeriod(
			int clientProjectId, String startDate, String endDate)
			throws APIExceptions;
}
