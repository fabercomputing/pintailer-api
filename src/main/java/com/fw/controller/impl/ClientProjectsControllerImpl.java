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
import com.fw.controller.IClientProjectsController;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.ClientProjects;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IClientProjectsService;
import com.fw.utils.ApplicationCommonUtil;

/**
 * 
 * @author Sumit Srivastava
 *
 */

@Controller
@RequestMapping(value = "/fwTestManagement")
public class ClientProjectsControllerImpl implements IClientProjectsController {

	private static Logger log = Logger
			.getLogger(ClientProjectsControllerImpl.class);

	@Autowired
	IClientProjectsService clientProjectsService;

	@Autowired
	AuthorizeUser authorizeUser;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/clientProject/addClientProject", method = { POST })
	public ResponseEntity<?> addClientProjects(
			@RequestBody ClientProjects clientProject) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForOrganization(clientProject
					.getClientOrganization());
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<ClientProjects>(
				clientProjectsService.addClientProjects(clientProject),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/clientProject/updateClientProject", method = { PATCH })
	public ResponseEntity<?> updateClientProjectsById(
			@RequestBody ClientProjects clientProject) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProject
					.getClientProjectId());
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		clientProjectsService.updateClientProjectsById(clientProject);
		return new ResponseEntity<ClientProjects>(clientProject, HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/clientProject/getClientProject", method = { GET })
	public ResponseEntity<?> getClientProjectsById(
			@RequestParam("clientProjectsId") int clientProjectsId)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectsId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<ClientProjects>(
				clientProjectsService.getClientProjectsById(clientProjectsId),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/clientProject/deleteClientProject", method = { DELETE })
	public ResponseEntity<?> removeClientProject(
			@RequestParam("clientProjectsId") int clientProjectsId)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectsId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		clientProjectsService.deleteClientProjectsById(clientProjectsId);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/clientProject/getAssignedClientProjectsForDefaultOrg", method = { GET })
	public ResponseEntity<?> getAssignedClientProjectsForDefaultOrg()
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		List<ClientProjects> clientProjects = clientProjectsService
				.getAssignedClientProjects();
		if (null == clientProjects) {
			return new ResponseEntity<String>(
					"User is not assigned to any project. Atleast one project "
							+ "is required to continue.", HttpStatus.OK);
		}
		return new ResponseEntity<List<ClientProjects>>(clientProjects,
				HttpStatus.OK);

	}

	@Override
	@RequestMapping(value = "/private/clientProject/getAllClientProjectsForDefaultOrg", method = { GET })
	public ResponseEntity<?> getAllClientProjectsForDefaultOrg()
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<List<ClientProjects>>(
				clientProjectsService.getAllClientProjectsForDefaultOrg(),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/clientProject/getAllClientProjectsForGivenOrg", method = { GET })
	public ResponseEntity<?> getAllClientProjectsForGivenOrg(
			@RequestParam("organizationName") String organizationName)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<List<ClientProjects>>(
				clientProjectsService
						.getAllClientProjectsForGivenOrg(organizationName),
				HttpStatus.OK);
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
