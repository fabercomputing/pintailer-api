package com.fw.dao;

import java.util.List;

import com.fw.domain.ReleaseTestCaseMapping;
import com.fw.exceptions.APIExceptions;

public interface IReleaseTestCaseMappingManager {
	ReleaseTestCaseMapping persistReleaseTestCaseMapping(
			ReleaseTestCaseMapping releaseTestCaseMapping) throws APIExceptions;

	int persistReleaseTestCaseMappingInBatch(List<Object[]> args)
			throws APIExceptions;

	int deleteReleaseMap(int clientProjectId, int releaseId,
			String testCaseMapIds) throws APIExceptions;

	List<ReleaseTestCaseMapping> getReleaseTestCaseMapping(int clientProjectId,
			int releaseId) throws APIExceptions;

	List<ReleaseTestCaseMapping> getReleaseTestCaseMapping(int clientProjectId,
			int releaseId, int testCaseId, int testCaseVersionId)
			throws APIExceptions;
}
