package com.fw.controller;

import java.util.List;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import org.springframework.http.ResponseEntity;

import com.fw.domain.TestScenarios;
import com.fw.exceptions.APIExceptions;

public interface IFeatureManagementController {
	ResponseEntity<?> getEntireFeature(int clientProjectId, String featureName)
			throws APIExceptions;

	ResponseEntity<?> updateFeature(int clientProjectId,
			List<TestScenarios> testScenarios) throws APIExceptions;

	ResponseEntity<String> downloadFeatureFile(int clientProjectId,
			String featureName, String reportFilePath) throws APIExceptions;

	ResponseEntity<?> getFeatureFileVersions(int clientProjectId,
			String featureFileName) throws APIExceptions;
}
