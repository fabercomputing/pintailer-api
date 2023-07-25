package com.fw.services;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.util.List;
import java.util.Map;

import com.fw.bean.UpdateModuleTreeBean;
import com.fw.domain.Modules;
import com.fw.domain.ModulesVersion;
import com.fw.exceptions.APIExceptions;
import com.fw.exceptions.DuplicateIdFoundException;

public interface IModulesService {

	Modules addModules(Modules unit) throws APIExceptions;

	void updateModulesById(Modules bidForm) throws APIExceptions;

	void deleteModulesById(long moduleId) throws APIExceptions;

	List<Modules> getModulesByProjectId(int clientProjectId)
			throws APIExceptions;

	List<Modules> getModulesHierarchy(int clientProjectId) throws APIExceptions;

	List<Modules> getModuleTree(int clientProjectId) throws APIExceptions;

	List<Modules> updateModuleTree(UpdateModuleTreeBean updateModuleTreeBean)
			throws APIExceptions, DuplicateIdFoundException;

	Modules getModulesById(long moduleId) throws APIExceptions;

	List<Long> getAllChildModules(long parentModuleId) throws APIExceptions;

	List<Modules> getTopParentModulesByProjectIdForReport(int clientProjectId)
			throws APIExceptions;

	List<String> getModuleHierarchy(long moduleId) throws APIExceptions;

	long getModuleIdFromHierarchy(String sql) throws APIExceptions;

	Modules getModulesByModuleNameAndClientProjectId(String moduleName,
			long parentModuleId, int clientProjectId) throws APIExceptions;

	List<ModulesVersion> getModulesVersionById(long moduleId)
			throws APIExceptions;

	Map<String, String[]> getModuleHierarchy(String moduleIds)
			throws APIExceptions;
}
