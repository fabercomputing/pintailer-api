package com.fw.services.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fw.dao.ITestCaseMapManager;
import com.fw.dao.ITestScenarioStepManager;
import com.fw.dao.ITestScenariosManager;
import com.fw.dao.ITestStepManager;
import com.fw.domain.FeatureVersion;
import com.fw.domain.TestCaseMap;
import com.fw.domain.TestScenarioStep;
import com.fw.domain.TestScenarioStepVersion;
import com.fw.domain.TestScenarios;
import com.fw.domain.TestStep;
import com.fw.domain.TestStepVersion;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IFeatureManagementService;
import com.fw.services.ITestScenariosService;
import com.fw.services.ITestStepService;
import com.fw.utils.ApplicationCommonUtil;
import com.fw.utils.GenerateUniqueHash;
import com.fw.utils.TestStepsInfoUtil;
import com.fw.utils.ValueValidations;

@Service
public class FeatureManagementServiceImpl implements IFeatureManagementService {

	private Logger log = Logger.getLogger(FeatureManagementServiceImpl.class);

	@Autowired
	ITestScenariosManager testScenariosManager;

	@Autowired
	ITestCaseMapManager testCaseMapManager;

	@Autowired
	ITestScenarioStepManager testScenarioStepManager;

	@Autowired
	ITestStepService testStepService;

	@Autowired
	ITestStepManager testStepManagerImpl;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Autowired
	ITestScenariosService testScenariosService;

	@Autowired
	ITestStepManager testStepManager;

	@Override
	public List<TestScenarios> getEntireFeature(int clientProjectId,
			String featureName) throws APIExceptions {
		return testScenariosManager.getEntireFeature(clientProjectId,
				featureName);
	}

