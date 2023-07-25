package com.fw.controller;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import org.springframework.http.ResponseEntity;

import com.fw.bean.UpdateModuleTreeBean;
import com.fw.domain.Modules;
import com.fw.exceptions.APIExceptions;
import com.fw.exceptions.DuplicateIdFoundException;

public interface IModulesController {

	ResponseEntity<?> addModules(Modules unit) throws APIExceptions;

	ResponseEntity<?> updateModulesById(Modules bidForm) throws APIExceptions;

	ResponseEntity<?> removeModules(long moduleId) throws APIExceptions;

	ResponseEntity<?> getModulesByProjectId(int clientProjectId)
			throws APIExceptions;

	ResponseEntity<?> getModulesHierarchy(int clientProjectId)
			throws APIExceptions;

	ResponseEntity<?> getModuleTreeByProjectId(int clientProjectId)
			throws APIExceptions;

	ResponseEntity<?> updateModuleTree(
			UpdateModuleTreeBean updateModuleTreeBean)
			throws APIExceptions, DuplicateIdFoundException;

	ResponseEntity<?> getModulesById(long moduleId) throws APIExceptions;

	ResponseEntity<?> getModulesVersionById(long moduleId) throws APIExceptions;
}
