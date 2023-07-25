package com.fw.controller.impl;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fw.config.AuthorizeUser;
import com.fw.controller.IReleaseController;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.Release;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IReleaseService;
import com.fw.utils.ApplicationCommonUtil;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class ReleaseControllerImpl implements IReleaseController {

	private static Logger log = Logger.getLogger(ReleaseControllerImpl.class);

	@Autowired
	IReleaseService releaseService;

	@Autowired
	AuthorizeUser authorizeUser;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/release/addRelease", method = { POST })
	public ResponseEntity<?> addRelease(@RequestBody Release release)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(release
					.getClientProjectId());
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<Release>(
				releaseService.persistRelease(release), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/release/updateRelease", method = { PATCH })
	public ResponseEntity<?> updateRelease(@RequestBody Release release)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(release
					.getClientProjectId());
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<Release>(
				releaseService.updateRelease(release), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/release/getAllReleases", method = { GET })
	public ResponseEntity<?> getAllReleases(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("condition") String condition) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<Release>>(releaseService.getAllReleases(
				clientProjectId, condition), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/release/deleteRelease/{releaseId}", method = { DELETE })
	public ResponseEntity<?> deleteRelease(
			@PathVariable("releaseId") int releaseId) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<Integer>(
				releaseService.deleteRelease(releaseId), HttpStatus.OK);

	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
