package com.fw.controller.impl;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fw.config.AuthorizeUser;
import com.fw.controller.IReleaseMapController;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.AddReleaseMapBean;
import com.fw.domain.ReleaseMap;
import com.fw.domain.ReleaseMapVersion;
import com.fw.domain.TestCaseClientBean;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IReleaseMapService;
import com.fw.utils.ApplicationCommonUtil;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class ReleaseMapControllerImpl implements IReleaseMapController {

	private static Logger log = Logger
			.getLogger(ReleaseMapControllerImpl.class);

	@Autowired
	IReleaseMapService releaseMapService;

	@Autowired
	AuthorizeUser authorizeUser;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/release/addReleaseMap", method = { POST })
	public ResponseEntity<?> addReleaseMap(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestBody ReleaseMap releaseMap) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<ReleaseMap>(releaseMapService
				.persistReleaseMap(clientProjectId, releaseMap), HttpStatus.OK);
	}

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/release/addReleaseMapBatch", method = {
			POST })
	public ResponseEntity<?> addReleaseMapBatch(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestBody AddReleaseMapBean releaseMap) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<Integer>(releaseMapService
				.persistReleaseMapBatch(clientProjectId, releaseMap),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/release/updateReleaseMap", method = {
			PATCH })
	public ResponseEntity<?> updateReleaseMap(
			@RequestBody ReleaseMap releaseMap) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<ReleaseMap>(
				releaseMapService.updateReleaseMap(releaseMap), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/release/getReleasesMap", method = { GET })
	public ResponseEntity<?> getReleasesMap(
			@RequestParam("releaseUniqueId") int releaseId,
			@RequestParam("projectId") int projectId,
			@RequestParam("moduleId") long moduleId,
			@RequestParam("searchTxt") String searchTxt,
			@RequestParam("sortByColumn") String sortByColumn,
			@RequestParam("ascOrDesc") String ascOrDesc,
			@RequestParam("limit") int limit,
			@RequestParam("pageNumber") int pageNumber,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(projectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<List<TestCaseClientBean>>(
				releaseMapService.getReleasesMap(releaseId, projectId, moduleId,
						searchTxt, sortByColumn, ascOrDesc, limit, pageNumber,
						startDate, endDate),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/release/deleteReleaseMap", method = {
			DELETE })
	public ResponseEntity<?> deleteReleaseMap(
			@RequestParam("releaseId") int releaseId) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<Integer>(
				releaseMapService.deleteReleaseMap(releaseId), HttpStatus.OK);

	}

	@Override
	@RequestMapping(value = "/private/release/deleteReleaseMapByReleaseUniqueId", method = {
			DELETE })
	public ResponseEntity<?> deleteReleaseMapByReleaseUniqueId(
			@RequestParam("clientProjectId") int clientProjectsId,
			@RequestParam("releasUniqueId") int releaseId,
			@RequestParam("moduleId") long moduleId) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectsId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<Integer>(
				releaseMapService.deleteReleaseMapByReleaseUniqueId(
						clientProjectsId, releaseId, moduleId),
				HttpStatus.OK);

	}

	@Override
	@RequestMapping(value = "/private/release/getReleasesMapVersion", method = {
			GET })
	public ResponseEntity<?> getReleasesMapVersion(
			@RequestParam("releaseId") int releaseId) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<List<ReleaseMapVersion>>(
				releaseMapService.getReleasesMapVersion(releaseId),
				HttpStatus.OK);
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
