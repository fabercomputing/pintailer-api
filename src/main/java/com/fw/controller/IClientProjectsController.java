package com.fw.controller;

import org.springframework.http.ResponseEntity;

import com.fw.domain.ClientProjects;
import com.fw.exceptions.APIExceptions;

/**
 * 
 * @author Sumit Srivastava
 *
 */

public interface IClientProjectsController {

	ResponseEntity<?> addClientProjects(ClientProjects clientProject)
			throws APIExceptions;

	ResponseEntity<?> updateClientProjectsById(ClientProjects clientProject)
			throws APIExceptions;

	ResponseEntity<?> getClientProjectsById(int clientProjectId)
			throws APIExceptions;

	ResponseEntity<?> removeClientProject(int clientProjectsId)
			throws APIExceptions;

	ResponseEntity<?> getAssignedClientProjectsForDefaultOrg()
			throws APIExceptions;

	ResponseEntity<?> getAllClientProjectsForDefaultOrg() throws APIExceptions;

	ResponseEntity<?> getAllClientProjectsForGivenOrg(String organizationName)
			throws APIExceptions;

}
