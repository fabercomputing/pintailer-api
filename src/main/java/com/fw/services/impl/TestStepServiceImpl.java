package com.fw.services.impl;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fw.dao.IClientProjectsManager;
import com.fw.dao.ITestCaseManager;
import com.fw.dao.ITestCaseMapManager;
import com.fw.dao.ITestScenarioStepManager;
import com.fw.dao.ITestScenariosManager;
import com.fw.dao.ITestStepManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.Modules;
import com.fw.domain.TestCase;
import com.fw.domain.TestCaseMap;
import com.fw.domain.TestScenarioStep;
import com.fw.domain.TestScenarioStepVersion;
import com.fw.domain.TestScenarios;
import com.fw.domain.TestStep;
import com.fw.domain.TestStepVersion;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;
import com.fw.services.IFeatureManagementService;
import com.fw.services.ITestCaseMapService;
import com.fw.services.ITestCaseService;
import com.fw.services.ITestScenariosService;
import com.fw.services.ITestStepService;
import com.fw.utils.ApplicationCommonUtil;
import com.fw.utils.GenerateUniqueHash;
import com.fw.utils.LocalUtils;
import com.fw.utils.TestStepsInfoUtil;
import com.fw.utils.ValueValidations;

@Service
public class TestStepServiceImpl implements ITestStepService {

	private Logger log = Logger.getLogger(TestStepServiceImpl.class);

	@Autowired
	ITestStepManager testStepManager;

	@Autowired
	ITestScenariosManager testScenariosManager;

	@Autowired
	ITestScenarioStepManager testScenarioStepManager;

	@Autowired
	ITestCaseMapService testCaseMapService;

	@Autowired
	IClientProjectsManager clientProjectsManager;

	@Autowired
	ITestCaseMapManager testCaseMapManager;

	@Autowired
	ITestCaseManager testCaseManager;

	@Autowired
	ITestCaseService testCaseService;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Autowired
	ITestScenariosService testScenariosService;

	@Autowired
	IFeatureManagementService featureManagementService;

	@Override
	@Transactional
	public TestStep addTestStep(TestStep testStep) throws APIExceptions {
		if (testStep != null) {
			testStep.setHashCode(GenerateUniqueHash.getTestStepHash(
					testStep.getClientProjectId(), testStep.getName()));
			return testStepManager.persistTestStep(testStep);
		} else
			return null;
	}

	@Override
	@Transactional
	public int updateTestStepById(TestStep testStep) throws APIExceptions {
		if (testStep != null) {
			testStep.setHashCode(GenerateUniqueHash.getTestStepHash(
					testStep.getClientProjectId(), testStep.getName()));
			return testStepManager.updateTestStepById(testStep);
		}
		return 0;
	}

	@Override
	@Transactional
	public void updateTestStepById(TestStep testStep, boolean applicableFlg)
			throws APIExceptions {
		if (testStep != null) {
			testStep.setHashCode(GenerateUniqueHash.getTestStepHash(
					testStep.getClientProjectId(), testStep.getName()));
			testStepManager.updateTestStepById(testStep, applicableFlg);
		}
	}

