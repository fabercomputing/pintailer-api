package com.fw.controller.impl;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
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
import com.fw.controller.IReleaseTestCaseBugController;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.ReleaseTestCaseBug;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IReleaseTestCaseBugService;
import com.fw.utils.ApplicationCommonUtil;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class ReleaseTestCaseBugControllerImpl
		implements IReleaseTestCaseBugController {

	private Logger log = Logger
			.getLogger(ReleaseTestCaseBugControllerImpl.class);

	@Autowired
	AuthorizeUser authorizeUser;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Autowired
	IReleaseTestCaseBugService releaseTestCaseBugService;

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/releaseTestCaseBug/addReleaseTestCaseBug", method = {
			POST })
	public ResponseEntity<?> addReleaseTestCaseBug(
			@RequestParam int clientProjectId,
			@RequestBody ReleaseTestCaseBug releaseTestCaseBug)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<ReleaseTestCaseBug>(releaseTestCaseBugService
				.persistReleaseTestCaseBug(clientProjectId, releaseTestCaseBug),
				HttpStatus.OK);
	}

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/releaseTestCaseBug/updateReleaseTestCaseBugById", method = {
			POST })
	public ResponseEntity<?> updateReleaseTestCaseBugById(
			@RequestBody ReleaseTestCaseBug releaseTestCaseBug)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		releaseTestCaseBugService
				.updateReleaseTestCaseBugById(releaseTestCaseBug);
		return new ResponseEntity<ReleaseTestCaseBug>(releaseTestCaseBug,
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testCase/releaseTestCaseBug/deleteReleaseTestCaseBugById", method = {
			DELETE })
	public ResponseEntity<?> deleteReleaseTestCaseBugById(
			@RequestParam long releaseTestCaseBugId) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		releaseTestCaseBugService
				.deleteReleaseTestCaseBugById(releaseTestCaseBugId);
		return new ResponseEntity<Void>(HttpStatus.OK);

	}

	@Override
	@RequestMapping(value = "/private/releaseTestCaseBug/getReleaseTestCaseBugById", method = {
			GET })
	public ResponseEntity<?> getReleaseTestCaseBugById(
			@RequestParam int releaseTestCaseBugId,
			@RequestParam String applicable, @RequestParam String isDeleted)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<ReleaseTestCaseBug>(
				releaseTestCaseBugService.getReleaseTestCaseBugById(
						releaseTestCaseBugId, applicable, isDeleted),
				HttpStatus.OK);
	}
	
	@Override
	@RequestMapping(value = "/private/releaseTestCaseBug/getReleaseTestCaseBugByTestCaseId", method = {
			GET })
	public ResponseEntity<?> getReleaseTestCaseBugByTestCaseId(
			@RequestParam int testCaseId,
			@RequestParam String applicable, @RequestParam String isDeleted)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<List<ReleaseTestCaseBug>>(
				releaseTestCaseBugService.getReleaseTestCaseBugByTestCaseId(
						testCaseId, applicable, isDeleted),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/releaseTestCaseBug/getReleaseTestCaseBug", method = {
			GET })
	public ResponseEntity<?> getReleaseTestCaseBug(@RequestParam int releaseId,
			@RequestParam int testCaseId,
			@RequestParam String testCaseVersionId, @RequestParam String bugId,
			@RequestParam String bugType, @RequestParam String applicable,
			@RequestParam String isDeleted,
			@RequestParam String createDateStart,
			@RequestParam String createDateEnd,
			@RequestParam String modifiedDateStart,
			@RequestParam String modifiedDateEnd) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		try {
			updateDataSource();
			return new ResponseEntity<List<ReleaseTestCaseBug>>(
					releaseTestCaseBugService.getReleaseTestCaseBug(releaseId,
							testCaseId, testCaseVersionId, bugId, bugType,
							applicable, isDeleted, createDateStart,
							createDateEnd, modifiedDateStart, modifiedDateEnd),
					HttpStatus.OK);
		} catch (APIExceptions e) {
			log.error(e);
			throw new APIExceptions(
					"Error occured whule fetching the test case bug info : "
							+ e.getMessage());
		}
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
