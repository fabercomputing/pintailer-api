package com.fw.dao;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.List;

import com.fw.domain.ClientProjects;
import com.fw.exceptions.APIExceptions;

public interface IClientProjectsManager {

	ClientProjects persistClientProjects(ClientProjects clientProjects)
			throws APIExceptions;

	void updateClientProjectsById(ClientProjects clientProjects)
			throws APIExceptions;

	ClientProjects getClientProjectsById(int clientProjectId)
			throws APIExceptions;

	void deleteClientProjectsById(int clientProjectId) throws APIExceptions;

	List<ClientProjects> getAllClientProjectsForOrg(String organizationName,
			String DBName) throws APIExceptions;

}