	@Override
	@Transactional
	public boolean updateFeature(int clientProjectId,
			List<TestScenarios> testScenariosList) throws APIExceptions {
		StringJoiner errorSteps = new StringJoiner(",");
		/* for feature version info */
		String featureFileName = null;
		StringJoiner featureVersionInfo = new StringJoiner(",");
		for (TestScenarios testScenarios : testScenariosList) {
			/* flag for version control */
			boolean isModified = false;

			testScenarios.setName(testScenarios.getName().trim());
			/* skip processing scenario which are not updated */
			String testScenariosModificationStatus = testScenarios
					.getScenarioModificationStatus().toLowerCase();

			if (testScenariosModificationStatus.equals("u")) {
				if (!ValueValidations.isValueValid(
						testScenarios.getScenarioSelectedVersion())) {
					testScenarios.setScenarioSelectedVersion(
							testScenarios.getScenarioLatestVersion());
				}
				/*
				 * fetching the test scenario and step mapping version info. the
				 * latest version will be added to the feature version details
				 * to load the accurate versions of all scenarios and steps
				 * based on the mapping version
				 */
				List<TestScenarioStepVersion> scenarioStepMappingVersion = testScenarioStepManager
						.getScenarioStepMappingVersion(
								testScenarios.getHashCode(),
								Integer.parseInt(testScenarios
										.getScenarioSelectedVersion()
										.replace("V", "").trim()));

				/* Scenario is unchanged */
				featureVersionInfo.add(applicationCommonUtil.concatString("-",
						"" + testScenarios.getTestScenarioId(),
						testScenarios.getHashCode(),
						testScenarios.getScenarioSelectedVersion(),
						!(null == scenarioStepMappingVersion
								|| scenarioStepMappingVersion.isEmpty())
										? scenarioStepMappingVersion.get(0)
												.getTestScenariosStepVersion()
										: "V1"));
				featureFileName = testScenarios.getFeatureFileName();
				continue;
			}
			isModified = true;
			int testScenarioId = 0;

			/*
			 * Removing the step sequence i.e, step scenario mapping.
			 * Irrespective of the modification status of the scenario. This
			 * will force the system to refresh the sequence of test steps every
			 * time if their any add/modification/deletion in scenario or steps.
			 */
			testScenarioStepManager.deleteTestScenarioStepByScenarioId(
					testScenarios.getTestScenarioId());

			if (testScenariosModificationStatus.equals("m")) {
				/* If the test scenario name or tags are updated */
				String temp = testScenarios.getHashCode();
				testScenarios.setHashCode(GenerateUniqueHash
						.getFeatureScenarioHash(clientProjectId,
								testScenarios.getFeatureFileName(),
								testScenarios.getName()));
				testScenariosManager.updateTestScenariosById(testScenarios);
				testScenarioId = testScenarios.getTestScenarioId();
				if (!testScenarios.getHashCode().equals(temp)) {
					/*
					 * increasing the version of the scenario by 1 to match the
					 * latest version in the DB.
					 */
					testScenarios.setScenarioLatestVersion("V" + (Integer
							.parseInt(testScenarios.getScenarioLatestVersion()
									.replace("V", "").trim())
							+ 1));
				}

				if (!ValueValidations.isValueValid(
						testScenarios.getScenarioSelectedVersion())) {
					testScenarios.setScenarioSelectedVersion(
							testScenarios.getScenarioLatestVersion());
				}
			} else if (testScenariosModificationStatus.equals("a")) {
				/* If the scenario is newly added in the feature file */
				testScenarios.setHashCode(GenerateUniqueHash
						.getFeatureScenarioHash(clientProjectId,
								testScenarios.getFeatureFileName(),
								testScenarios.getName()));
				testScenarioId = testScenariosManager
						.persistTestScenarios(testScenarios)
						.getTestScenarioId();
				testScenarios.setScenarioLatestVersion("V1");
				testScenarios.setScenarioSelectedVersion("V1");
			} else if (testScenariosModificationStatus.equals("d")) {
				/* If the scenario is removed from the feature file */
				testScenarioId = testScenarios.getTestScenarioId();

				/* Removing all the mappings of the given scenario */
				testCaseMapManager.deleteTestCaseMapByScenarioId(testScenarioId,
						0, 0);

				// Removing the scenario from the DB
//				testScenariosManager.deleteTestScenariosById(testScenarioId);
				testScenarios.setDeleted(true);
				testScenariosManager.updateTestScenariosById(testScenarios);

				testScenarioId = 0;
			}

			/* stop further process if scenario id is not available */
			if (testScenarioId == 0) {
				continue;
			}

			List<TestStep> testStepsList = testScenarios.getTestStepsList();
			String stepModificationStatus = null;
			int testStepSequence = 0;
			TestScenarioStep testScenarioStep = new TestScenarioStep();
			long testStepId = 1;

			/* For version info save */
			StringJoiner testStepVersionInfo = new StringJoiner(",");
			for (TestStep testStep : testStepsList) {
				testStep.setName(testStep.getName().trim());
				stepModificationStatus = testStep.getStepModificationStatus()
						.toLowerCase();
				testStepId = testStep.getTestStepId();
				if (!TestStepsInfoUtil.isStep(testStep.getName())) {
					errorSteps.add(testStep.getName());
					continue;
				}

				/*
				 * This is added to handle the case when added and updated step
				 * already exist in DB.
				 */
				String stepKeyword = testStep.getName()
						.substring(0, testStep.getName().indexOf(" ")).trim();
				String stepDefinition = testStep.getName()
						.substring(testStep.getName().indexOf(" ")).trim();
				testStep.setName(stepDefinition);
				String hashCode = GenerateUniqueHash
						.getTestStepHash(clientProjectId, stepDefinition);
				TestStep testStepIdByHashCode = testStepManagerImpl
						.getTestStepIdByHashCode(hashCode);
				if (stepModificationStatus.equals("m")) {
					if (null == testStepIdByHashCode) {
						/*
						 * If the test step definition is updated, than modify
						 * the hash code and definition of test step in DB
						 * without disturbing the mapping. Here, the updated
						 * step does not match with any other existing step in
						 * DB
						 */
						testStepService.updateTestStepById(testStep);
					} else {
						testStepId = testStepIdByHashCode.getTestStepId();
						/*
						 * Updating the mapping as the step is updated and now
						 * it matches the other existing test step.
						 * 
						 * Fetching the mapping for the previous step (before
						 * update)
						 */
						List<TestCaseMap> testCaseMaps = testCaseMapManager
								.getInfo(clientProjectId,
										testStep.getHashCode(), testScenarioId,
										0);
						for (TestCaseMap testCaseMap : testCaseMaps) {
							/*
							 * updating the test step id as test step name i.e.
							 * description is already updated in the request
							 * JSON.
							 */
							testCaseMap.setTestStepId(testStepId);
							testCaseMapManager
									.updateTestCaseMapById(testCaseMap);
						}
					}
				} else if (stepModificationStatus.equals("a")) {
					if (null == testStepIdByHashCode) {
						testStep.setClientProjectId(clientProjectId);
						testStepId = testStepService.addTestStep(testStep)
								.getTestStepId();
					} else {
						testStepId = testStepIdByHashCode.getTestStepId();
					}
				} else if (stepModificationStatus.equals("d")) {
					/*
					 * Delete only mapping when the step is removed as updating
					 * the applicable or delete flag will create issues while
					 * re-import the test step again. Commented following step
					 * on 3 Dec 2018
					 */
					// testStepService.updateTestStepById(testStep, false);
					testCaseMapManager.deleteTestCaseMapByScenarioId(
							testScenarioId, testStep.getTestStepId(), 0);
					testStepId = 0;
				}

				if (testStepId == 0) {
					continue;
				}

				/* fetching the step info */
				testStep = testStepService.getTestStepById(testStepId,
						clientProjectId);

				/*
				 * if the user does not select any specific version of the step,
				 * than the latest version of the step will be considered as
				 * selected
				 */
				if (!ValueValidations
						.isValueValid(testStep.getStepSelectedVersion())) {
					testStep.setStepSelectedVersion(
							testStep.getStepLatestVersion());
				}

				testScenarioStep.setTestScenarioId(testScenarioId);
				testScenarioStep.setTestStepSequence(testStepSequence);
				testScenarioStep.setTestStepId(testStepId);
				testScenarioStep.setStepKeyword(stepKeyword);
				testScenarioStep.setCreatedBy(testScenarios.getModifiedBy());
				testScenarioStep.setModifiedBy(testScenarios.getModifiedBy());

				if (null == testScenarioStepManager
						.persistTestScenarioStep(testScenarioStep)) {
					log.error(
							"Some error occured while storing test scenario and step sequence");
					throw new APIExceptions(
							"Some error occured while storing test scenario and step sequence");
				}

				testStepVersionInfo.add(applicationCommonUtil.concatString("-",
						"" + testStepId, testStep.getStepSelectedVersion(),
						"" + testStepSequence, stepKeyword));

				testStepSequence++;
			}
			if (isModified) {
				testScenariosService.saveScenarioStepVersionInfo(
						testScenarios.getTestScenarioId(),
						testScenarios.getHashCode(),
						testScenarios.getScenarioSelectedVersion(),
						testStepVersionInfo.toString());
			}

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
			featureFileName = testScenarios.getFeatureFileName();
		}
		saveFeatureVersionInfo(clientProjectId, featureFileName,
				featureVersionInfo.toString());
		updateScenarioSequenceAfterAddDelete(testScenariosList);
		if (!errorSteps.toString().trim().equals("")) {
			throw new APIExceptions("Steps [" + errorSteps
					+ "] is not provided with the essential "
					+ "Gherkin keyword. Please try again");
		}
		return true;
	}

