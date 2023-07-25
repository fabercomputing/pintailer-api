package com.fw.services.impl;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fw.bean.UpdateModuleTreeBean;
import com.fw.dao.IModulesManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.Modules;
import com.fw.domain.ModulesVersion;
import com.fw.exceptions.APIExceptions;
import com.fw.exceptions.DuplicateIdFoundException;
import com.fw.services.IModulesService;
import com.fw.utils.ApplicationCommonUtil;
import com.fw.utils.LocalUtils;

@Service
public class ModuleServiceImpl implements IModulesService {

	private Logger log = Logger.getLogger(ModuleServiceImpl.class);

	@Autowired
	IModulesManager modulesManager;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	@Transactional
	public Modules addModules(Modules logEntity) throws APIExceptions {
		if (logEntity != null) {
			return modulesManager.persistModules(logEntity);
		} else
			return null;
	}

	@Override
	@Transactional
	public void updateModulesById(Modules logEntity) throws APIExceptions {
		if (logEntity != null) {
			modulesManager.updateModulesById(logEntity);
		}
	}

	@Override
	@Transactional
	public void deleteModulesById(long moduleId) throws APIExceptions {
		modulesManager.deleteModulesById(moduleId);
	}

	@Override
	public Modules getModulesById(long moduleId) throws APIExceptions {
		updateDataSource();
		return modulesManager.getModulesById(moduleId);
	}

	@Override
	public List<Modules> getModulesByProjectId(int clientProjectId)
			throws APIExceptions {
		updateDataSource();
		return modulesManager.getModulesByProjectId(clientProjectId);
	}

	@Override
	public List<Modules> getModuleTree(int clientProjectId)
			throws APIExceptions {
		List<Modules> modules = getModulesByProjectId(clientProjectId);
		return getNestedChildren(modules);
	}

	private List<Modules> getNestedChildren(List<Modules> modules)
			throws APIExceptions {

		Map<Long, Modules> moduleMap = new HashMap<>();
		Modules childModule = null;
		Modules parentModule = null;

		// Here the module hierarchy is getting created as each module which has
		// a parent is considered as its child and is pushed under its parent
		// module bean as child
		for (Modules item : modules) {
			// ------ Process child ----
			// the map is used to store all the modules so that immediate parent
			// can be search for a module in the loop
			if (!moduleMap.containsKey(item.getModuleId())) {
				moduleMap.put(item.getModuleId(), item);
			}
			childModule = moduleMap.get(item.getModuleId());

			// ------ Process Parent ----
			// If a module is not a top level module, it will be a child of its
			// parent module as per the DB structure.
			if (item.getModuleParentId() != 0) {
				// In case the modules are not in the order i.e. expected order
				// is parent comes
				// first and child later but can be fetch in random, the below
				// 'if' condition is added to add
				// the parent module which has not yet accessed in the loop to
				// maintain the loop logic and to avoid the extra order by
				// condition in the query. Also the order by condition in the
				// query is not a full proof solution as modules addition does
				// not ensure than parent will always getting added before the
				// child
				if (!moduleMap.containsKey(item.getModuleParentId())) {
					moduleMap.put(item.getModuleParentId(), modulesManager
							.getModulesById(item.getModuleParentId()));
				}

				// Now fetching the parent module bean and adding the current
				// item as its child
				parentModule = moduleMap.get(item.getModuleParentId());
				parentModule.getChildren().add(childModule);
			}

		}

		// The below loop is to return the top most parents modules in the list
		// as all their respective children has been added in their beans.
		List<Modules> moduleTree = new ArrayList<Modules>();
		for (Modules mmd : moduleMap.values()) {
			if (mmd.getModuleParentId() == 0) {
				moduleTree.add(mmd);
			}

		}
		return moduleTree;
	}

	@Override
	public List<Modules> getModulesHierarchy(int clientProjectId)
			throws APIExceptions {
		List<Modules> modules = getModulesByProjectId(clientProjectId);
		List<Modules> modulesWithHierarchy = new ArrayList<Modules>();
		for (Modules module : modules) {
			List<String> manualTestCaseModuleHierarchy = getModuleHierarchy(
					module.getModuleId());
			if (null == manualTestCaseModuleHierarchy
					|| manualTestCaseModuleHierarchy.isEmpty()) {
				throw new APIExceptions(
						"Some error occured while retrieving the module hierarchy for the module ["
								+ module.getName() + "]");
			}
			module.setHierarchy(manualTestCaseModuleHierarchy.get(0));
			modulesWithHierarchy.add(module);
		}
		return modulesWithHierarchy;
	}

	@Override
	@Transactional
	public List<Modules> updateModuleTree(
			UpdateModuleTreeBean updateModuleTreeBean)
			throws APIExceptions, DuplicateIdFoundException {
		try {
			Modules module = modulesManager.getModulesByModuleName(
					updateModuleTreeBean.getNewModuleName(),
					updateModuleTreeBean.getProjectId());
			if (module == null) {
				Modules modules = new Modules();
				modules.setName(updateModuleTreeBean.getNewModuleName());
				modules.setModuleParentId(
						updateModuleTreeBean.getParentModuleId());
				modules.setClientProjectsId(
						updateModuleTreeBean.getProjectId());
				modules.setCreatedBy(updateModuleTreeBean.getUserName());
				modules.setModifiedBy(updateModuleTreeBean.getUserName());

				// persist module
				modulesManager.persistModules(modules);
				// get module hierarchy
				return getModuleTree(updateModuleTreeBean.getProjectId());
			} else {
				throw new DuplicateIdFoundException(LocalUtils.getStringLocale(
						"fw_test_mgmt_locale", "DuplicateModule"));
			}
		} catch (APIExceptions e) {
			log.error(
					"Error occured while updating the module hierarchy tree : "
							+ e.getMessage());
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "UpdateModuleTree"));
		}
	}

	@Override
	public List<Long> getAllChildModules(long parentModuleId)
			throws APIExceptions {
		updateDataSource();
		return modulesManager.getAllChildModules(parentModuleId);
	}

	@Override
	public List<Modules> getTopParentModulesByProjectIdForReport(
			int clientProjectId) throws APIExceptions {
		updateDataSource();
		return modulesManager
				.getTopParentModulesByProjectIdForReport(clientProjectId);
	}

	@Override
	public List<String> getModuleHierarchy(long moduleId) throws APIExceptions {
		updateDataSource();
		return modulesManager.getModuleHierarchy(moduleId);
	}

	@Override
	public Map<String, String[]> getModuleHierarchy(String moduleIds)
			throws APIExceptions {
		updateDataSource();
		return modulesManager.getModuleHierarchy(moduleIds);
	}

	@Override
	public long getModuleIdFromHierarchy(String sql) throws APIExceptions {
		return modulesManager.getModuleIdFromHierarchy(sql);
	}

	@Override
	public Modules getModulesByModuleNameAndClientProjectId(String moduleName,
			long parentModuleId, int clientProjectId) throws APIExceptions {
		return modulesManager.getModulesByModuleNameAndClientProjectId(
				moduleName, parentModuleId, clientProjectId);
	}

	@Override
	public List<ModulesVersion> getModulesVersionById(long moduleId)
			throws APIExceptions {
		updateDataSource();
		return modulesManager.getModulesVersionById(moduleId);
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
