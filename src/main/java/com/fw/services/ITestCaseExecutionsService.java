package com.fw.services;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.fw.domain.TestCaseExecutions;
import com.fw.exceptions.APIExceptions;

public interface ITestCaseExecutionsService {

	List<TestCaseExecutions> addTestCaseExecutions(TestCaseExecutions unit)
			throws APIExceptions;

	List<TestCaseExecutions> getTestCaseExecutions() throws APIExceptions;

	TestCaseExecutions getTestCaseExecutionsById(long executionId,
			int testCaseId, int releaseId, int environmentId)
			throws APIExceptions;

	List<TestCaseExecutions> importTestCaseExecutions(MultipartFile uploadfile,
			int clientProjectId, int environmentId, int releaseId,
			boolean isSync) throws APIExceptions, Exception;

	void importTestCaseExecutionsNew(MultipartFile uploadfile,
									 int clientProjectId, int environmentId, int releaseId,
									 boolean isSync) throws APIExceptions, Exception;

	List<TestCaseExecutions> getAllLatestTestCaseExecutions(int releaseId,
			int environmentId, int testCaseId, String testStepIds)
			throws APIExceptions;

	Map<Integer, List<TestCaseExecutions>> getAllLatestTestCaseExecutions(
			int releaseId, int environmentId, String testCaseIds)
			throws APIExceptions;
}
