package com.fw.services.impl;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fw.dao.IClientProjectsManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.ClientProjects;
import com.fw.domain.UserManagement;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;
import com.fw.services.IClientProjectsService;
import com.fw.services.IUserManagementService;
import com.fw.utils.ApplicationCommonUtil;

@Service
public class ClientProjectServiceImpl implements IClientProjectsService {

	// private Logger log = Logger.getLogger(ClientProjectServiceImpl.class);

	@Autowired
	IClientProjectsManager clientProjectManager;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Autowired
	IUserManagementService userManagementService;

	@Override
	@Transactional
	public ClientProjects addClientProjects(ClientProjects logEntity)
			throws APIExceptions {
		if (logEntity != null) {
			return clientProjectManager.persistClientProjects(logEntity);
		} else
			return null;
	}

	@Override
	@Transactional
	public void updateClientProjectsById(ClientProjects logEntity)
			throws APIExceptions {
		if (logEntity != null) {
			clientProjectManager.updateClientProjectsById(logEntity);
		}
	}

	@Override
	@Transactional
	public ResponseEntity<Void> deleteClientProjectsById(int id)
			throws APIExceptions {
		clientProjectManager.deleteClientProjectsById(id);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Override
	public List<ClientProjects> getAssignedClientProjects()
			throws APIExceptions {
		String[] assignedProjectIds = getAssignedProjectIds();
		if (null == assignedProjectIds) {
			return null;
		} else {
			List<ClientProjects> assignedProjectsList = new ArrayList<ClientProjects>();
			for (String projectId : assignedProjectIds) {
				ClientProjects clientProject = getClientProjectsById(Integer
						.parseInt(projectId));
				if (!clientProject.getClientOrganization().equalsIgnoreCase(
						applicationCommonUtil.getDefaultOrgInOriginalCase())) {
					throw new APIExceptions(
							"The assigned project ["
									+ clientProject.getName()
									+ "] does not belong to the user's default organization ["
									+ applicationCommonUtil
											.getDefaultOrgInOriginalCase()
									+ "]. Contact administrator and try again later.");
				}
				assignedProjectsList.add(clientProject);
			}
			return assignedProjectsList;
		}
	}

	private String[] getAssignedProjectIds() throws APIExceptions {
		ClientDatabaseContextHolder.set(PintailerConstants.COMMON_ORG);
		try {
			UserManagement userManagement = userManagementService
					.getUserManagementByUserAndOrgName(
							applicationCommonUtil.getCurrentUser(),
							applicationCommonUtil.getDefaultOrgInOriginalCase());
			if (null == userManagement.getUserProject()) {
				return null;
			} else {
				return userManagement.getUserProject().split(",");
			}

		} catch (APIExceptions ex) {
			throw new APIExceptions(ex.getMessage());
		} finally {
			ClientDatabaseContextHolder.set(applicationCommonUtil
					.getDefaultOrg());
		}

	}

	@Override
	public List<ClientProjects> getAllClientProjectsForDefaultOrg()
			throws APIExceptions {
		return clientProjectManager.getAllClientProjectsForOrg(
				applicationCommonUtil.getDefaultOrgInOriginalCase(),
				applicationCommonUtil.getDefaultOrg());
	}

	@Override
	public List<ClientProjects> getAllClientProjectsForGivenOrg(
			String organizationName) throws APIExceptions {
		return clientProjectManager.getAllClientProjectsForOrg(
				organizationName, applicationCommonUtil.getDefaultOrg());
	}

	@Override
	public ClientProjects getClientProjectsById(int clientProjectId)
			throws APIExceptions {
		return clientProjectManager.getClientProjectsById(clientProjectId);
	}
}
