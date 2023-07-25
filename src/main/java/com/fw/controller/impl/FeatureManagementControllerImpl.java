package com.fw.controller.impl;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fw.config.AuthorizeUser;
import com.fw.controller.IFeatureManagementController;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.TestScenarios;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IFeatureManagementService;
import com.fw.utils.ApplicationCommonUtil;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class FeatureManagementControllerImpl
		implements IFeatureManagementController {

	private static Logger log = Logger
			.getLogger(FeatureManagementControllerImpl.class);

	@Autowired
	IFeatureManagementService featureManagementService;

	@Autowired
	AuthorizeUser authorizeUser;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	@RequestMapping(value = "/private/featureManagement/getEntireFeature", method = {
			GET })
	public ResponseEntity<?> getEntireFeature(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("featureName") String featureName)
			throws APIExceptions {
		updateDataSource();
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		try {
			return new ResponseEntity<List<TestScenarios>>(
					featureManagementService.getEntireFeature(clientProjectId,
							featureName),
					HttpStatus.OK);
		} catch (Exception e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(
					"Error occured while fetching the feature file : "
							+ e.getMessage(),
					HttpStatus.NO_CONTENT);
		}
	}

	@Override
	@RequestMapping(value = "/private/featureManagement/updateFeature", method = {
			PATCH })
	public ResponseEntity<?> updateFeature(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestBody List<TestScenarios> testScenarios)
			throws APIExceptions {
		updateDataSource();
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		try {
			return new ResponseEntity<Boolean>(featureManagementService
					.updateFeature(clientProjectId, testScenarios),
					HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<String>(
					"Error occured while updating the " + "feature file : "
							+ ex.getMessage(),
					HttpStatus.NOT_MODIFIED);
		}
	}

	@Override
	@RequestMapping(value = "/private/featureManagement/downloadFeatureFile", method = {
			GET })
	public ResponseEntity<String> downloadFeatureFile(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("featureName") String featureName,
			@RequestParam("reportFilePath") String reportFilePath)
			throws APIExceptions {
		updateDataSource();
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<String>(
				featureManagementService.downloadFeatureFile(clientProjectId,
						featureName, reportFilePath),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/featureManagement/getFeatureFileVersions", method = {
			GET })
	public ResponseEntity<?> getFeatureFileVersions(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("featureName") String featureName)
			throws APIExceptions {
		updateDataSource();
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity<Map<String, List<TestScenarios>>>(
				featureManagementService.getFeatureFileVersions(clientProjectId,
						featureName),
				HttpStatus.OK);
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}

}