	@Override
	public void saveFeatureVersionInfo(int clientProjectId,
			String featureFileName, String scenarioInfo) throws APIExceptions {
		FeatureVersion featureVersion = new FeatureVersion();
		featureVersion.setFeatureFileName(featureFileName);
		featureVersion.setTestScenariosHashVersionInfo(scenarioInfo);
		featureVersion.setHardDeleted(false);
		featureVersion.setClientProjectId(clientProjectId);

		List<FeatureVersion> existingfeatureVersions = testScenarioStepManager
				.getFeatureVersion(clientProjectId, featureFileName);
		if (!(null == existingfeatureVersions
				|| existingfeatureVersions.isEmpty())) {
			if (!existingfeatureVersions.get(0)
					.getTestScenariosHashVersionInfo().equals(scenarioInfo)) {
				testScenarioStepManager.persistFeatureVersion(featureVersion);
			} else {
				log.info("No change in the current version of the feature");
			}
		} else {
			testScenarioStepManager.persistFeatureVersion(featureVersion);
		}

	}

	@Transactional
	private void updateScenarioSequenceAfterAddDelete(
			List<TestScenarios> testScenariosList) throws APIExceptions {
		try {
			String currentUser = applicationCommonUtil.getCurrentUser();
			for (TestScenarios testScenarios : testScenariosList) {
				testScenarios.setModifiedBy(currentUser);
				testScenariosManager
						.updateTestScenariosSequenceById(testScenarios);
			}
		} catch (APIExceptions e) {
			String message = "Error occured while updating the scenario sequence : "
					+ e.getMessage();
			log.error(message);
			throw new APIExceptions(message);
		}
	}

