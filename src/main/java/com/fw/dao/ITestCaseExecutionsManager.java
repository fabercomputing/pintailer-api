package com.fw.dao;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.List;
import java.util.Map;

import com.fw.domain.TestCaseExecutions;
import com.fw.exceptions.APIExceptions;

public interface ITestCaseExecutionsManager {

	TestCaseExecutions persistTestCaseExecutions(
			TestCaseExecutions testCaseExecutions) throws APIExceptions;

	int persistTestCaseExecutionsInBatch(final List<Object[]> args)
			throws APIExceptions;

	List<TestCaseExecutions> getAllTestCaseExecutions() throws APIExceptions;

	TestCaseExecutions getTestCaseExecutionsById(long executionId,
			int testCaseId, int releaseId, int environmentId)
			throws APIExceptions;

	List<TestCaseExecutions> getAllLatestTestCaseExecutions(int releaseId,
			int environmentId, int testCaseId, String testStepIds)
			throws APIExceptions;

	Map<Integer, List<TestCaseExecutions>> getAllLatestTestCaseExecutions(
			int releaseId, int environmentId, String testCaseIds)
			throws APIExceptions;
}