	@Override
	@Transactional
	public ResponseEntity<Void> deleteTestStepById(long testStepId)
			throws APIExceptions {
		testStepManager.deleteTestStepById(testStepId);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Override
	public List<TestStep> getTestStep(int clientProjectId)
			throws APIExceptions {
		return testStepManager.getAllTestStepRowMapper(clientProjectId);
	}

	@Override
	public TestStep getTestStepById(long testStepId, int clientProjectId)
			throws APIExceptions {
		return testStepManager.getTestStepById(testStepId, clientProjectId);
	}

	@Override
	public List<TestStepVersion> getTestStepsVersionByStepId(long testStepId)
			throws APIExceptions {
		return testStepManager.getTestStepsVersionByStepId(testStepId);
	}

	@Override
	public Map<Integer, TestScenarios> getTestStepByScenarios(
			TestScenarios testScenarios) throws APIExceptions {
		Map<Integer, TestScenarios> result = new LinkedHashMap<Integer, TestScenarios>();
		try {
//			String scenarioName = testScenarios.getName();
//			String featureFileName = testScenarios.getFeatureFileName();
//			int scenarioId = testScenariosManager
//					.getTestScenarioIdByScenarioAndFeatureFile(
//							testScenarios.getClientProjectId(), scenarioName,
//							featureFileName);
			/*
			 * Getting all the version of scenario step mapping for the given
			 * scenario id irrespective of the version.
			 */
			List<TestScenarioStepVersion> scenarioStepMappingVersion = testScenarioStepManager
					.getScenarioStepMappingVersion(
							testScenarios.getTestScenarioId(), 0);

//			if (null == scenarioStepMappingVersion
//					|| scenarioStepMappingVersion.isEmpty()) {
//				List<TestScenarioStep> testScenariosStepList = testScenarioStepManager
//						.getTestStepIdByScenarioId(
//								testScenarios.getClientProjectId(), scenarioId);
//
//				for (TestScenarioStep testScenarioStep : testScenariosStepList) {
//					TestStep testStep = getTestStepById(
//							testScenarioStep.getTestStepId(),
//							testScenarios.getClientProjectId());
//					if (!ValueValidations
//							.isValueNull(testScenarioStep.getStepKeyword())) {
//						testStep.setName(testScenarioStep.getStepKeyword()
//								.concat(" ").concat(testStep.getName()));
//					}
//					if (!ValueValidations
//							.isValueValid(testStep.getStepSelectedVersion())) {
//						testStep.setStepSelectedVersion(
//								testStep.getStepLatestVersion());
//					}
//					testStepList.add(testStep);
//				}
//				result.put(0, testStepList);
//			} else {
			for (TestScenarioStepVersion testScenarioStepVersion : scenarioStepMappingVersion) {
//			TestScenarioStepVersion testScenarioStepVersion = scenarioStepMappingVersion
//					.get(0);
				/*
				 * Fetching scenario information for the specific scenario id
				 * and version
				 */
				int testScenariosId = testScenarioStepVersion
						.getTestScenariosId();
				TestScenarios specificTestScenariosVersion = testScenariosManager
						.getSpecificTestScenariosVersion(
								testScenarios.getClientProjectId(),
								testScenariosId, testScenarioStepVersion
										.getTestScenariosVersionId());
				if (null == specificTestScenariosVersion
						&& testScenarioStepVersion
								.getTestScenariosVersionId() == 1) {
					specificTestScenariosVersion = testScenariosManager
							.getTestScenariosById(testScenariosId, "false");
				} else if (null == specificTestScenariosVersion
						&& testScenarioStepVersion
								.getTestScenariosVersionId() != 1) {
					throw new APIExceptions(
							"Error occured while fetching the scenario version ["
									+ testScenarioStepVersion
											.getTestScenariosVersionId()
									+ "] info");
				}
				specificTestScenariosVersion.setScenarioLatestVersion(
						testScenarios.getScenarioLatestVersion());
				String[] stepInfosList = testScenarioStepVersion
						.getTestStepIdVersionSequenceKeyword().split(",");
				List<TestStep> testStepList = new ArrayList<TestStep>();
				for (String stepInfos : stepInfosList) {
					String[] stepInfo = stepInfos.split("-");
					List<TestStepVersion> testStepsVersionsList = getTestStepsVersionByStepId(
							Long.parseLong(stepInfo[0]));
					TestStep testStep = null;
					if (null == testStepsVersionsList
							|| testStepsVersionsList.isEmpty()) {
						testStep = getTestStepById(Long.parseLong(stepInfo[0]),
								testScenarios.getClientProjectId());
						testStep.setStepSelectedVersion(
								testStep.getStepLatestVersion());
						if (stepInfo.length == 4
								&& !ValueValidations.isValueNull(stepInfo[3])) {
							testStep.setName(stepInfo[3].concat(" ")
									.concat(testStep.getName()));
						}
					} else {
						testStep = getTestStepById(Long.parseLong(stepInfo[0]),
								testScenarios.getClientProjectId());

						TestStepVersion testStepsVersionByStepIdAndVersionId = testStepManager
								.getTestStepsVersionByStepIdAndVersionId(
										Long.parseLong(stepInfo[0]),
										Integer.parseInt(
												stepInfo[1].replace("V", "")));
						testStep.setClientProjectId(
								testStepsVersionByStepIdAndVersionId
										.getClientProjectId());
						testStep.setCreatedBy(
								testStepsVersionByStepIdAndVersionId
										.getCreatedBy());
						testStep.setDeleted(testStepsVersionByStepIdAndVersionId
								.isDeleted());
						testStep.setHashCode(
								testStepsVersionByStepIdAndVersionId
										.getHashCode());
						testStep.setModifiedBy(
								testStepsVersionByStepIdAndVersionId
										.getModifiedBy());
						if (stepInfo.length == 4
								&& !ValueValidations.isValueNull(stepInfo[3])) {
							testStep.setName(stepInfo[3].concat(" ")
									.concat(testStepsVersionByStepIdAndVersionId
											.getName()));
						} else {
							testStep.setName(
									testStepsVersionByStepIdAndVersionId
											.getName());
						}
						testStep.setStepSelectedVersion(stepInfo[1]);
					}
					testStepList.add(testStep);
				}
				specificTestScenariosVersion.setTestStepsList(testStepList);
				result.put(
						testScenarioStepVersion.getTestScenarioStepVersionId(),
						specificTestScenariosVersion);
			}
			return result;
		} catch (APIExceptions e) {
			throw new APIExceptions(
					(LocalUtils.getStringLocale("fw_test_mgmt_locale",
							"TestScenarios")) + e.getMessage());
		}
	}

	private List<String> extractMethodNames(MultipartFile file) throws IOException {
		List<String> methodNames = new ArrayList<>();

		try (InputStream inputStream = file.getInputStream();
			 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim(); // Remove leading and trailing whitespace

				if (line.startsWith("public void") && line.contains("(") && line.contains(")")) {
					int startIndex = line.indexOf("public void") + 12;
					int endIndex = line.indexOf("(");

					// Check if the startIndex and endIndex are valid
					if (startIndex >= 0 && endIndex >= 0 && endIndex > startIndex) {
						String methodName = line.substring(startIndex, endIndex);
						methodNames.add(methodName);
					}
				}
			}
		}

		return methodNames;
	}


