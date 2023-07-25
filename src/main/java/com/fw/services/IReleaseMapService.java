package com.fw.services;

import java.util.List;

import com.fw.domain.AddReleaseMapBean;
import com.fw.domain.ReleaseMap;
import com.fw.domain.ReleaseMapVersion;
import com.fw.domain.TestCaseClientBean;
import com.fw.exceptions.APIExceptions;

public interface IReleaseMapService {

	ReleaseMap persistReleaseMap(int clientProjectId, ReleaseMap releaseMap)
			throws APIExceptions;

	Integer persistReleaseMapBatch(int clientProjectId,
			AddReleaseMapBean addReleaseMapBean) throws APIExceptions;

	ReleaseMap updateReleaseMap(ReleaseMap releaseMap) throws APIExceptions;

	int deleteReleaseMap(int releaseMapId) throws APIExceptions;

	int deleteReleaseMapByReleaseUniqueId(int clientProjectId, int releaseId,
			long moduleId) throws APIExceptions;

	List<ReleaseMapVersion> getReleasesMapVersion(int releaseId)
			throws APIExceptions;

	List<TestCaseClientBean> getReleasesMap(int releaseId, int clientProjectId,
			long moduleId, String searchTxt, String sortByColumn,
			String ascOrDesc, int limit, int pageNumber, String startDate,
			String endDate) throws APIExceptions;

	List<TestCaseClientBean> updateTestCaseCompulsoryExecutionFlag(
			int releaseId, List<TestCaseClientBean> testCasesList)
			throws APIExceptions;
}
