package com.fw.dao;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.List;
import java.util.Map;

import com.fw.domain.Modules;
import com.fw.domain.ModulesVersion;
import com.fw.exceptions.APIExceptions;

public interface IModulesManager {

	Modules persistModules(Modules logEntity) throws APIExceptions;

	void updateModulesById(Modules logEntity) throws APIExceptions;

	Modules getModulesById(long moduleId) throws APIExceptions;

	void deleteModulesById(long moduleId) throws APIExceptions;

	Modules getModulesByModuleNameAndClientProjectId(String moduleName,
			long parentModuleId, int clientProjectId) throws APIExceptions;

	List<Modules> getModulesByProjectId(int clientProjectId)
			throws APIExceptions;

	long getModuleIdFromHierarchy(String sql) throws APIExceptions;

	List<Modules> getTopParentModulesByProjectIdForReport(int clientProjectId)
			throws APIExceptions;

	List<Long> getAllChildModules(long parentModuleId) throws APIExceptions;

	List<String> getModuleHierarchy(long moduleId) throws APIExceptions;

	Modules getModulesByModuleName(String moduleName, int clientProjectId)
			throws APIExceptions;

	List<ModulesVersion> getModulesVersionById(long moduleId)
			throws APIExceptions;

	Map<String, String[]> getModuleHierarchy(String moduleIds)
			throws APIExceptions;
}
