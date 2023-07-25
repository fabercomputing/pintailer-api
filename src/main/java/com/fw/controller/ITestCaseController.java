package com.fw.controller;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.fw.domain.TestCase;
import com.fw.exceptions.APIExceptions;
import com.fw.exceptions.EmptyDataException;
import com.fw.exceptions.ImportDuplicateDataException;

public interface ITestCaseController {

	ResponseEntity<?> addTestCase(TestCase testCase) throws APIExceptions;

	ResponseEntity<?> getTestCasesClientBeans(int clientProjectId,
			String applicable, String searchTxt, String sortByColumn,
			String ascOrDesc, int limit, int pageNumber, String startDate,
			String endDate, String isDeleted) throws APIExceptions;

	ResponseEntity<?> updateTestCaseById(TestCase testCase, int releaseId,
			boolean isTestCaseDataUpdated) throws APIExceptions;

	ResponseEntity<?> importTestCase(MultipartFile uploadfile,
			int clientProjectId)
			throws APIExceptions, ImportDuplicateDataException,
			NullPointerException, EmptyDataException, Exception;

	ResponseEntity<?> importTestCaseNew(MultipartFile uploadfile,
									 int clientProjectId)
			throws APIExceptions, ImportDuplicateDataException,
			NullPointerException, EmptyDataException, Exception;

	ResponseEntity<?> removeTestCase(int testCaseId) throws APIExceptions;

	ResponseEntity<?> getTestCaseListByIds(int clientProjectId,
			String applicable, String testCaseIds, String searchTxt,
			String sortByColumn, String ascOrDesc, int limit, int pageNumber,
			String startDate, String endDate, String isDeleted)
			throws APIExceptions;

	ResponseEntity<?> getTestCasesCount(int clientProjectId, String applicable,
			String searchTxt) throws APIExceptions;

	ResponseEntity<?> getTags(int clientProjectId) throws APIExceptions;

	ResponseEntity<?> getTestCaseVersion(int testCaseId) throws APIExceptions;

	ResponseEntity<?> getTestCaseByModule(int clientProjectId, int releaseId,
			long moduleId, String applicable, String isDeleted)
			throws APIExceptions;
}