	public List<String> extractDescriptionTexts(MultipartFile file) throws IOException {
		List<String> descriptionTexts = new ArrayList<>();

		try (InputStream inputStream = file.getInputStream();
			 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

			String line;

			while ((line = reader.readLine()) != null) {
				if (line.trim().contains("description =")) {
					String descriptionText = extractDescriptionAttribute(line.trim());

					if (descriptionText != null) {
						descriptionTexts.add(descriptionText.trim());
					}
				}
			}
		}

		return descriptionTexts;
	}

	public String extractDescriptionAttribute(String methodSourceCode) {
		String regex = "@Test\\(description\\s*=\\s*\"([^\"]*)\"";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(methodSourceCode);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	@Override
	@Transactional
	public void importTestStepFromJava(MultipartFile uploadfile, int clientProjectId) throws APIExceptions, Exception {
		try {
			List<String> methodNames = extractMethodNames(uploadfile);
			List<String> descriptions = extractDescriptionTexts(uploadfile);
			methodNames.addAll(descriptions);

			// Get the TestStep based on the clientProjectId
			List<TestStep> testStepList = testStepManager.getAllTestStepRowMapper(clientProjectId);

			// Create a HashSet of lowercase and trimmed method names
			Set<String> methodSet = new HashSet<>();
			for (String methodName : methodNames) {
				methodSet.add(methodName.trim().toLowerCase());
			}

			// Loop through the test step list and update applicable flag if a match is found
			for (TestStep testStep : testStepList) {
				String testName = testStep.getName().trim().toLowerCase();
				if (methodSet.contains(testName)) {
					// Update applicable flag
					if(!testStep.isApplicable()){
						testStep.setApplicable(true);
						testStepManager.updateTestStepById(testStep, true);
					}
				}
			}
		} catch (Exception e){
			System.out.println(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public void importTestStepFeature(MultipartFile uploadfile,
			int clientProjectId) throws APIExceptions, Exception {
		updateDataSource();
		TestStepsInfoUtil testStepsInfoUtil = new TestStepsInfoUtil();
		// Fetching existing features and their scenarios in DB.
		Map<String, Map<String, ArrayList<String>>> featureDefinition = testStepsInfoUtil
				.getTestStepFeature(clientProjectId, uploadfile);

		if (null == featureDefinition || featureDefinition.size() <= 0) {
			throw new Exception("Error occured while reading the feature file");
		}
		boolean isAutomaticMappingProcessRequired = false;
		ArrayList<Object> manualVsAutomated = testStepsInfoUtil
				.readAndMapFeatureFile(uploadfile, clientProjectId,
						applicationCommonUtil.getCurrentUser());

		Set<TestCase> testCases = null;
		if (null != manualVsAutomated) {
			isAutomaticMappingProcessRequired = true;
			Map<TestCase, ArrayList<String>> manualVsAutomatedSteps = (Map<TestCase, ArrayList<String>>) (manualVsAutomated
					.get(0));
			testCases = manualVsAutomatedSteps.keySet();

			/*
			 * Get module id if hierarchy is available. This is a specific
			 * requirement for commissioning tool feature file. i.e automatic
			 * mapping. Here we are also setting module id as this is a part of
			 * hash code. Previously it was done in TestStepsInfoUtil class but
			 * as hash code generation required module id which was not
			 * available there, the code is updated and shifted here to set
			 * module id and hash code. Changes done on 8th January 2018.
			 */
			try {
				Map<TestCase, ArrayList<String>> manualVsAutomatedStepsUpdated = new LinkedHashMap<TestCase, ArrayList<String>>();
				for (TestCase testCase : testCases) {
					ArrayList<String> testStepsList = manualVsAutomatedSteps
							.get(testCase);
					Modules modules = testCaseService.createModuleHierarchy(
							testCase.getModuleNameHierarchy(), clientProjectId);
					testCase.setModuleId(modules.getModuleId());
					testCase.setHashCode(GenerateUniqueHash.getTestCaseHash(
							modules.getModuleId(), testCase.getTestCaseNo(),
							testCase.getTestSummary(),
							testCase.getPreCondition(),
							testCase.getExecutionSteps(),
							testCase.getExpectedResult()));
					manualVsAutomatedStepsUpdated.put(testCase, testStepsList);
					testStepsList = null;
					modules = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				manualVsAutomatedSteps = null;
			}
		}

		String featureFileName = featureDefinition.keySet().iterator().next()
				.toString();
		Map<String, ArrayList<String>> scenarioSteps = featureDefinition
				.get(featureFileName);

		/*
		 * Deleting existing mapping, step sequence and scenario-feature for
		 * latest feature file. Added for handling the edited test steps in a
		 * feature file
		 */
		cleanExistingData(clientProjectId, featureFileName,
				scenarioSteps.keySet(), isAutomaticMappingProcessRequired,
				testCases);

		/* Get all scenarios hashcode with their unique ID from DB */
		Map<String, Integer> testScenariosHashCode = testScenariosManager
				.getTestScenarioHashCode(clientProjectId, featureFileName);
		if (null == testScenariosHashCode) {
			testScenariosHashCode = new LinkedHashMap<String, Integer>();
		}

		/* Get all existing test step hash code with their ID from DB */
		Map<String, Long> testStepsHashCode = testStepManager
				.getTestStepHashCode();
		if (null == testStepsHashCode) {
			testStepsHashCode = new LinkedHashMap<String, Long>();
		}

		ArrayList<String> scenarioStepList = new ArrayList<String>();
		String testScenarioHashCode = null;
		Integer scenarioId = 0;

		TestScenarios testScenarios = new TestScenarios();
		TestStep testStep = new TestStep();
		TestScenarioStep testScenarioStep = new TestScenarioStep();

		int scenarioSequence = 1;
		StringJoiner featureVersionInfo = new StringJoiner(",");
		for (Entry<String, ArrayList<String>> valuesEntry : scenarioSteps
				.entrySet()) {
			/*
			 * Scenario Name : Scenario Description + Symbol + Scenario Tag +
			 * Symbol + Is Feature flag + Symbol + Is Background Scenario flag +
			 * Symbol + Is Scenario Outline flag
			 */
			testScenarios.setName(valuesEntry.getKey()
					.split(PintailerConstants.READ_FILE_OBJECT_SEPARATOR)[0]);
			testScenarios.setScenarioTag(valuesEntry.getKey()
					.split(PintailerConstants.READ_FILE_OBJECT_SEPARATOR)[1]);
			testScenarios.setFeature(Boolean.parseBoolean(valuesEntry.getKey()
					.split(PintailerConstants.READ_FILE_OBJECT_SEPARATOR)[2]));
			testScenarios.setBackground(
					Boolean.parseBoolean(valuesEntry.getKey().split(
							PintailerConstants.READ_FILE_OBJECT_SEPARATOR)[3]));
			testScenarios.setScenarioOutline(
					Boolean.parseBoolean(valuesEntry.getKey().split(
							PintailerConstants.READ_FILE_OBJECT_SEPARATOR)[4]));

			testScenarios.setFeatureFileName(featureFileName);
			testScenarios.setCreatedBy(applicationCommonUtil.getCurrentUser());
			testScenarios.setModifiedBy(applicationCommonUtil.getCurrentUser());
			testScenarioHashCode = GenerateUniqueHash.getFeatureScenarioHash(
					clientProjectId, testScenarios.getFeatureFileName(),
					testScenarios.getName());
			testScenarios.setHashCode(testScenarioHashCode);
			testScenarios.setClientProjectId(clientProjectId);

			testScenarios.setScenarioSequence(scenarioSequence);
			testScenarios.setScenarioLatestVersion("V1");
			testScenarios.setScenarioSelectedVersion("V1");

			/*
			 * If exist, get the test scenario ID from the map and if not exist,
			 * than insert and return the new scenario id
			 */
			if (!testScenariosHashCode.keySet()
					.contains(testScenarioHashCode)) {
				scenarioId = testScenariosManager
						.persistTestScenarios(testScenarios)
						.getTestScenarioId();
				testScenariosHashCode.put(testScenarioHashCode, scenarioId);
			} else {
				scenarioId = testScenariosHashCode.get(testScenarioHashCode);
				/* fetching the scenario based on found scenario id */
				TestScenarios testScenariosById = testScenariosManager
						.getTestScenariosById(scenarioId, null);
				testScenariosById.setDeleted(false);
				testScenariosById.setScenarioSequence(scenarioSequence);
				testScenariosById
						.setModifiedBy(applicationCommonUtil.getCurrentUser());
				testScenariosManager.updateTestScenariosById(testScenariosById);

				testScenarios = testScenariosManager
						.getTestScenariosById(scenarioId, "false");
			}

			scenarioStepList = valuesEntry.getValue();
			if (null == scenarioStepList) {
				scenarioSequence++;
				continue;
			}

			long testStepId = 0l;

			// For version info save
			StringBuilder testStepVersionInfo = new StringBuilder();
			boolean firstTimeFlg = true;
			for (String step : scenarioStepList) {
				/*
				 * Step : Step Sequence + Symbol + Step Definition Hash code +
				 * Symbol + Step Definition with Keyword
				 */
				final String[] stepInfoArr = step
						.split(PintailerConstants.READ_FILE_OBJECT_SEPARATOR);
				int testStepSequence = Integer.parseInt(stepInfoArr[0]);
				String testStepHashCode = stepInfoArr[1];

				testStepId = testStepsHashCode.get(testStepHashCode) == null ? 0
						: testStepsHashCode.get(testStepHashCode);
				if (testStepId == 0) {
					testStep.setName(stepInfoArr[3]);
					testStep.setHashCode(testStepHashCode);
					testStep.setCreatedBy(
							applicationCommonUtil.getCurrentUser());
					testStep.setModifiedBy(
							applicationCommonUtil.getCurrentUser());
					testStep.setClientProjectId(clientProjectId);
					testStep.setStepLatestVersion("V1");
					testStep.setStepSelectedVersion("V1");
					testStepId = testStepManager.persistTestStep(testStep)
							.getTestStepId();
					testStepsHashCode.put(testStepHashCode, testStepId);
				} else {
					testStep = testStepManager.getTestStepById(testStepId,
							clientProjectId);
					testStep.setStepSelectedVersion(
							testStep.getStepLatestVersion());
				}

				// saving data in test scenarios step
				// if (!testScenarioStepManager.isDataExist(clientDetails
				// .getProject().getClientOrganization(), clientDetails
				// .getProject().getClientProjectId(), scenarioId,
				// testStepId, testStepSequence)) {
				testScenarioStep.setTestScenarioId(scenarioId);
				testScenarioStep.setTestStepSequence(testStepSequence);
				testScenarioStep.setTestStepId(testStepId);
				testScenarioStep.setStepKeyword(stepInfoArr[2]);
				testScenarioStep
						.setCreatedBy(applicationCommonUtil.getCurrentUser());
				testScenarioStep
						.setModifiedBy(applicationCommonUtil.getCurrentUser());

				TestScenarioStep existingTestScenarioStep = testScenarioStepManager
						.isDataExist(clientProjectId, scenarioId, testStepId);
				if (null == existingTestScenarioStep) {
					if (null == testScenarioStepManager
							.persistTestScenarioStep(testScenarioStep)) {
						log.error(
								"Some error occured while storing test scenario and step sequence");
						throw new APIExceptions(
								"Some error occured while storing test scenario and step sequence");
					}
				} else {
					if (existingTestScenarioStep
							.getTestStepSequence() != testStepSequence) {
						existingTestScenarioStep
								.setTestStepSequence(testStepSequence);
						testScenarioStepManager.updateTestScenarioStepById(
								existingTestScenarioStep);
					}
				}

				if (!firstTimeFlg) {
					testStepVersionInfo.append(",");
				}
				testStepVersionInfo.append(applicationCommonUtil.concatString(
						"-", "" + testStepId,
						ValueValidations
								.isValueValid(testStep.getStepSelectedVersion())
										? testStep.getStepSelectedVersion()
										: testStep.getStepLatestVersion(),
						"" + testStepSequence, stepInfoArr[2]));
				firstTimeFlg = false;
				// }
			}
			testScenariosService.saveScenarioStepVersionInfo(
					testScenarios.getTestScenarioId(),
					testScenarios.getHashCode(),
					testScenarios.getScenarioSelectedVersion(),
					testStepVersionInfo.toString());

			List<TestScenarioStepVersion> scenarioStepMappingVersion = testScenarioStepManager
					.getScenarioStepMappingVersion(testScenarios.getHashCode(),
							Integer.parseInt(
									testScenarios.getScenarioSelectedVersion()
											.replace("V", "").trim()));
			featureVersionInfo.add(applicationCommonUtil.concatString("-",
					"" + testScenarios.getTestScenarioId(),
					testScenarios.getHashCode(),
					ValueValidations.isValueValid(
							testScenarios.getScenarioSelectedVersion())
									? testScenarios.getScenarioSelectedVersion()
									: testScenarios.getScenarioLatestVersion(),
					!(null == scenarioStepMappingVersion
							|| scenarioStepMappingVersion.isEmpty())
									? scenarioStepMappingVersion.get(0)
											.getTestScenariosStepVersion()
									: "V1"));

			scenarioSequence++;
		}

		featureManagementService.saveFeatureVersionInfo(clientProjectId,
				featureFileName, featureVersionInfo.toString());

		if (isAutomaticMappingProcessRequired) {
			testCaseMapService.automaticMap(manualVsAutomated, clientProjectId);
		} else {
			cleanExistingTestMapping(clientProjectId, featureFileName);
		}

		testScenarios = null;
		testStep = null;
		testScenarioStep = null;
		testStepsInfoUtil = null;
	}

	@Transactional
	private void cleanExistingData(int clientProjectId, String featureFileName,
			Set<String> scenarios, boolean isAutomaticMappingProcessRequired,
			Set<TestCase> testCases) throws APIExceptions {
		Set<String> scenarioNames = new LinkedHashSet<String>();
		for (String scenarioName : scenarios) {
			/*
			 * Scenario Name : Scenario Description + Symbol + Scenario Tag +
			 * Symbol + Is Feature flag + Symbol + Is Background Scenario flag +
			 * Symbol + Is Scenario Outline flag
			 */
			scenarioNames.add(scenarioName
					.split(PintailerConstants.READ_FILE_OBJECT_SEPARATOR)[0]);
		}

		if (!isAutomaticMappingProcessRequired) {
			/*
			 * fetching the existing list of scenarios from the db in case
			 * automatic mapping is not available
			 */
			List<TestScenarios> testScenariosByFeatureName = testScenariosManager
					.getTestScenariosByFeatureName(clientProjectId,
							featureFileName);
			/*
			 * Deleting all the data for the scenarios which are no longer
			 * available in the latest imported feature file
			 */
			for (TestScenarios testScenarios : testScenariosByFeatureName) {
				int testScenarioId = testScenariosManager
						.getTestScenarioIdByScenarioAndFeatureFile(
								clientProjectId, testScenarios.getName(),
								featureFileName);
				if (!scenarioNames.contains(testScenarios.getName())) {

					testCaseMapManager.deleteTestCaseMapByScenarioId(
							testScenarioId, 0, 0);
					testScenarioStepManager
							.deleteTestScenarioStepByScenarioId(testScenarioId);
//					testScenariosManager
//							.deleteTestScenariosById(testScenarioId);
					testScenarios.setDeleted(true);
					testScenariosManager.updateTestScenariosById(testScenarios);
				} else {
					testScenarioStepManager
							.deleteTestScenarioStepByScenarioId(testScenarioId);
				}
			}
		} else {
			List<String> existingMappedTestCaseHash = new ArrayList<String>();
			List<Integer> idsVerified = new ArrayList<Integer>();
			for (String scenarioName : scenarioNames) {
				int testScenarioId = testScenariosManager
						.getTestScenarioIdByScenarioAndFeatureFile(
								clientProjectId, scenarioName, featureFileName);

				if (testScenarioId <= 0) {
					continue;
				}

				/*
				 * Retrieving the list of existing mapped test cases to make
				 * them disable if they are updated or removed from latest
				 * feature file
				 */
				List<TestCaseMap> testCaseMaps = testCaseMapManager
						.getInfo(clientProjectId, null, testScenarioId, 0);
				for (TestCaseMap testCaseMap : testCaseMaps) {
					/*
					 * To avoid execution of queries if the test case id already
					 * queried the queried ids are added in idsVerified list
					 */
					if (null != testCases && !idsVerified
							.contains(testCaseMap.getTestCaseId())) {
						idsVerified.add(testCaseMap.getTestCaseId());

						String hashCode = testCaseManager
								.getTestCaseById(testCaseMap.getTestCaseId(),
										null)
								.getHashCode();
						if (null == hashCode
								|| hashCode.equalsIgnoreCase("null")
								|| hashCode.equals("")) {
							throw new APIExceptions("Required information is "
									+ "missing to process the existing mapped test cases. "
									+ "Application will behave unexpectedly.");
						}
						if (!existingMappedTestCaseHash.contains(hashCode)) {
							existingMappedTestCaseHash.add(hashCode);
						}
					}
				}

				testCaseMapManager.deleteTestCaseMapByScenarioId(testScenarioId,
						0, 0);
				testScenarioStepManager
						.deleteTestScenarioStepByScenarioId(testScenarioId);
//				testScenariosManager.deleteTestScenariosById(testScenarioId);
				TestScenarios testScenarios = testScenariosManager
						.getTestScenariosById(testScenarioId, null);
				testScenarios.setDeleted(true);
				testScenariosManager.updateTestScenariosById(testScenarios);
			}

			/* Getting hash of test cases in the latest feature file */
			if (null == testCases) {
				return;
			}
			List<String> existingTestCaseHash = new ArrayList<String>();
			for (TestCase testCase : testCases) {
				if (!existingTestCaseHash.contains(testCase.getHashCode())) {
					existingTestCaseHash.add(testCase.getHashCode());
				}
			}
			/*
			 * disabling the test cases which are no longer available in the
			 * latest feature file
			 */
			for (String hash : existingMappedTestCaseHash) {
				if (!existingTestCaseHash.contains(hash)) {
					TestCase testCase = testCaseManager.getTestCaseByHash(hash,
							false, false);
					testCase.setApplicable(false);
					testCaseManager.updateTestCaseById(testCase);
				}
			}
		}
	}

	/**
	 * This method with delete the mappings which are no longer valid after
	 * latest feature file is imported. It will look for the steps in the latest
	 * feature file and were mapped previously. If step not found, the
	 * corresponding mapping will be deleted
	 */
	@Transactional
	private void cleanExistingTestMapping(int clientProjectId,
			String featureFileName) throws APIExceptions {
		/* fetching all the test scenarios of the given feature file */
		List<TestScenarios> testScenariosByFeatureName = testScenariosManager
				.getTestScenariosByFeatureName(clientProjectId,
						featureFileName);
		for (TestScenarios testScenarios : testScenariosByFeatureName) {
			/*
			 * Fetching all the existing mappings of the test scenarios which
			 * were available in the previous imports of the feature file (If
			 * not getting imported for the first time)
			 */
			List<TestCaseMap> existingMappings = testCaseMapManager.getInfo(
					clientProjectId, null, testScenarios.getTestScenarioId(),
					0);
			if (null == existingMappings || existingMappings.size() == 0) {
				/*
				 * File is imported for the first time..no mapping exists in db
				 */
				continue;
			}
			/*
			 * Updated List of test steps in the given test scenario in sequence
			 * after the latest import of feature file
			 */
			List<TestScenarioStep> testStepIdsInScenario = testScenarioStepManager
					.getTestStepIdByScenarioId(clientProjectId,
							testScenarios.getTestScenarioId());
			/*
			 * comparing one by one, if the test step in the mapping is
			 * available in the latest test step sequence of the scenario
			 */
			for (TestCaseMap testCaseMap : existingMappings) {
				boolean isFound = false;
				for (TestScenarioStep testScenarioStep : testStepIdsInScenario) {
					if (testCaseMap.getTestStepId() == testScenarioStep
							.getTestStepId()) {
						isFound = true;
					}
				}
				if (!isFound) {
					/*
					 * deleting the test cases whose mapped test steps are not
					 * available in the latest test step sequence of the
					 * scenario
					 */
					testCaseMapManager
							.deleteTestCaseMapByTestCaseIdAndScenarioId(
									testCaseMap.getTestCaseId(),
									testScenarios.getTestScenarioId());
					log.info("Mapping is removed for test case id["
							+ testCaseMap.getTestCaseId() + "], test step id["
							+ testCaseMap.getTestStepId() + "] and scenario ["
							+ testScenarios.getName() + "] inside feature ["
							+ featureFileName + "]");
				}
			}
		}
	}

	@Override
	public Map<Long, String> getTestStepsNameByIds(String testStepIds)
			throws APIExceptions {
		Map<Long, String> result = new LinkedHashMap<Long, String>();
		List<TestStep> testStepsByIds = testStepManager
				.getTestStepsByIds(testStepIds);
		if (testStepsByIds == null || testStepsByIds.isEmpty()) {
			return null;
		}
		for (TestStep testStep : testStepsByIds) {
			result.put(testStep.getTestStepId(), testStep.getName());
		}
		return result;
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}

}
