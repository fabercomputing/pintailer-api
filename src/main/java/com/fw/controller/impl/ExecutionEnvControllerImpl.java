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
import org.springframework.web.bind.annotation.ResponseBody;

import com.fw.config.AuthorizeUser;
import com.fw.controller.IExecutionEnvController;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.ExecutionEnv;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IExecutionEnvService;
import com.fw.utils.ApplicationCommonUtil;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class ExecutionEnvControllerImpl implements IExecutionEnvController {

	private static Logger log = Logger
			.getLogger(ExecutionEnvControllerImpl.class);

	@Autowired
	IExecutionEnvService executionEnvService;

	@Autowired
	AuthorizeUser authorizeUser;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/executionEnv/addExecutionEnv", method = { POST })
	public ResponseEntity<?> addExecutionEnv(
			@RequestBody ExecutionEnv executionEnv) throws APIExceptions {
		
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForOrganization(executionEnv
					.getClientOrganization());
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<ExecutionEnv>(
				executionEnvService.addExecutionEnv(executionEnv),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/executionEnv/updateExecutionEnv", method = { PATCH })
	public ResponseEntity<?> updateExecutionEnv(
			@RequestBody ExecutionEnv executionEnv) throws APIExceptions {
		updateDataSource();
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForOrganization(executionEnv
					.getClientOrganization());
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Integer>(
				executionEnvService.updateExecutionEnv(executionEnv),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/executionEnv/getAllExecutionEnv", method = { GET })
	public ResponseEntity<?> getAllExecutionEnv() throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<ExecutionEnv>>(
				executionEnvService.getAllExecutionEnvs(), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/executionEnv/deleteExecutionEnv/{executionEnvId}", method = { DELETE })
	public ResponseEntity<?> deleteExecutionEnv(
			@PathVariable("executionEnvId") int executionEnvId)
			throws APIExceptions {
		updateDataSource();
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Integer>(
				executionEnvService.deleteExecutionEnvById(executionEnvId),
				HttpStatus.OK);
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
