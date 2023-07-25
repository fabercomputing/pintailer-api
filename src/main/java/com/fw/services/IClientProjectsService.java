package com.fw.services;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.fw.domain.ClientProjects;
import com.fw.exceptions.APIExceptions;

public interface IClientProjectsService {

	ClientProjects addClientProjects(ClientProjects clientProjects)
			throws APIExceptions;

	List<ClientProjects> getAssignedClientProjects() throws APIExceptions;

	List<ClientProjects> getAllClientProjectsForDefaultOrg()
			throws APIExceptions;

	void updateClientProjectsById(ClientProjects clientProjects)
			throws APIExceptions;

	ClientProjects getClientProjectsById(int clientProjectsId)
			throws APIExceptions;

	ResponseEntity<Void> deleteClientProjectsById(int clientProjectsId)
			throws APIExceptions;

	List<ClientProjects> getAllClientProjectsForGivenOrg(String organizationName)
			throws APIExceptions;
}
