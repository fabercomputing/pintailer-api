package com.fw.dao;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.List;
import java.util.Map;

import com.fw.domain.TestCase;
import com.fw.domain.TestCaseVersion;
import com.fw.exceptions.APIExceptions;

public interface ITestCaseManager {

	TestCase persistTestCase(TestCase testCase) throws APIExceptions;

	int updateTestCaseById(TestCase testCase) throws APIExceptions;

	List<TestCase> getAllTestCases(int clientProjectId, int releaseId,
			String moduleIds, String tagValue, String applicable,
			String testCaseIds, String searchTxt, String sortByColumn,
			String ascOrDesc, int limit, int pageNumber, String startDate,
			String endDate, String isDeleted) throws APIExceptions;

	TestCase getTestCaseById(int testCaseId, String isDeleted)
			throws APIExceptions;

	void deleteTestCaseById(int testCaseId) throws APIExceptions;

	List<TestCase> getTestCaseByModuleId(long moduleId) throws APIExceptions;

	Map<String, Integer> getTestCaseHashCode(boolean isAllRequired,
			boolean isDeleted, boolean applicable) throws APIExceptions;

	int getTestCasesCount(int clientProjectId, String applicable,
			String searchTxt) throws APIExceptions;

	TestCase getTestCaseByHash(String hash, boolean isAllRecordsRequired,
			boolean isDeleted) throws APIExceptions;

	List<String> getProjectTestCaseTagsDetails(int clientProjectId)
			throws APIExceptions;

	List<TestCase> getAllTestCases() throws APIExceptions;

	List<TestCaseVersion> getTestCaseVersion(int testCaseId)
			throws APIExceptions;

	List<Integer> getAllTestCasesIds(int clientProjectId, int releaseId,
			String moduleIds, String tagValue, String applicable,
			String testCaseIds, String searchTxt, String sortByColumn,
			String ascOrDesc, int limit, int pageNumber, String startDate,
			String endDate, String isDeleted) throws APIExceptions;

	Map<Integer, List<TestCaseVersion>> getMultipleTestCasesVersionMap(
			String testCaseIds) throws APIExceptions;

	TestCaseVersion getTestCaseVersion(int testCaseId, int versionId)
			throws APIExceptions;

	List<Integer> getDeletedTestCaseIdsForGivenTime(int clientProjectId,
			String startDate, String endDate) throws APIExceptions;
}
