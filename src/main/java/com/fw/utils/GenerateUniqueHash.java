package com.fw.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

import com.fw.exceptions.APIExceptions;

public class GenerateUniqueHash {

	private static Logger log = Logger.getLogger(GenerateUniqueHash.class);

	private static String hash(String text) throws APIExceptions {
		text = trimLowerString(text);

		MessageDigest md;
		String hashtext = null;
		try {
			md = MessageDigest.getInstance("MD5");
			md.reset();
			md.update(text.toString().getBytes("UTF-8"));
			byte[] digest = md.digest();
			BigInteger bigInt = new BigInteger(1, digest);
			hashtext = bigInt.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
		} catch (NoSuchAlgorithmException e) {
			String message = "Error occured while creating the hash : "
					+ e.getMessage();
			log.error(message);
			throw new APIExceptions(message);
		} catch (UnsupportedEncodingException e) {
			String message = "Error occured while creating the hash : "
					+ e.getMessage();
			log.error(message);
			throw new APIExceptions(message);
		}
		return hashtext;
	}

	public static String trimLowerString(final String text) {
		String temp = text.replaceAll("( )+", "").replaceAll("\n", "")
				.replaceAll("\r", "").replaceAll("\t", "");
		return temp.toLowerCase().trim();
	}

	/**
	 * Return the hash code for the feature and scenario combined to maintain
	 * uniqueness in scenario creation for a specific feature. Inside DB used in
	 * table : test_scenarios
	 */
	public static String getFeatureScenarioHash(final long clientProjectId,
			final String featureName, final String scenarioName)
			throws APIExceptions {
		if (clientProjectId <= 0) {
			throw new APIExceptions(
					"Project details are not provided. Process cannot continue.");
		}
		if (!ValueValidations.isValueValid(featureName)) {
			throw new APIExceptions("Invalid feature name [" + featureName
					+ "] is given");
		}
		if (!ValueValidations.isValueValid(scenarioName)) {
			throw new APIExceptions("Invalid scenario name [" + scenarioName
					+ "] is given");
		}
		return hash(featureName.concat(scenarioName));
	}

	/**
	 * Return the hash code for the test case where 5 fields are currently being
	 * used to maintain uniqueness in test case creation. Inside DB used in
	 * table : test_case
	 */
	public static String getTestCaseHash(long moduleId, String testCaseIdRef,
			String testSummary, String preCondition, String executionSteps,
			String expectedResults) throws APIExceptions {
		if (moduleId <= 0) {
			throw new APIExceptions("Module cannot be blank. Please provide "
					+ "the required information.");
		}
		if (ValueValidations.isValueNull(testCaseIdRef)) {
			testCaseIdRef = "";
		}
		if (ValueValidations.isValueNull(testSummary)) {
			throw new APIExceptions(
					"Test summary cannot be blank. Please provide some valid value.");
		}
		if (ValueValidations.isValueNull(preCondition)) {
			preCondition = "";
		}
		if (ValueValidations.isValueNull(executionSteps)) {
			executionSteps = "";
		}
		if (ValueValidations.isValueNull(expectedResults)) {
			expectedResults = "";
		}
		return hash(("" + moduleId).concat(testCaseIdRef).concat(testSummary)
				.concat(preCondition).concat(executionSteps)
				.concat(expectedResults));
	}

	public static String getTestStepHash(int client_project_id,
			String stepDescription) throws APIExceptions {
		if (client_project_id <= 0
				&& !stepDescription.toLowerCase().contains("dummy")) {
			throw new APIExceptions(
					"Client project ID cannot be blank. Please provide "
							+ "the required information.");
		}
		if (ValueValidations.isValueNull(stepDescription)) {
			throw new APIExceptions(
					"Test step description cannot be blank. Please provide some valid value.");
		}
		return hash((client_project_id + "").concat(stepDescription));
	}
}
