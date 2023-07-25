package com.fw.dao;

import java.util.List;
import java.util.Map;

import com.fw.domain.ReleaseMap;
import com.fw.domain.ReleaseMapVersion;
import com.fw.exceptions.APIExceptions;

public interface IReleaseMapManager {
	ReleaseMap persistReleaseMap(ReleaseMap releaseMap) throws APIExceptions;

	int updateReleaseMap(ReleaseMap releaseMap) throws APIExceptions;

	int deleteReleaseMap(int releaseMapId) throws APIExceptions;

	int deleteReleaseMapByReleaseUniqueId(int releaseId) throws APIExceptions;

	int persistReleaseMapInBatch(List<Object[]> args) throws APIExceptions;

	int deleteReleaseMapByReleaseUniqueIdAndTestCaseId(int clientProjectId,
			int releaseId, String moduleIds) throws APIExceptions;

	boolean persistReleaseMapVersion(ReleaseMapVersion releaseMapVersion)
			throws APIExceptions;

	List<ReleaseMapVersion> getReleasesMapVersion(int releaseId)
			throws APIExceptions;

	Map<Integer, String> getReleasesMapSelectedVersion(int releaseId,
			int clientProjectId) throws APIExceptions;
}
