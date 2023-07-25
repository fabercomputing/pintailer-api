package com.fw.dao;

import java.util.List;

import com.fw.domain.ReleaseTestCaseBug;
import com.fw.exceptions.APIExceptions;

public interface IReleaseTestCaseBugManager {

	ReleaseTestCaseBug persistReleaseTestCaseBug(
			ReleaseTestCaseBug releaseTestCaseBug) throws APIExceptions;

	int updateReleaseTestCaseBugById(ReleaseTestCaseBug releaseTestCaseBug)
			throws APIExceptions;

	void deleteReleaseTestCaseBugById(long releaseTestCaseBugId)
			throws APIExceptions;

	ReleaseTestCaseBug getReleaseTestCaseBugById(int releaseTestCaseBugId,
			String applicable, String isDeleted) throws APIExceptions;

	List<ReleaseTestCaseBug> getReleaseTestCaseBug(int releaseId,
			int testCaseId, String testCaseVersionId, String bugId, String bugType,
			String applicable, String isDeleted, String createDateStart,
			String createDateEnd, String modifiedDateStart,
			String modifiedDateEnd) throws APIExceptions;

	List<ReleaseTestCaseBug> getReleaseTestCaseBugByTestCaseId(int testCaseId, String applicable, String isDeleted)
			throws APIExceptions;
}
