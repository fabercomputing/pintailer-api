package com.fw.controller;

import org.springframework.http.ResponseEntity;

import com.fw.domain.ReleaseTestCaseBug;
import com.fw.exceptions.APIExceptions;

public interface IReleaseTestCaseBugController {

	ResponseEntity<?> addReleaseTestCaseBug(int clientProjectId,
			ReleaseTestCaseBug releaseTestCaseBug) throws APIExceptions;

	ResponseEntity<?> updateReleaseTestCaseBugById(
			ReleaseTestCaseBug releaseTestCaseBug) throws APIExceptions;

	ResponseEntity<?> deleteReleaseTestCaseBugById(long releaseTestCaseBugId)
			throws APIExceptions;

	ResponseEntity<?> getReleaseTestCaseBugById(int releaseTestCaseBugId,
			String applicable, String isDeleted) throws APIExceptions;

	ResponseEntity<?> getReleaseTestCaseBug(int releaseId, int testCaseId,
			String testCaseVersionId, String bugId, String bugType,
			String applicable, String isDeleted, String createDateStart,
			String createDateEnd, String modifiedDateStart,
			String modifiedDateEnd) throws APIExceptions;

	ResponseEntity<?> getReleaseTestCaseBugByTestCaseId(int testCaseId, String applicable, String isDeleted)
			throws APIExceptions;
}
