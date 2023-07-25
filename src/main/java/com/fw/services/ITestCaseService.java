package com.fw.services;

import java.io.IOException;
/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.fw.domain.Modules;
import com.fw.domain.TestCase;
import com.fw.domain.TestCaseClientBean;
import com.fw.domain.TestCaseVersion;
import com.fw.exceptions.APIExceptions;

public interface ITestCaseService {

	TestCase addTestCase(TestCase testCase) throws APIExceptions;

	void updateTestCaseById(TestCase testCase, int releaseId,
			boolean isTestCaseDataUpdated) throws APIExceptions;

	TestCase getTestCaseById(int testCaseId, String isDeleted)
			throws APIExceptions;

	ResponseEntity<Void> deleteTestCaseById(int testCaseId)
			throws APIExceptions;

	List<TestCase> importTestCase(MultipartFile uploadfile, int clientProjectId)
			throws APIExceptions, NullPointerException, IOException;

	List<TestCase> importTestCaseNew(MultipartFile uploadfile, int clientProjectId)
			throws APIExceptions, NullPointerException, IOException;

	Modules createModuleHierarchy(List<String> moduleNameList,
			int clientProjectId) throws APIExceptions;

	List<TestCaseClientBean> getTestCaseList(int clientProjectId, int releaseId,
			String moduleIds, String tags, String applicable,
			String testCaseIds, String searchTxt, String sortByColumn,
			String ascOrDesc, int limit, int pageNumber, String startDate,
			String endDate, String isDeleted, boolean isAutomationInfoRequired)
			throws APIExceptions;

	List<TestCase> getAllTestCases(int clientProjectId, int releaseId,
			String moduleIds, String tags, String applicable,
			String testCaseIds, String searchTxt, String sortByColumn,
			String ascOrDesc, int limit, int pageNumber, String startDate,
			String endDate, String isDeleted) throws APIExceptions;

	int getTestCasesCount(int clientProjectId, String applicable,
			String searchTxt) throws APIExceptions;

	Set<String> getProjectSpecificTagList(int clientProjectId)
			throws APIExceptions;

	List<TestCase> getTestCaseBeanListByModuleId(long moduleId,
			String applicable, String isDeleted) throws APIExceptions;

	List<TestCaseVersion> getTestCaseVersion(int testCaseId)
			throws APIExceptions;

	TestCaseVersion getTestCaseVersion(int testCaseId, int versionId)
			throws APIExceptions;

	String getFinalModuleHierarchy(long moduleId) throws APIExceptions;

	List<TestCaseClientBean> getTestCaseByModuleId(int clientProjectId,
			int releaseId, long moduleId, String applicable, String isDeleted)
			throws APIExceptions;

	List<Integer> getDeletedTestCaseIdsForGivenTime(int clientProjectId,
			String startDate, String endDate) throws APIExceptions;
}