	@Override
	public String downloadFeatureFile(int clientProjectId, String featureName,
			String reportFilePath) throws APIExceptions {
		final List<TestScenarios> testScenariosList = getEntireFeature(
				clientProjectId, featureName);
		/* Get the file reference */
		Path path = Paths.get(reportFilePath + File.separator
				+ testScenariosList.get(0).getFeatureFileName());

		/* Use try-with-resource to get auto-close writer instance */
		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			for (TestScenarios testScenarios : testScenariosList) {
				/*
				 * Writing the tags of the scenarios, feature and
				 * background...if available
				 */
				if (!(null == testScenarios.getScenarioTag()
						|| testScenarios.getScenarioTag().trim().equals("")
						|| testScenarios.getScenarioTag().trim()
								.equals("null"))) {
					writer.write(testScenarios.getScenarioTag().trim());
					writer.newLine();
				}

				/*
				 * Writing the keywords with description which defines scenario,
				 * feature file and background scenarios
				 */
				if (testScenarios.isFeature()) {
					writer.write("Feature: " + testScenarios.getName());
					writer.newLine();
					writer.newLine();
					continue;
				} else if (testScenarios.isBackground()) {
					writer.write("Background: " + testScenarios.getName());
				} else {
					writer.write("Scenario: " + testScenarios.getName());
				}
				writer.newLine();

				/* Writing the test steps in respective scenarios */
				List<TestStep> testStepsList = testScenarios.getTestStepsList();
				if (null == testStepsList) {
					throw new APIExceptions(
							"Test steps are not available for the given scenario ["
									+ testScenarios.getName()
									+ "]. File cannot be downloaded");
				}
				for (TestStep testStep : testStepsList) {
					String temp = testStep.getName();
					if (temp.contains("|")) {
						String[] split = temp.split("\n");
						boolean firstTime = true;
						for (String s : split) {
							if (firstTime) {
								writer.write("\t" + s);
								firstTime = false;
							} else {
								writer.write("\t\t" + s);
							}
							writer.newLine();
						}
					} else {
						writer.write("\t" + temp);
					}
					writer.newLine();
				}

				/* Adding new line at the end of the scenario */
				writer.newLine();
			}
		} catch (IOException e) {
			String message = "Error occured while creating the feature file : "
					+ e.getMessage();
			log.error(message);
			throw new APIExceptions(message);
		}

