package com.fw.controller.impl;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fw.bean.UpdateModuleTreeBean;
import com.fw.config.AuthorizeUser;
import com.fw.controller.IModulesController;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.Modules;
import com.fw.domain.ModulesVersion;
import com.fw.exceptions.APIExceptions;
import com.fw.exceptions.DuplicateIdFoundException;
import com.fw.services.IModulesService;
import com.fw.utils.ApplicationCommonUtil;
import com.fw.utils.LocalUtils;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class ModulesControllerImpl implements IModulesController {

	private Logger log = Logger.getLogger(ModulesControllerImpl.class);

	@Autowired
	IModulesService modulesService;

	@Autowired
	AuthorizeUser authorizeUser;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/modules/addModules", method = { POST })
	public ResponseEntity<?> addModules(@RequestBody Modules modules)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser
					.authorizeUserForProjectId(modules.getClientProjectsId());
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		modulesService.addModules(modules);
		return new ResponseEntity<String>("Module added succesfully.",
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/modules/updateModulesById", method = {
			PATCH })
	public ResponseEntity<?> updateModulesById(@RequestBody Modules modules)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser
					.authorizeUserForProjectId(modules.getClientProjectsId());
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		modulesService.updateModulesById(modules);
		return new ResponseEntity<Modules>(modules, HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/modules/deleteModules", method = {
			DELETE })
	public ResponseEntity<?> removeModules(
			@RequestParam("moduleId") long moduleId) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		modulesService.deleteModulesById(moduleId);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/modules/getModulesById", method = { GET })
	public ResponseEntity<?> getModulesById(
			@RequestParam("moduleId") long moduleId) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<Modules>(
				modulesService.getModulesById(moduleId), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/modules/getModulesByProjectId", method = {
			GET })
	public ResponseEntity<?> getModulesByProjectId(
			@RequestParam("clientProjectId") int clientProjectsId)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectsId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<Modules>>(
				modulesService.getModulesByProjectId(clientProjectsId),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/modules/getModulesHierarchy", method = {
			GET })
	public ResponseEntity<?> getModulesHierarchy(
			@RequestParam("clientProjectId") int clientProjectsId)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectsId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<Modules>>(
				modulesService.getModulesHierarchy(clientProjectsId),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/modules/getModuleTree", method = { GET })
	public ResponseEntity<?> getModuleTreeByProjectId(
			@RequestParam("clientProjectId") int clientProjectsId)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectsId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<Modules>>(
				modulesService.getModuleTree(clientProjectsId), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/modules/updateModuleTree", method = {
			PATCH })
	public ResponseEntity<?> updateModuleTree(
			@RequestBody UpdateModuleTreeBean updateModuleTreeBean)
			throws APIExceptions, DuplicateIdFoundException {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(
					updateModuleTreeBean.getProjectId());
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		try {
			List<Modules> modules = null;
			modules = modulesService.updateModuleTree(updateModuleTreeBean);
			return new ResponseEntity<List<Modules>>(modules, HttpStatus.OK);
		} catch (DuplicateIdFoundException e) {
			String message = LocalUtils.getStringLocale("fw_test_mgmt_locale",
					"DuplicateModule");
			log.error(message);
			throw new APIExceptions(message);
		} catch (APIExceptions e) {
			String message = LocalUtils.getStringLocale("fw_test_mgmt_locale",
					"UpdateModuleTree");
			throw new APIExceptions(message);
		}
	}

	@Override
	@RequestMapping(value = "/private/modules/getModulesVersion", method = {
			GET })
	public ResponseEntity<?> getModulesVersionById(
			@RequestParam("moduleId") long moduleId) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<List<ModulesVersion>>(
				modulesService.getModulesVersionById(moduleId), HttpStatus.OK);
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}