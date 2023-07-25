package com.fw.controller;

import org.springframework.http.ResponseEntity;

import com.fw.exceptions.APIExceptions;

public interface IReportsController {

//	ResponseEntity<?> getAutomationReport(int clientProjectId, String tagValue,
//			long moduleId) throws APIExceptions;

	ResponseEntity<?> getExecutionReport(int clientProjectId, int moduleId,
			int releaseId, int environmentId) throws APIExceptions;

	ResponseEntity<String> downloadReport(int clientProjectId, int releaseId,
			int environmentId, String reportFileFormat, String reportFilePath)
			throws APIExceptions;

	ResponseEntity<String> downloadManualExecutionTemplate(
			String templateFormat, String fileName, String filePath,
			String testCaseIds) throws APIExceptions;

	ResponseEntity<String> downloadTestCases(String format, String fileName,
			String filePath, String testCaseIds) throws APIExceptions;

	ResponseEntity<?> getDashboardReport() throws APIExceptions;

	ResponseEntity<?> getTestCasesOfSpecificActivityStatus(int clientProjectId,
			int moduleId, String isApplicable, String isDeleted, String format,
			String filePath, String fileName) throws APIExceptions;

	ResponseEntity<?> getAutomationReport(int clientProjectId, String tagValue,
			long moduleId, String applicable, String startDate, String endDate)
			throws APIExceptions;

	ResponseEntity<?> getAutomationProgress(int clientProjectId,
			String applicable, String startDate, String endDate)
			throws APIExceptions;
}