		return featureName;
	}

	@Override
	public Map<String, List<TestScenarios>> getFeatureFileVersions(
			int clientProjectId, String featureFileName) throws APIExceptions {
		Map<String, List<TestScenarios>> result = new HashMap<String, List<TestScenarios>>();
		List<FeatureVersion> featureVersionList = testScenarioStepManager
				.getFeatureVersion(clientProjectId, featureFileName);
		for (FeatureVersion featureVersion : featureVersionList) {
			String[] scenarioInfoList = featureVersion
					.getTestScenariosHashVersionInfo().split(",");
			List<TestScenarios> temp = new ArrayList<TestScenarios>();
			for (String scenarioInfo : scenarioInfoList) {
				String[] scenarioInfos = scenarioInfo.split("-");
				TestScenarios testScenariosByHashCode = testScenariosManager
						.getSpecificTestScenariosVersion(clientProjectId,
								scenarioInfos[1], Integer.parseInt(
										scenarioInfos[2].replace("V", "")));

				TestScenarioStepVersion scenarioStepMappingVersion = testScenarioStepManager
						.getScenarioStepMappingVersion(scenarioInfos[1],
								Integer.parseInt(
										scenarioInfos[2].replace("V", "")),
								Integer.parseInt(
										scenarioInfos[3].replace("V", "")));
				if (null == scenarioStepMappingVersion) {
					testScenariosByHashCode.setTestStepsList(testStepManager
							.getTestStepsByScenarioId(testScenariosByHashCode
									.getTestScenarioId()));
				} else {
					String[] stepsInfoList = scenarioStepMappingVersion
							.getTestStepIdVersionSequenceKeyword().split(",");
					List<TestStep> testStepsList = new ArrayList<TestStep>();
					for (String stepInfo : stepsInfoList) {
						String[] stepInfos = stepInfo.split("-");
						TestStep testStep = null;
						TestStepVersion testStepsVersionByStepIdAndVersionId = null;

						testStepsVersionByStepIdAndVersionId = testStepManager
								.getTestStepsVersionByStepIdAndVersionId(
										Long.parseLong(stepInfos[0]),
										Integer.parseInt(
												stepInfos[1].replace("V", "")));
						if (null == testStepsVersionByStepIdAndVersionId
								&& !stepInfos[1].equals("V1")) {
							throw new APIExceptions(
									"Details of the test steps in the scenario are not available for version ["
											+ stepInfos[1] + "]");
						} else if (null == testStepsVersionByStepIdAndVersionId
								&& stepInfos[1].equals("V1")) {
							testStep = testStepManager.getTestStepById(
									Long.parseLong(stepInfos[0]),
									clientProjectId);
							testStep.setStepSelectedVersion("V1");
						} else {

							testStep = new TestStep();
							testStep.setTestStepId(
									Long.parseLong(stepInfos[0]));
							testStep.setName(stepInfos[3] + " "
									+ testStepsVersionByStepIdAndVersionId
											.getName());
							testStep.setClientProjectId(clientProjectId);
							testStep.setHashCode(
									testStepsVersionByStepIdAndVersionId
											.getHashCode());
							testStep.setCreatedBy(
									testStepsVersionByStepIdAndVersionId
											.getCreatedBy());
							testStep.setModifiedBy(
									testStepsVersionByStepIdAndVersionId
											.getModifiedBy());
							testStep.setDeleted(
									testStepsVersionByStepIdAndVersionId
											.isDeleted());
							testStep.setStepLatestVersion(
									testStepManager
											.getTestStepById(
													Long.parseLong(
															stepInfos[0]),
													clientProjectId)
											.getStepLatestVersion());
							testStep.setStepSelectedVersion(stepInfos[1]);
						}
						testStepsList.add(testStep);
					}
					testScenariosByHashCode.setTestStepsList(testStepsList);
				}
				temp.add(testScenariosByHashCode);
			}
			result.put(featureVersion.getFeatureVersion(), temp);
		}
		return result;
	}
}
