package com.fw.controller;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.fw.domain.TestCaseExecutions;
import com.fw.exceptions.APIExceptions;
import com.fw.exceptions.EmptyDataException;
import com.fw.exceptions.ImportDuplicateDataException;

public interface ITestCaseExecutionsController {

	ResponseEntity<?> addTestCaseExecutions(
			TestCaseExecutions testCaseExecutions) throws APIExceptions;

	ResponseEntity<?> getTestCaseExecutions() throws APIExceptions;

	ResponseEntity<?> getTestCaseExecutionsById(long executionId,
			int testCaseId, int releaseId, int environmentId)
			throws APIExceptions;

	ResponseEntity<?> importTestCase(MultipartFile uploadfile,
			int clientProjectId, int environmentId, int releaseId,
			boolean isSync) throws APIExceptions, ImportDuplicateDataException,
			NullPointerException, EmptyDataException, Exception;

	ResponseEntity<?> importTestExecution(MultipartFile uploadfile,
									 int clientProjectId, int environmentId, int releaseId,
									 boolean isSync) throws APIExceptions, ImportDuplicateDataException,
			NullPointerException, EmptyDataException, Exception;
}
