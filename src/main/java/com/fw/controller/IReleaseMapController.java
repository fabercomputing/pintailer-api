package com.fw.controller;

import org.springframework.http.ResponseEntity;

import com.fw.domain.AddReleaseMapBean;
import com.fw.domain.ReleaseMap;
import com.fw.exceptions.APIExceptions;

public interface IReleaseMapController {

	ResponseEntity<?> addReleaseMap(int clientProjectId, ReleaseMap releaseMap)
			throws APIExceptions;

	ResponseEntity<?> addReleaseMapBatch(int clientProjectId,
			AddReleaseMapBean releaseMap) throws APIExceptions;

	ResponseEntity<?> updateReleaseMap(ReleaseMap releaseMap)
			throws APIExceptions;

	ResponseEntity<?> deleteReleaseMap(int releaseMapId) throws APIExceptions;

	ResponseEntity<?> deleteReleaseMapByReleaseUniqueId(int clientProjectId,
			int releaseId, long moduleId) throws APIExceptions;

	ResponseEntity<?> getReleasesMapVersion(int releaseId) throws APIExceptions;

	ResponseEntity<?> getReleasesMap(int releaseId, int projectId,
			long moduleId, String searchTxt, String sortByColumn,
			String ascOrDesc, int limit, int pageNumber, String startDate,
			String endDate) throws APIExceptions;
}
