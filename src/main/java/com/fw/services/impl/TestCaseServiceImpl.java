package com.fw.services.impl;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.csvreader.CsvReader;
import com.fw.bean.ImportTestCaseBean;
import com.fw.dao.IClientProjectsManager;
import com.fw.dao.IReleaseTestCaseMappingManager;
import com.fw.dao.ITestCaseManager;
import com.fw.dao.ITestCaseMapManager;
import com.fw.dao.ITestScenarioStepManager;
import com.fw.dao.ITestScenariosManager;
import com.fw.dao.ITestStepManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.ClientProjects;
import com.fw.domain.Modules;
import com.fw.domain.ReleaseTestCaseMapping;
import com.fw.domain.TestCase;
import com.fw.domain.TestCaseClientBean;
import com.fw.domain.TestCaseMap;
import com.fw.domain.TestCaseMapVersion;
import com.fw.domain.TestCaseVersion;
import com.fw.domain.TestScenarioStep;
import com.fw.domain.TestScenarioStepVersion;
import com.fw.domain.TestScenarios;
import com.fw.domain.TestStep;
import com.fw.exceptions.APIExceptions;
import com.fw.exceptions.EmptyDataException;
import com.fw.pintailer.constants.PintailerConstants;
import com.fw.services.IFeatureManagementService;
import com.fw.services.IModulesService;
import com.fw.services.ITestCaseMapService;
import com.fw.services.ITestCaseService;
import com.fw.services.ITestScenariosService;
import com.fw.utils.ApplicationCommonUtil;
import com.fw.utils.GenerateUniqueHash;
import com.fw.utils.LocalUtils;
import com.fw.utils.TestStepsInfoUtil;
import com.fw.utils.ValueValidations;

@Service
public class TestCaseServiceImpl implements ITestCaseService {

	private Logger log = Logger.getLogger(TestCaseServiceImpl.class);

	@Autowired
	ITestCaseManager testCaseManager;

	@Autowired
	IClientProjectsManager clientProjectsManager;

	@Autowired
	IModulesService modulesService;

	@Autowired
	ITestCaseMapService testCaseMapService;

	@Autowired
	ITestScenariosManager testScenariosManager;

	@Autowired
	ITestStepManager testStepManager;

	@Autowired
	ITestScenarioStepManager testScenarioStepManager;

	@Autowired
	ITestCaseMapManager testCaseMapManager;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Autowired
	ITestCaseService testCaseService;

	@Autowired
	ITestScenariosService testScenariosService;

	@Autowired
	ReleaseMapServiceImpl releaseMapServiceImpl;

	@Autowired
	IFeatureManagementService featureManagementService;

	@Autowired
	IReleaseTestCaseMappingManager releaseTestCaseMappingManager;

	@Override
	@Transactional
	public TestCase addTestCase(TestCase testCase) throws APIExceptions {
		if (testCase != null) {
			testCase.setHashCode(GenerateUniqueHash.getTestCaseHash(
					testCase.getModuleId(), testCase.getTestCaseNo(),
					testCase.getTestSummary(), testCase.getPreCondition(),
					testCase.getExecutionSteps(),
					testCase.getExpectedResult()));

			if (null == testCase.getTags() || testCase.getTags().isEmpty()) {
				List<String> tagsList = new ArrayList<String>();
				tagsList.add(PintailerConstants.DEFAULT_TEST_CASE_TAG);
				testCase.setTags(tagsList);
//				testCase.setTags(null);
			}
			// setting delete false as false and applicable as true if the test
			// case added but hash code matched with some test case which is not
			// visible
			TestCase testCaseByHash = testCaseManager
					.getTestCaseByHash(testCase.getHashCode(), true, true);

			if (null != testCaseByHash) {
				testCaseByHash.setDeleted(false);
				testCaseByHash.setApplicable(true);
				updateTestCaseById(testCaseByHash, 0, false);
				log.info("The test case already exist in the DB. It is active "
						+ "again in case it was set to inactive before");
				return testCase;
			}
			return testCaseManager.persistTestCase(testCase);
		} else
			return null;
	}

	/**
	 * boolean flag 'isTestCaseDataUpdated' is added to increase the version of
	 * the test case if one of the fields which define test case has been
	 * changed. This is added to avoid confusion and not creating version even
	 * if user has updated only the filename or test steps to be mapped i.e. for
	 * Portal, or deleted or applicable flag toggle
	 */
	@Override
	@Transactional
	public void updateTestCaseById(TestCase testCase, int releaseId,
			boolean isTestCaseDataUpdated) throws APIExceptions {
		if (testCase != null) {
			testCase.setHashCode(GenerateUniqueHash.getTestCaseHash(
					testCase.getModuleId(), testCase.getTestCaseNo(),
					testCase.getTestSummary(), testCase.getPreCondition(),
					testCase.getExecutionSteps(),
					testCase.getExpectedResult()));

			/*
			 * In case the tag is not given to a test case, it will be assigned
			 * to 'P3' tag
			 */
			if (null == testCase.getTags() || testCase.getTags().isEmpty()) {
				List<String> tagsList = new ArrayList<String>();
				tagsList.add(PintailerConstants.DEFAULT_TEST_CASE_TAG);
				testCase.setTags(tagsList);
			}

			Modules modules = modulesService
					.getModulesById(testCase.getModuleId());
			ClientProjects clientProjects = clientProjectsManager
					.getClientProjectsById(modules.getClientProjectsId());

			StringJoiner featureVersionInfo = new StringJoiner(",");
			int testScenarioStepVersionId = 0;
			String selectedTestStepIds = "";
			/*
			 * Handling the case when a manual test case is later automated. So
			 * test step is added based on info in file name and automated file
			 * number field and test case and test step mapping will be done
			 * accordingly. Updated on 12 Sep 2018
			 */
			if (ValueValidations.isValueValid(testCase.getFileName())
					&& ValueValidations.isValueValid(
							testCase.getAutomatedTestCaseNoFromFile())) {
				/*
				 * if file name and automated test case info is provided,
				 * proceed else if condition will not execute
				 */

				/*
				 * tempFileName contains <old File Name>:<new File Name>. This
				 * is to handle the situation if user change the file name still
				 * he will get single feature and scenario name on test case
				 * mapping page i.e. the updated name or the previous name, he
				 * will get one name. If the user is giving the information for
				 * the first time i.e. the test case is recently automated,
				 * tempFileName will contain the new file name without ":"
				 */
				String tempFileName = testCase.getFileName();
				if (tempFileName.contains(",")) {
					throw new APIExceptions(
							"Multiple file names are not allowed ["
									+ tempFileName + "]");
				}
				int testScenarioId = 0;
				TestScenarios testScenario = null;
				if (tempFileName.contains(":")) {
					String previousName = tempFileName.split(":")[0].trim();
					String newName = tempFileName.split(":")[1].trim();

					/*
					 * updating the test case object with file name set as
					 * latest file name
					 */
					testCase.setFileName(previousName);
					testCase = setFeatureAndScenarioName(testCase, false);

					/*
					 * fetching the scenario details if exists for the previous
					 * file name
					 */
					testScenario = testScenariosManager.getTestScenariosById(
							testScenariosManager
									.getTestScenarioIdByScenarioAndFeatureFile(
											clientProjects.getClientProjectId(),
											testCase.getScenarioName(),
											testCase.getFeatureName()),
							"false");
					if (null != testScenario) {
						testScenarioId = testScenario.getTestScenarioId();
					} else {
						throw new APIExceptions(
								"The feature and scenario are not available for existing file name given as["
										+ previousName + "]");
					}
					if (!previousName.equals(newName)) {
						/*
						 * updating existing scenario and feature name if
						 * previous file name does not match with latest file
						 * name
						 */
						testCase.setFileName(newName);
						testCase = setFeatureAndScenarioName(testCase, false);
						testScenario
								.setFeatureFileName(testCase.getFeatureName());
						testScenario.setName(testCase.getScenarioName());
						String temp = testScenario.getHashCode();
						testScenario.setHashCode(
								GenerateUniqueHash.getFeatureScenarioHash(
										modules.getClientProjectsId(),
										testCase.getFeatureName(),
										testCase.getScenarioName()));
						testScenario.setModifiedBy(testCase.getModifiedBy());
						testScenariosManager
								.updateTestScenariosById(testScenario);

						if (!testScenario.getHashCode().equals(temp)) {
							/*
							 * increasing the version of the scenario by 1 to
							 * match the latest version in the DB.
							 */
							testScenario
									.setScenarioLatestVersion("V" + (Integer
											.parseInt(testScenario
													.getScenarioLatestVersion()
													.replace("V", "").trim())
											+ 1));
						}

						if (!ValueValidations.isValueValid(
								testScenario.getScenarioSelectedVersion())) {
							testScenario.setScenarioSelectedVersion(
									testScenario.getScenarioLatestVersion());
						}
					}
				} else {
					/*
					 * adding new test scenario and feature name based on the
					 * new file name given in the test case update bean
					 */

					testCase = setFeatureAndScenarioName(testCase, false);

					/*
					 * Checking if the given scenario and feature exist in DB as
					 * user can map a new manual test case with existing
					 * automated test step
					 */
					testScenario = testScenariosManager.getTestScenariosById(
							testScenariosManager
									.getTestScenarioIdByScenarioAndFeatureFile(
											clientProjects.getClientProjectId(),
											testCase.getScenarioName(),
											testCase.getFeatureName()),
							"false");
					if (null != testScenario) {
						testScenarioId = testScenario.getTestScenarioId();
					} else {
						testScenario = new TestScenarios();
						testScenario.setName(testCase.getScenarioName());
						testScenario
								.setFeatureFileName(testCase.getFeatureName());
						testScenario.setClientProjectId(
								clientProjects.getClientProjectId());
						testScenario.setCreatedBy(testCase.getModifiedBy());
						testScenario.setModifiedBy(testCase.getModifiedBy());
						testScenario.setDeleted(false);
						testScenario.setHashCode(
								GenerateUniqueHash.getFeatureScenarioHash(
										modules.getClientProjectsId(),
										testCase.getFeatureName(),
										testCase.getScenarioName()));
						testScenario.setScenarioTag(
								PintailerConstants.DEFAULT_TEST_SCENARIO_TAG);
						testScenario.setScenarioLatestVersion("V1");
						testScenario.setScenarioSelectedVersion("V1");
						testScenario = testScenariosManager
								.persistTestScenarios(testScenario);
						testScenarioId = testScenario.getTestScenarioId();
					}
				}

//				TestScenarioStepVersion scenarioStepMappingVersion = testScenarioStepManager
//						.getScenarioStepMappingVersion(testScenarioId,
//								Integer.parseInt(testScenario
//										.getScenarioSelectedVersion()
//										.replace("V", ""))).get(0);
//				
//				String testStepIdVersionSequenceKeyword = scenarioStepMappingVersion.getTestStepIdVersionSequenceKeyword();

				/*
				 * Now getting details of test step if exist or creating new
				 * one. Make note that test steps can be more than one and will
				 * be comma separated
				 */
				String[] testStepsName = testCase
						.getAutomatedTestCaseNoFromFile().trim().split(",");
				long testStepId = 0;
				boolean isExistingMappingDeleted = true;
				StringBuilder testStepVersionInfo = null;
				String separator = "";
				boolean isNewStepAdded = false;
				for (int i = 0; i < testStepsName.length; i++) {
					/* Fetching all the steps for the given test scenario */
					List<TestScenarioStep> testScenarioSteps = testScenarioStepManager
							.getTestStepIdByScenarioId(
									clientProjects.getClientProjectId(),
									testScenarioId);

					boolean firstTimeFlg = true;
					testStepVersionInfo = new StringBuilder();

					String tempStepName = testStepsName[i].trim();
					String keyword = "";
					if (tempStepName.toLowerCase().startsWith("given")
							|| tempStepName.toLowerCase().startsWith("when")
							|| tempStepName.toLowerCase().startsWith("then")
							|| tempStepName.toLowerCase().startsWith("and")
							|| tempStepName.toLowerCase().startsWith("but")) {
						/* Hold the starting keyword */
						keyword = tempStepName
								.substring(0, tempStepName.indexOf(" ")).trim();

						/* Line without keyword */
						tempStepName = tempStepName
								.substring(tempStepName.indexOf(" ")).trim();
					}
					TestStep testStep = testStepManager.getTestStepIdByHashCode(
							GenerateUniqueHash.getTestStepHash(
									clientProjects.getClientProjectId(),
									tempStepName));
					if (null != testStep) {
						/* test step already exist in the DB */
						testStepId = testStep.getTestStepId();
						testStep = testStepManager.getTestStepById(testStepId,
								clientProjects.getClientProjectId());
					} else {
						/* test step has to be added in DB */
						testStep = new TestStep();
						testStep.setName(tempStepName);
						testStep.setHashCode(GenerateUniqueHash.getTestStepHash(
								clientProjects.getClientProjectId(),
								tempStepName));
						testStep.setDeleted(false);
						testStep.setCreatedBy(testCase.getModifiedBy());
						testStep.setModifiedBy(testCase.getModifiedBy());
						testStep.setClientProjectId(
								clientProjects.getClientProjectId());
						testStep.setStepLatestVersion("V1");
						testStep.setStepSelectedVersion("V1");

						testStep = testStepManager.persistTestStep(testStep);
						testStepId = testStep.getTestStepId();
					}

					/*
					 * For the current test step, now the test step and scenario
					 * will be map with proper sequence i.e. new step will be
					 * added with max sequence number for the given scenario. If
					 * step is already mapped nothing will happen. This is done
					 * to display the newly added step on the test case mapping
					 * page.
					 */
					boolean isTestStepSequenceExist = false;
					int maxSequence = 0;
					for (TestScenarioStep testScenarioStep : testScenarioSteps) {
						testStep = testStepManager.getTestStepById(
								testScenarioStep.getTestStepId(),
								clientProjects.getClientProjectId());
						if (testScenarioStep.getTestStepId() == testStepId) {
							isTestStepSequenceExist = true;
							testStep = testStepManager.getTestStepById(
									testStepId,
									clientProjects.getClientProjectId());

							selectedTestStepIds += separator
									+ testStep.getTestStepId() + "::"
									+ testStep.getStepLatestVersion();
							separator = ",";
//							break;
						} else {
							if (!isTestStepSequenceExist
									&& (maxSequence < testScenarioStep
											.getTestStepSequence())) {
								maxSequence = testScenarioStep
										.getTestStepSequence();
							}
						}

						if (!firstTimeFlg) {
							testStepVersionInfo.append(",");
						}
						testStepVersionInfo
								.append(applicationCommonUtil.concatString("-",
										"" + testStep.getTestStepId(),
										ValueValidations.isValueValid(testStep
												.getStepSelectedVersion())
														? testStep
																.getStepSelectedVersion()
														: testStep
																.getStepLatestVersion(),
										isTestStepSequenceExist
												? "" + testScenarioStep
														.getTestStepSequence()
												: "" + (maxSequence + 1),
										keyword));
						firstTimeFlg = false;

					}

					if (!isTestStepSequenceExist) {
						TestScenarioStep testScenarioStep = new TestScenarioStep();
						testScenarioStep.setTestScenarioId(testScenarioId);
						testScenarioStep.setTestStepId(testStepId);
						testScenarioStep.setTestStepSequence(maxSequence + 1);
						testScenarioStepManager
								.persistTestScenarioStep(testScenarioStep);

						selectedTestStepIds += separator
								+ testStep.getTestStepId() + "::"
								+ testStep.getStepLatestVersion();
						separator = ",";
						isNewStepAdded = true;
					}

					/*
					 * Finally Deleting the existing mapping and then adding the
					 * mapping information so that the reports will show
					 * additional count of the automated test case
					 */
					if (isExistingMappingDeleted) {
						testCaseMapManager
								.deleteTestCaseMapByTestCaseIdAndScenarioId(
										testCase.getTestCaseId(),
										testScenarioId);
						isExistingMappingDeleted = false;
					}

					TestCaseMap testCaseMap = new TestCaseMap();
					testCaseMap.setTestCaseId(testCase.getTestCaseId());
					testCaseMap.setTestStepId(testStepId);
					testCaseMap.setTestScenarioId(testScenarioId);
					testCaseMapManager.persistTestCaseMap(
							testCase.getTestCaseId(), "" + testStepId,
							testScenarioId);
				}

				// If user added new step mapping, the step will be added in the
				// scenario and feature and version data will be stored. But in
				// case the user deleted the mapped step, the step will still
				// remain in the feature file and scenario. If user wants to
				// permanent delete it, he has to go the edit feature file page
				// to delete the step
				List<TestScenarioStepVersion> scenarioStepMappingVersion = null;
				if (isNewStepAdded) {
					// creating the new scenario and step mapping version
					testScenariosService.saveScenarioStepVersionInfo(
							testScenario.getTestScenarioId(),
							testScenario.getHashCode(),
							testScenario.getScenarioSelectedVersion(),
							testStepVersionInfo.toString());

					// Creating the new feature version
					scenarioStepMappingVersion = testScenarioStepManager
							.getScenarioStepMappingVersion(
									testScenario.getTestScenarioId(),
									Integer.parseInt(testScenario
											.getScenarioSelectedVersion()
											.replace("V", "").trim()));
					featureVersionInfo.add(applicationCommonUtil.concatString(
							"-", "" + testScenario.getTestScenarioId(),
							testScenario.getHashCode(),
							ValueValidations.isValueValid(
									testScenario.getScenarioSelectedVersion())
											? testScenario
													.getScenarioSelectedVersion()
											: testScenario
													.getScenarioLatestVersion(),
							!(null == scenarioStepMappingVersion
									|| scenarioStepMappingVersion.isEmpty())
											? scenarioStepMappingVersion.get(0)
													.getTestScenariosStepVersion()
											: "V1"));

					featureManagementService.saveFeatureVersionInfo(
							clientProjects.getClientProjectId(),
							testCase.getFileName(),
							featureVersionInfo.toString());
				} else {
					scenarioStepMappingVersion = testScenarioStepManager
							.getScenarioStepMappingVersion(
									testScenario.getTestScenarioId(),
									Integer.parseInt(testScenario
											.getScenarioSelectedVersion()
											.replace("V", "").trim()));
				}

				testScenarioStepVersionId = scenarioStepMappingVersion.get(0)
						.getTestScenarioStepVersionId();

				testCase.setAutomatable(true);
			} else {
				/*
				 * This code is added on 25 Sep 2018, to delete the test case
				 * and test step mapping in case the user remove the name of the
				 * file and automated test case info. System will take this as
				 * the test case is no longer automated although it will not
				 * change its automatable status
				 */
				String tempFileName = testCase.getFileName();
				if (ValueValidations.isValueValid(tempFileName)) {
					String previousName = tempFileName.split(":")[0].trim();

					/*
					 * updating the test case obj with file name set as latest
					 * file name
					 */
					testCase.setFeatureName(previousName);
					testCase = setFeatureAndScenarioName(testCase, false);

					/*
					 * fetching the scenario details if exists for the previous
					 * file name
					 */
					TestScenarios testScenario = testScenariosManager
							.getTestScenariosById(testScenariosManager
									.getTestScenarioIdByScenarioAndFeatureFile(
											clientProjects.getClientProjectId(),
											testCase.getScenarioName(),
											testCase.getFeatureName()),
									"false");
					if (null != testScenario) {
						testCaseMapManager
								.deleteTestCaseMapByTestCaseIdAndScenarioId(
										testCase.getTestCaseId(),
										testScenario.getTestScenarioId());
						testScenarioStepVersionId = 0;
						selectedTestStepIds = PintailerConstants.TEST_CASE_STEP_NO_MAPPING;
					}
				}
				testCase.setFileName(null);
				testCase.setAutomatedTestCaseNoFromFile(null);
			}

			TestCaseMapVersion testCaseMapVersion = new TestCaseMapVersion();
			testCaseMapVersion.setTestCaseId(testCase.getTestCaseId());
			TestCase testCaseById = testCaseManager
					.getTestCaseById(testCase.getTestCaseId(), "false");
			if (isTestCaseDataUpdated) {
				testCaseMapVersion.setTestCaseVersionId(Integer.parseInt(
						testCaseById.getLatestVersion().replace("V", "")) + 1);
			} else {
				testCaseMapVersion.setTestCaseVersionId(Integer.parseInt(
						testCaseById.getLatestVersion().replace("V", "")));
			}
			testCaseMapVersion
					.setTestScenarioStepVersionId(testScenarioStepVersionId);
			testCaseMapVersion
					.setSelectedTestStepsIdAndVersion(selectedTestStepIds);
			testCaseMapVersion = testCaseMapManager
					.persistTestCaseMapVersion(testCaseMapVersion);
			if (null == testCaseMapVersion) {
				throw new APIExceptions(
						"Error occured while storing the auto map version "
								+ "info while importing the feature file.");
			}

			// Adding release test case mapping version info if release id
			// is provided
			if (releaseId > 0) {
				List<ReleaseTestCaseMapping> releaseTestCaseMappings = null;
				if (isTestCaseDataUpdated) {
					releaseTestCaseMappings = releaseTestCaseMappingManager
							.getReleaseTestCaseMapping(
									clientProjects.getClientProjectId(),
									releaseId, testCaseById.getTestCaseId(),
									(Integer.parseInt(
											testCaseById.getLatestVersion()
													.replace("V", ""))
											+ 1));
				} else {
					releaseTestCaseMappings = releaseTestCaseMappingManager
							.getReleaseTestCaseMapping(
									clientProjects.getClientProjectId(),
									releaseId, testCaseById.getTestCaseId(),
									(Integer.parseInt(
											testCaseById.getLatestVersion()
													.replace("V", ""))));
				}
				if (null == releaseTestCaseMappings
						|| releaseTestCaseMappings.isEmpty()) {
					throw new APIExceptions(
							"The given test case and its version "
									+ "is not mapped to the given release. First mapped "
									+ "the test case and its version to release on release "
									+ "mapping page and try here again later.");
				}
//				List<Integer> releaseIdList = releaseTestCaseMappings
//						.stream().map(e -> e.getReleaseId()).distinct()
//						.collect(Collectors.toList());
//				for (int releaseId : releaseIdList) {
				ReleaseTestCaseMapping releaseTestCaseMapping = new ReleaseTestCaseMapping();
				releaseTestCaseMapping.setClientProjectId(
						clientProjects.getClientProjectId());
				releaseTestCaseMapping.setDeleted(false);
				releaseTestCaseMapping.setReleaseId(releaseId);
				releaseTestCaseMapping
						.setTestCaseId(testCaseById.getTestCaseId());
				if (isTestCaseDataUpdated) {
					releaseTestCaseMapping
							.setTestCaseVersionId(Integer.parseInt(testCaseById
									.getLatestVersion().replace("V", "")) + 1);
				} else {
					releaseTestCaseMapping
							.setTestCaseVersionId(Integer.parseInt(testCaseById
									.getLatestVersion().replace("V", "")));
				}
				releaseTestCaseMapping.setTestCaseMapVersionId(
						testCaseMapVersion.getTestCaseMapVersionId());
				if (null == releaseTestCaseMappingManager
						.persistReleaseTestCaseMapping(
								releaseTestCaseMapping)) {
					throw new APIExceptions("Error occured while storing "
							+ "the release and test case mapping version info");
				}
//				}
			}

			testCaseManager.updateTestCaseById(testCase);
		}

	}

	@Override
	@Transactional
	public ResponseEntity<Void> deleteTestCaseById(int testCaseId)
			throws APIExceptions {
		testCaseManager.deleteTestCaseById(testCaseId);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Override
	public List<TestCase> getAllTestCases(int clientProjectId, int releaseId,
			String moduleIds, String tags, String applicable,
			String testCaseIds, String searchTxt, String sortByColumn,
			String ascOrDesc, int limit, int pageNumber, String startDate,
			String endDate, String isDeleted) throws APIExceptions {
		return testCaseManager.getAllTestCases(clientProjectId, releaseId,
				moduleIds, tags, applicable, testCaseIds, searchTxt,
				sortByColumn, ascOrDesc, limit, pageNumber, startDate, endDate,
				isDeleted);
	}

	@Override
	public TestCase getTestCaseById(int testCaseId, String isDeleted)
			throws APIExceptions {
		return testCaseManager.getTestCaseById(testCaseId, isDeleted);
	}

	@Override
	@Transactional
	public List<TestCase> importTestCase(MultipartFile uploadfile,
			int clientProjectId)
			throws APIExceptions, NullPointerException, IOException {
		updateDataSource();
		List<TestCase> duplicateList;
		List<ImportTestCaseBean> importTestCaseBeanList;
		List<TestCase> actualList;
		try {
			importTestCaseBeanList = processCSVFile(uploadfile);
			actualList = createHierarchy(importTestCaseBeanList,
					clientProjectId);
			duplicateList = new ArrayList<TestCase>();
			Map<String, Integer> testCaseHashCode = testCaseManager
					.getTestCaseHashCode(true, false, false);
			if (null == testCaseHashCode) {
				testCaseHashCode = new LinkedHashMap<String, Integer>();
			}
			for (TestCase testCase : actualList) {
				if (!testCaseHashCode.keySet()
						.contains(testCase.getHashCode())) {
					TestCase tc = testCaseManager.persistTestCase(testCase);
					testCaseHashCode.put(tc.getHashCode(), tc.getTestCaseId());
				} else {
					TestCase testCaseByHash = testCaseManager.getTestCaseByHash(
							testCase.getHashCode(), true, false);
					if (testCaseByHash.isDeleted()) {
						testCaseByHash
								.setComments("The test case can not be import "
										+ "as it was deleted previously. To "
										+ "restore it, contact the system admin");
					} else if (!testCaseByHash.isApplicable()) {
						testCaseByHash.setComments(
								"The test case can not be import as it "
										+ "already exists in the system and is currently "
										+ "not applicable. Update its applicable status "
										+ "to use it");
					} else {
						testCaseByHash.setComments(
								"The test case can not be import as it "
										+ "already exists in the system");
					}
					duplicateList.add(testCaseByHash);
				}
			}
		} catch (APIExceptions e) {
			// throw new APIExceptions(LocalUtils.getStringLocale(
			// "fw_test_mgmt_locale", "DuplicateTestCaseList"));
			throw new APIExceptions(e.getMessage());
		}

		// Those rows which are automated and test case no is provided will be
		// automatically mapped
		createScenarioAndStepSequence(actualList, clientProjectId);
		TestStepsInfoUtil testStepsInfoUtil = new TestStepsInfoUtil();
		testCaseMapService.automaticMap(
				testStepsInfoUtil.convertCSVObjectForAutomationMapping(
						clientProjectId, actualList,
						applicationCommonUtil.getCurrentUser()),
				clientProjectId);
		return duplicateList;
	}

	@Override
	@Transactional
	public List<TestCase> importTestCaseNew(MultipartFile uploadfile,
										 int clientProjectId)
			throws APIExceptions, NullPointerException, IOException {
		updateDataSource();
		List<TestCase> duplicateList;
		List<ImportTestCaseBean> importTestCaseBeanList;
		List<TestCase> actualList;
		try {
			importTestCaseBeanList = processCSVFileNew(uploadfile);
			actualList = createHierarchy(importTestCaseBeanList,
					clientProjectId);
			duplicateList = new ArrayList<TestCase>();
			Map<String, Integer> testCaseHashCode = testCaseManager
					.getTestCaseHashCode(true, false, false);
			if (null == testCaseHashCode) {
				testCaseHashCode = new LinkedHashMap<String, Integer>();
			}
			for (TestCase testCase : actualList) {
				if (!testCaseHashCode.keySet()
						.contains(testCase.getHashCode())) {
					TestCase tc = testCaseManager.persistTestCase(testCase);
					testCaseHashCode.put(tc.getHashCode(), tc.getTestCaseId());
				} else {
					TestCase testCaseByHash = testCaseManager.getTestCaseByHash(
							testCase.getHashCode(), true, false);
					if (testCaseByHash.isDeleted()) {
						testCaseByHash
								.setComments("The test case can not be import "
										+ "as it was deleted previously. To "
										+ "restore it, contact the system admin");
					} else if (!testCaseByHash.isApplicable()) {
						testCaseByHash.setComments(
								"The test case can not be import as it "
										+ "already exists in the system and is currently "
										+ "not applicable. Update its applicable status "
										+ "to use it");
					} else {
						testCaseByHash.setComments(
								"The test case can not be import as it "
										+ "already exists in the system");
					}
					duplicateList.add(testCaseByHash);
				}
			}
		} catch (APIExceptions e) {
			// throw new APIExceptions(LocalUtils.getStringLocale(
			// "fw_test_mgmt_locale", "DuplicateTestCaseList"));
			throw new APIExceptions(e.getMessage());
		}

		// Those rows which are automated and test case no is provided will be
		// automatically mapped
		createScenarioAndStepSequenceNew(actualList, clientProjectId);
		TestStepsInfoUtil testStepsInfoUtil = new TestStepsInfoUtil();
		testCaseMapService.automaticMap(
				testStepsInfoUtil.convertCSVObjectForAutomationMapping(
						clientProjectId, actualList,
						applicationCommonUtil.getCurrentUser()),
				clientProjectId);
		return duplicateList;
	}

	// As CSV file does not have actual features and scenarios as in
	// Cucumber Feature file, so CSV filename will be considered as
	// feature file name and scenario name is a subset of it. All the test step
	// numbers given in csv column will be considered as automated test steps
	// and will be
	// arranged in sequence in single scenario which is a subset of feature file
	// name
	@Transactional
	private void createScenarioAndStepSequence(List<TestCase> actualList,
			int clientProjectId) throws APIExceptions {
		// Get all existing test step hash code with their ID from DB
		Map<String, Long> testStepsHashCode = testStepManager
				.getTestStepHashCode();
		if (null == testStepsHashCode) {
			testStepsHashCode = new LinkedHashMap<String, Long>();
		}

		int testStepSequence = 1;
		long testStepId = 0l;
		int scenarioId = 0;

		int scenarioSequence = 1;
		String currentUser = applicationCommonUtil.getCurrentUser();
		String featureFileName = null;
		Map<Integer, Map<TestScenarios, List<TestStep>>> resultMap = new LinkedHashMap<Integer, Map<TestScenarios, List<TestStep>>>();
		for (TestCase testCase : actualList) {
			if (!testCase.isAutomaticMappingAvailable()) {
				continue;
			}

			// If exist, get the test scenario ID from the map and if not
			// exist, than insert and return the new scenario id
			featureFileName = testCase.getFeatureName();
			if(!ValueValidations.isValueValid(featureFileName)){
				// file name is not provided in the file
				continue;
			}
			scenarioId = testScenariosManager
					.getTestScenarioIdByScenarioAndFeatureFile(clientProjectId,
							testCase.getScenarioName(), featureFileName);

			TestScenarios testScenarios = new TestScenarios();
			testScenarios.setName(testCase.getScenarioName());
			testScenarios.setFeatureFileName(featureFileName);
			testScenarios.setModifiedBy(currentUser);
			String testScenarioHashCode = GenerateUniqueHash
					.getFeatureScenarioHash(clientProjectId,
							testScenarios.getFeatureFileName(),
							testScenarios.getName());
			testScenarios.setHashCode(testScenarioHashCode);
			testScenarios.setClientProjectId(clientProjectId);
			testScenarios.setScenarioSequence(scenarioSequence);
			testScenarios.setScenarioTag("");
			testScenarios.setScenarioLatestVersion("V1");
			testScenarios.setScenarioSelectedVersion("V1");

			if (scenarioId == 0) {
				testScenarios.setCreatedBy(currentUser);
				scenarioId = testScenariosManager
						.persistTestScenarios(testScenarios)
						.getTestScenarioId();
			} else {
				testScenarios = testScenariosManager
						.getTestScenariosById(scenarioId, null);
				testScenarios.setDeleted(false);
				testScenariosManager
						.updateTestScenariosSequenceById(testScenarios);
				testScenarios = testScenariosManager
						.getTestScenariosById(scenarioId, "false");
			}

			String temp = testCase.getAutomatedTestCaseNoFromFile();
			String[] split = temp.split(",");
			TestStep testStep = new TestStep();
			List<TestStep> testStepsList = new ArrayList<TestStep>();
			for (String step : split) {
				step = step.trim();
				testStep.setName(step);
				String testStepHashCode = GenerateUniqueHash
						.getTestStepHash(clientProjectId, step);
				testStep.setHashCode(testStepHashCode);
				testStep.setCreatedBy(currentUser);
				testStep.setModifiedBy(currentUser);
				testStep.setClientProjectId(clientProjectId);
				if (!testStepsHashCode.keySet().contains(testStepHashCode)) {
					testStep.setStepLatestVersion("V1");
					testStep.setStepSelectedVersion("V1");
					testStepId = testStepManager.persistTestStep(testStep)
							.getTestStepId();
					testStepsHashCode.put(testStepHashCode, testStepId);
				} else {
					testStepId = testStepsHashCode.get(testStepHashCode);
					testStep = testStepManager.getTestStepById(testStepId,
							clientProjectId);
					testStep.setStepSelectedVersion(
							testStep.getStepLatestVersion());
				}

				// saving data in test scenarios step
				TestScenarioStep testScenarioStep = new TestScenarioStep();
				TestScenarioStep existingTestScenarioStep = testScenarioStepManager
						.isDataExist(clientProjectId, scenarioId, testStepId);
				if (null == existingTestScenarioStep) {
					testScenarioStep.setTestScenarioId(scenarioId);
					testScenarioStep.setTestStepSequence(testStepSequence);
					testScenarioStep.setTestStepId(testStepId);
					testScenarioStep.setCreatedBy(currentUser);
					testScenarioStep.setModifiedBy(currentUser);
					if (null == testScenarioStepManager
							.persistTestScenarioStep(testScenarioStep)) {
						log.error(
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
				testStepSequence++;
				testStepsList.add(testStep);
			}
			scenarioSequence++;
			/*
			 * creating the map with structure as Key: scenarioId and Value: Map
			 * with key:testSenario and list of test steps. This will be used
			 * after this loop tp store the version info
			 */
			if (null == resultMap.get(scenarioId)) {
				Map<TestScenarios, List<TestStep>> value = new LinkedHashMap<TestScenarios, List<TestStep>>();
				value.put(testScenarios, testStepsList);
				resultMap.put(scenarioId, value);
			} else {
				Map<TestScenarios, List<TestStep>> value = resultMap
						.get(scenarioId);
				Set<TestScenarios> keySet = value.keySet();
				List<TestStep> list = null;
				for (TestScenarios sc : keySet) {
					list = value.get(sc);
				}
				list.addAll(testStepsList);
				/*
				 * As scenario is is same, inner map is replaced with key as
				 * scenario and value as latest list of test steps.
				 */
				value.replace(testScenarios, list);
				resultMap.put(scenarioId, value);
			}
		}
		
		if(ValueValidations.isValueNull(featureFileName)){
			// no file name is provided for any test cases in the import file
			return;
		}

		StringJoiner featureVersionInfo = new StringJoiner(",");
		// For version info save
		StringBuilder testStepVersionInfo = new StringBuilder();
		boolean firstTimeFlg = true;

		/* executing the loop and fetching the info of each scenario ID */
		for (Entry<Integer, Map<TestScenarios, List<TestStep>>> valuesEntryScenarioIdScenarioStepList : resultMap
				.entrySet()) {
			Map<TestScenarios, List<TestStep>> scenarioStepList = valuesEntryScenarioIdScenarioStepList
					.getValue();

			/*
			 * executing the loop for the inner map to fetch the scenario bean
			 * and list of test steps
			 */
			for (Entry<TestScenarios, List<TestStep>> valuesEntryScenarioStepList : scenarioStepList
					.entrySet()) {
				TestScenarios testScenarios = valuesEntryScenarioStepList
						.getKey();
				List<TestStep> testStepList = valuesEntryScenarioStepList
						.getValue();
				int testStepIndex = 1;
				for (TestStep testStep : testStepList) {
					if (!firstTimeFlg) {
						testStepVersionInfo.append(",");
					}
					testStepVersionInfo.append(applicationCommonUtil
							.concatString("-", "" + testStep.getTestStepId(),
									ValueValidations.isValueValid(
											testStep.getStepSelectedVersion())
													? testStep
															.getStepSelectedVersion()
													: testStep
															.getStepLatestVersion(),
									"" + testStepIndex, "Then"));
					firstTimeFlg = false;
					testStepIndex++;
				}
				testScenariosService.saveScenarioStepVersionInfo(
						testScenarios.getTestScenarioId(),
						testScenarios.getHashCode(),
						testScenarios.getScenarioSelectedVersion(),
						testStepVersionInfo.toString());

				List<TestScenarioStepVersion> scenarioStepMappingVersion = testScenarioStepManager
						.getScenarioStepMappingVersion(
								testScenarios.getHashCode(),
								Integer.parseInt(testScenarios
										.getScenarioSelectedVersion()
										.replace("V", "").trim()));

				featureVersionInfo.add(applicationCommonUtil.concatString("-",
						"" + testScenarios.getTestScenarioId(),
						testScenarios.getHashCode(),
						ValueValidations.isValueValid(
								testScenarios.getScenarioSelectedVersion())
										? testScenarios
												.getScenarioSelectedVersion()
										: testScenarios
												.getScenarioLatestVersion(),
						!(null == scenarioStepMappingVersion
								|| scenarioStepMappingVersion.isEmpty())
										? scenarioStepMappingVersion.get(0)
												.getTestScenariosStepVersion()
										: "V1"));
			}
		}

		featureManagementService.saveFeatureVersionInfo(clientProjectId,
				featureFileName, featureVersionInfo.toString());
	}

	@Transactional
	private void createScenarioAndStepSequenceNew(List<TestCase> actualList,
											   int clientProjectId) throws APIExceptions {
		// Get all existing test step hash code with their ID from DB
		Map<String, Long> testStepsHashCode = testStepManager
				.getTestStepHashCode();
		if (null == testStepsHashCode) {
			testStepsHashCode = new LinkedHashMap<String, Long>();
		}

		int testStepSequence = 1;
		long testStepId = 0l;
		int scenarioId = 0;

		int scenarioSequence = 1;
		String currentUser = applicationCommonUtil.getCurrentUser();
		String featureFileName = null;
		Map<Integer, Map<TestScenarios, List<TestStep>>> resultMap = new LinkedHashMap<Integer, Map<TestScenarios, List<TestStep>>>();
		for (TestCase testCase : actualList) {
			if (!testCase.isAutomaticMappingAvailable()) {
				continue;
			}

			// If exist, get the test scenario ID from the map and if not
			// exist, than insert and return the new scenario id
			featureFileName = testCase.getFeatureName();
			if(!ValueValidations.isValueValid(featureFileName)){
				// file name is not provided in the file
				continue;
			}
			scenarioId = testScenariosManager
					.getTestScenarioIdByScenarioAndFeatureFile(clientProjectId,
							testCase.getScenarioName(), featureFileName);

			TestScenarios testScenarios = new TestScenarios();
			testScenarios.setName(testCase.getScenarioName());
			testScenarios.setFeatureFileName(featureFileName);
			testScenarios.setModifiedBy(currentUser);
			String testScenarioHashCode = GenerateUniqueHash
					.getFeatureScenarioHash(clientProjectId,
							testScenarios.getFeatureFileName(),
							testScenarios.getName());
			testScenarios.setHashCode(testScenarioHashCode);
			testScenarios.setClientProjectId(clientProjectId);
			testScenarios.setScenarioSequence(scenarioSequence);
			testScenarios.setScenarioTag("");
			testScenarios.setScenarioLatestVersion("V1");
			testScenarios.setScenarioSelectedVersion("V1");

			if (scenarioId == 0) {
				testScenarios.setCreatedBy(currentUser);
				scenarioId = testScenariosManager
						.persistTestScenarios(testScenarios)
						.getTestScenarioId();
			} else {
				testScenarios = testScenariosManager
						.getTestScenariosById(scenarioId, null);
				testScenarios.setDeleted(false);
				testScenariosManager
						.updateTestScenariosSequenceById(testScenarios);
				testScenarios = testScenariosManager
						.getTestScenariosById(scenarioId, "false");
			}

			String temp = testCase.getAutomatedTestCaseNoFromFile();
			String[] split = temp.split(",");
			TestStep testStep = new TestStep();
			List<TestStep> testStepsList = new ArrayList<TestStep>();
			for (String step : split) {
				step = step.trim();
				testStep.setName(step);
				String testStepHashCode = GenerateUniqueHash
						.getTestStepHash(clientProjectId, step);
				testStep.setHashCode(testStepHashCode);
				testStep.setCreatedBy(currentUser);
				testStep.setModifiedBy(currentUser);
				testStep.setClientProjectId(clientProjectId);
				// applicable will be true only when TestNG Java file is being uploaded
				testStep.setApplicable(false);
				if (!testStepsHashCode.keySet().contains(testStepHashCode)) {
					testStep.setStepLatestVersion("V1");
					testStep.setStepSelectedVersion("V1");
					testStepId = testStepManager.persistTestStep(testStep)
							.getTestStepId();
					testStepsHashCode.put(testStepHashCode, testStepId);
				} else {
					testStepId = testStepsHashCode.get(testStepHashCode);
					testStep = testStepManager.getTestStepById(testStepId,
							clientProjectId);
					testStep.setStepSelectedVersion(
							testStep.getStepLatestVersion());
				}

				// saving data in test scenarios step
				TestScenarioStep testScenarioStep = new TestScenarioStep();
				TestScenarioStep existingTestScenarioStep = testScenarioStepManager
						.isDataExist(clientProjectId, scenarioId, testStepId);
				if (null == existingTestScenarioStep) {
					testScenarioStep.setTestScenarioId(scenarioId);
					testScenarioStep.setTestStepSequence(testStepSequence);
					testScenarioStep.setTestStepId(testStepId);
					testScenarioStep.setCreatedBy(currentUser);
					testScenarioStep.setModifiedBy(currentUser);
					if (null == testScenarioStepManager
							.persistTestScenarioStep(testScenarioStep)) {
						log.error(
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
				testStepSequence++;
				testStepsList.add(testStep);
			}
			scenarioSequence++;
			/*
			 * creating the map with structure as Key: scenarioId and Value: Map
			 * with key:testSenario and list of test steps. This will be used
			 * after this loop tp store the version info
			 */
			if (null == resultMap.get(scenarioId)) {
				Map<TestScenarios, List<TestStep>> value = new LinkedHashMap<TestScenarios, List<TestStep>>();
				value.put(testScenarios, testStepsList);
				resultMap.put(scenarioId, value);
			} else {
				Map<TestScenarios, List<TestStep>> value = resultMap
						.get(scenarioId);
				Set<TestScenarios> keySet = value.keySet();
				List<TestStep> list = null;
				for (TestScenarios sc : keySet) {
					list = value.get(sc);
				}
				list.addAll(testStepsList);
				/*
				 * As scenario is is same, inner map is replaced with key as
				 * scenario and value as latest list of test steps.
				 */
				value.replace(testScenarios, list);
				resultMap.put(scenarioId, value);
			}
		}

		if(ValueValidations.isValueNull(featureFileName)){
			// no file name is provided for any test cases in the import file
			return;
		}

		StringJoiner featureVersionInfo = new StringJoiner(",");
		// For version info save
		StringBuilder testStepVersionInfo = new StringBuilder();
		boolean firstTimeFlg = true;

		/* executing the loop and fetching the info of each scenario ID */
		for (Entry<Integer, Map<TestScenarios, List<TestStep>>> valuesEntryScenarioIdScenarioStepList : resultMap
				.entrySet()) {
			Map<TestScenarios, List<TestStep>> scenarioStepList = valuesEntryScenarioIdScenarioStepList
					.getValue();

			/*
			 * executing the loop for the inner map to fetch the scenario bean
			 * and list of test steps
			 */
			for (Entry<TestScenarios, List<TestStep>> valuesEntryScenarioStepList : scenarioStepList
					.entrySet()) {
				TestScenarios testScenarios = valuesEntryScenarioStepList
						.getKey();
				List<TestStep> testStepList = valuesEntryScenarioStepList
						.getValue();
				int testStepIndex = 1;
				for (TestStep testStep : testStepList) {
					if (!firstTimeFlg) {
						testStepVersionInfo.append(",");
					}
					testStepVersionInfo.append(applicationCommonUtil
							.concatString("-", "" + testStep.getTestStepId(),
									ValueValidations.isValueValid(
											testStep.getStepSelectedVersion())
											? testStep
											.getStepSelectedVersion()
											: testStep
											.getStepLatestVersion(),
									"" + testStepIndex, "Then"));
					firstTimeFlg = false;
					testStepIndex++;
				}
				testScenariosService.saveScenarioStepVersionInfo(
						testScenarios.getTestScenarioId(),
						testScenarios.getHashCode(),
						testScenarios.getScenarioSelectedVersion(),
						testStepVersionInfo.toString());

				List<TestScenarioStepVersion> scenarioStepMappingVersion = testScenarioStepManager
						.getScenarioStepMappingVersion(
								testScenarios.getHashCode(),
								Integer.parseInt(testScenarios
										.getScenarioSelectedVersion()
										.replace("V", "").trim()));

				featureVersionInfo.add(applicationCommonUtil.concatString("-",
						"" + testScenarios.getTestScenarioId(),
						testScenarios.getHashCode(),
						ValueValidations.isValueValid(
								testScenarios.getScenarioSelectedVersion())
								? testScenarios
								.getScenarioSelectedVersion()
								: testScenarios
								.getScenarioLatestVersion(),
						!(null == scenarioStepMappingVersion
								|| scenarioStepMappingVersion.isEmpty())
								? scenarioStepMappingVersion.get(0)
								.getTestScenariosStepVersion()
								: "V1"));
			}
		}

		featureManagementService.saveFeatureVersionInfo(clientProjectId,
				featureFileName, featureVersionInfo.toString());
	}

	private List<TestCase> createHierarchy(
			List<ImportTestCaseBean> importTestCaseBeanList,
			int clientProjectId) throws APIExceptions {
		updateDataSource();
		List<TestCase> actualList = new ArrayList<TestCase>();
		List<String> moduleNameList = new ArrayList<String>();
		Modules modules = null;
		for (ImportTestCaseBean bean : importTestCaseBeanList) {
			moduleNameList.clear();
			moduleNameList.add(bean.getModuleName());
			moduleNameList.add(bean.getFunctionality());
			moduleNameList.add(bean.getSubFunctionality());
			modules = createModuleHierarchy(moduleNameList, clientProjectId);
			if (null != modules) {
				TestCase testCase = setActualValues(bean, modules);
				testCase.setAutomaticMappingAvailable(
						bean.isAutomaticMappingRequired());
				if (bean.isAutomaticMappingRequired()) {
					testCase = setFeatureAndScenarioName(testCase, true);
				}
				actualList.add(testCase);
			} else {
				throw new APIExceptions("Modules info is missing in the given"
						+ " test cases. Process cannot continue.");
			}
		}
		return actualList;
	}

	private TestCase setFeatureAndScenarioName(TestCase testCase,
			boolean isDataImporting) throws APIExceptions {
		String featureName = testCase.getFileName();
		if (!ValueValidations.isValueValid(featureName)) {
			String errMessage = "Error : File name is not provided for the namual vs "
					+ "automated test cases mapping in csv file";
			log.error(errMessage);
			throw new APIExceptions(errMessage);
		}
		if (!featureName.contains("_")) {
			String errMessage = "Error : File name is not as per the standards. Please "
					+ "provide '_' in the file name to get feature "
					+ "and scenario name for automatic mapping";
			log.error(errMessage);
			throw new APIExceptions(errMessage);
		}
		/*
		 * The flag isDataImporting is added to check the below condition only
		 * when the test case is imported. At this time the file name given in
		 * the CSV has '.' followed by extension. Once it is validated the '.'
		 * and extension is removed from the filename and stored in DB as
		 * feature file name. So during the update of the test case, the file
		 * name does not contain '.'. So this check is no longer valid at that
		 * time
		 */
		if (isDataImporting) {
			if (!featureName.contains(".")) {
				String errMessage = "Error : File name is not as per the standards. Please "
						+ "provide '.' with file extension in the file name to get feature "
						+ "and scenario name for automatic mapping";
				log.error(errMessage);
				throw new APIExceptions(errMessage);
			}
			featureName = featureName.substring(0,
					testCase.getFileName().lastIndexOf("."));
		}
		String scenarioName = featureName
				.substring(featureName.indexOf("_") + 1, featureName.length());
		testCase.setFeatureName(featureName);
		testCase.setScenarioName(scenarioName);

		return testCase;
	}

	@Override
	@Transactional
	public Modules createModuleHierarchy(List<String> moduleNameList,
			int clientProjectId) throws APIExceptions {
		Modules modules = null;
		long moduleParentId = 0;
		boolean firstTime = true;
		String tempSQL = "SELECT module_id FROM modules WHERE "
				+ "name='%moduleName%' and module_parent_id=(0)";
		String sql = tempSQL + " AND client_project_id=" + clientProjectId;
		for (String moduleName : moduleNameList) {
			moduleName = moduleName.replaceAll("'", "").trim();
			if (null != moduleName && moduleName.trim().equals("")) {
				continue;
			}
			if (firstTime) {
				sql = sql.replace("%moduleName%", moduleName)
						.replace("%moduleID%", "0");
				firstTime = false;
			} else {
				int lastIndexOfCharacterToReplace = tempSQL.lastIndexOf("0");
				StringBuilder builder = new StringBuilder();
				builder.append(
						tempSQL.substring(0, lastIndexOfCharacterToReplace));
				builder.append(sql);
				builder.append(tempSQL.substring(
						lastIndexOfCharacterToReplace + 1, tempSQL.length()));
				sql = builder.toString().replace("%moduleName%", moduleName);
			}

			long moduleId = modulesService.getModuleIdFromHierarchy(sql);
			if (moduleId != 0) {
				modules = modulesService
						.getModulesByModuleNameAndClientProjectId(moduleName,
								moduleParentId, clientProjectId);
				moduleParentId = moduleId;
			} else {
				modules = new Modules();
				modules.setName(moduleName);
				modules.setModuleParentId(moduleParentId);
				modules.setClientProjectsId(clientProjectId);
				modules.setCreatedBy(applicationCommonUtil.getCurrentUser());
				modules.setModifiedBy(applicationCommonUtil.getCurrentUser());
				// Saving module if not exist
				modules = modulesService.addModules(modules);
				moduleParentId = modules.getModuleId();
			}
		}
		return modules;
	}

	private TestCase setActualValues(ImportTestCaseBean testCaseBean,
			Modules modules) throws APIExceptions {
		TestCase testCase = new TestCase();
		// obj.setTestCaseId(loop.getIdRef());
		testCase.setTestCaseNo(testCaseBean.getTestCaseIdRef());
		testCase.setTestData(testCaseBean.getTestData());
		testCase.setModuleId(modules.getModuleId());
		testCase.setTestSummary(testCaseBean.getTestSummary());
		testCase.setPreCondition(testCaseBean.getPreCondition());

		String tags = testCaseBean.getCriticality();
		List<String> tagList = Arrays.asList(tags);
		testCase.setTags(tagList);
		testCase.setExecutionSteps(testCaseBean.getExecutionSteps());
		testCase.setExpectedResult(testCaseBean.getExpectedResult());
		if (testCaseBean.getIsAutomatable().equalsIgnoreCase("yes"))
			testCase.setAutomatable(true);
		else if (testCaseBean.getIsAutomatable().equalsIgnoreCase(null))
			testCase.setAutomatable(false);

		testCase.setRemarks(testCaseBean.getRemarks());
		testCase.setFileName(testCaseBean.getFileName());
		testCase.setAutomatedTestCaseNoFromFile(
				testCaseBean.getAutomatetTestCaseNoFromFile());
		if (testCaseBean.getApplicable().equalsIgnoreCase("yes"))
			testCase.setApplicable(true);
		else if (testCaseBean.getApplicable().equalsIgnoreCase(null))
			testCase.setApplicable(false);

		testCase.setCreatedBy(applicationCommonUtil.getCurrentUser());
		testCase.setModifiedBy(applicationCommonUtil.getCurrentUser());
		testCase.setHashCode(GenerateUniqueHash.getTestCaseHash(
				modules.getModuleId(), testCase.getTestCaseNo(),
				testCase.getTestSummary(), testCase.getPreCondition(),
				testCase.getExecutionSteps(), testCase.getExpectedResult()));
		return testCase;
	}

	public List<ImportTestCaseBean> processCSVFile(MultipartFile uploadfile)
			throws APIExceptions, NullPointerException, EmptyDataException,
			IOException {
		List<ImportTestCaseBean> list = new ArrayList<>();
		// For local file processing
		log.info("Start processCSVFile");
		try {
			CsvReader csvReader = new CsvReader(uploadfile.getInputStream(),
					Charset.defaultCharset());
			csvReader.readHeaders();
			// Verifying the import file headers
			List<String> headersList = Arrays
					.asList(PintailerConstants.IMPORT_TC_CSV_HEADERS);
			String[] actualHeadersInFile = csvReader.getHeaders();
			String nonExistColumnHeaders = null;
			boolean firstTime = true;
			for (int i = 0; i < actualHeadersInFile.length; i++) {
				if (actualHeadersInFile[i].trim().equals("")) {
					continue;
				}
				if (!headersList.contains(actualHeadersInFile[i].replaceAll("\uFEFF", ""))) {
					if (firstTime) {
						nonExistColumnHeaders = actualHeadersInFile[i];
						firstTime = false;
					} else {
						nonExistColumnHeaders += "," + actualHeadersInFile[i];
					}
				}
			}

			if (null != nonExistColumnHeaders) {
				throw new APIExceptions("The imported file headers ["
						+ nonExistColumnHeaders
						+ "] are not valid. Please update the file and retry.");
			}

			boolean isApplicable = false;
			boolean isTestCaseNoAvailable = false;
			int count = 1;
			while (csvReader.readRecord()) {
				isApplicable = false;
				isTestCaseNoAvailable = false;
				ImportTestCaseBean bean = new ImportTestCaseBean();
				bean.setTestCaseIdRef(csvReader
						.get(PintailerConstants.IMPORT_CSV_COLUMN_TC_ID_REF)
						.trim());
				bean.setModuleName(csvReader
						.get(PintailerConstants.IMPORT_CSV_COLUMN_MODULE_NAME)
						.trim());
				bean.setFunctionality(csvReader
						.get(PintailerConstants.IMPORT_CSV_COLUMN_FUNCTIONALITY)
						.trim());
				bean.setSubFunctionality(csvReader.get(
						PintailerConstants.IMPORT_CSV_COLUMN_SUB_FUNCTIONALITY)
						.trim());
				bean.setTestSummary(csvReader
						.get(PintailerConstants.IMPORT_CSV_COLUMN_TEST_SUMMARY)
						.trim());
				bean.setPreCondition(csvReader
						.get(PintailerConstants.IMPORT_CSV_COLUMN_PRE_CONDITION)
						.trim());
				bean.setExecutionSteps(csvReader.get(
						PintailerConstants.IMPORT_CSV_COLUMN_EXECUTION_STEPS)
						.trim());
				bean.setExpectedResult(csvReader.get(
						PintailerConstants.IMPORT_CSV_COLUMN_EXPECTED_RESULT)
						.trim());
				bean.setActualResult(csvReader
						.get(PintailerConstants.IMPORT_CSV_COLUMN_ACTUAL_RESULT)
						.trim());
				bean.setTester(csvReader
						.get(PintailerConstants.IMPORT_CSV_COLUMN_TESTER)
						.trim());
				bean.setExecutionDate(csvReader.get(
						PintailerConstants.IMPORT_CSV_COLUMN_EXECUTION_DATE)
						.trim());
				bean.setTestResults(csvReader
						.get(PintailerConstants.IMPORT_CSV_COLUMN_TEST_RESULTS)
						.trim());
				bean.setTestData(csvReader
						.get(PintailerConstants.IMPORT_CSV_COLUMN_TEST_DATA)
						.trim());
				bean.setLinkedDefect(csvReader.get(
						PintailerConstants.IMPORT_CSV_COLUMN_LINKED_DEFECTS)
						.trim());
				bean.setEnvironment(csvReader
						.get(PintailerConstants.IMPORT_CSV_COLUMN_ENVIRONMENT)
						.trim());
				bean.setCriticality(csvReader
						.get(PintailerConstants.IMPORT_CSV_COLUMN_CRITICALITY)
						.trim());

				bean.setIsAutomatable(csvReader.get(
						PintailerConstants.IMPORT_CSV_COLUMN_IS_AUTOMATABLE)
						.trim());
				bean.setRemarks(csvReader
						.get(PintailerConstants.IMPORT_CSV_COLUMN_REMARKS)
						.trim());
				bean.setFileName(csvReader
						.get(PintailerConstants.IMPORT_CSV_COLUMN_FILE_NAME)
						.trim());

				String testCaseNo = csvReader
						.get(PintailerConstants.IMPORT_CSV_COLUMN_TEST_CASE_NO)
						.trim();
				if (null != testCaseNo
						&& !testCaseNo.replaceAll("\t", "").replaceAll("\n", "")
								.replaceAll("\r", "").trim().equals("")) {
					isTestCaseNoAvailable = true;
				}
				bean.setAutomatetTestCaseNoFromFile(testCaseNo);

				String isApplicableFlg = csvReader
						.get(PintailerConstants.IMPORT_CSV_COLUMN_APPLICABLE)
						.trim();
				if (null != isApplicableFlg
						&& isApplicableFlg.toLowerCase().equals("yes")) {
					isApplicable = true;
				}
				bean.setApplicable(isApplicableFlg);

				bean.setCreationDate(csvReader
						.get(PintailerConstants.IMPORT_CSV_COLUMN_CREATION_DATE)
						.trim());

				// Is automatic mapping required flag initialized in bean as
				// per the values in column
				if (isApplicable && isTestCaseNoAvailable) {
					bean.setAutomaticMappingRequired(true);
				} else {
					bean.setAutomaticMappingRequired(false);
				}

				StringBuilder error = new StringBuilder();

				if (bean.getModuleName() == null
						|| bean.getModuleName().equals("")) {
					error.append(" Module Name,");
				}
				if (bean.getFunctionality() == null
						|| bean.getFunctionality().equals("")) {
					error.append(" Functionaity Name,");
				}
				if (bean.getSubFunctionality() == null
						|| bean.getSubFunctionality().equals("")) {
					error.append(" Sub Functionaity Name,");
				}
				if (bean.getTestSummary() == null
						|| bean.getTestSummary().equals("")) {
					error.append(" Test Summary,");
				}
				if (bean.getExecutionSteps() == null
						|| bean.getExecutionSteps().equals("")) {
					error.append(" Execution Steps,");
				}
				if (bean.getExpectedResult() == null
						|| bean.getExpectedResult().equals("")) {
					error.append(" Expected Results,");
				}
				
/*
 * 
 * 
 * Commenting because the test results are not the mendetory value
 * 
 * 
 * */
//				if (bean.getTestResults() == null
//						|| bean.getTestResults().equals("")) {
//					error.append(" Test Results,");
//				}
				
				if (bean.getCriticality() == null
						|| bean.getCriticality().equals("")) {
					bean.setCriticality("P3");
				}
				if (!error.toString().isEmpty()) {
					throw new EmptyDataException(LocalUtils.getStringLocale(
							"fw_test_mgmt_locale", "MandatoryValuesCSV")
							+ " Error in line " + count + " for column[s] : "
							+ error.toString().trim().substring(0,
									error.toString().length() - 1));
				} else {
					list.add(bean);
				}
				count++;
			}
		} catch (APIExceptions e) {
			String message = LocalUtils.getStringLocale("fw_test_mgmt_locale",
					"TestCaseImport") + " Issue : " + e.getMessage();
			log.error(message);
			throw new APIExceptions(message);
		}
		return list;

	}

	public List<ImportTestCaseBean> processCSVFileNew(MultipartFile uploadfile)
			throws APIExceptions, NullPointerException, EmptyDataException,
			IOException {
		List<ImportTestCaseBean> list = new ArrayList<>();
		// For local file processing
		log.info("Start processCSVFile");
		try {
			CsvReader csvReader = new CsvReader(uploadfile.getInputStream(),
					Charset.defaultCharset());
			csvReader.readHeaders();
			// Verifying the import file headers
			List<String> headersList = Arrays
					.asList(PintailerConstants.IMPORT_TC_CSV_HEADERS_NEW);
			String[] actualHeadersInFile = csvReader.getHeaders();
			String nonExistColumnHeaders = null;
			boolean firstTime = true;
			for (int i = 0; i < actualHeadersInFile.length; i++) {
				if (actualHeadersInFile[i].trim().equals("")) {
					continue;
				}
				if (!headersList.contains(actualHeadersInFile[i].replaceAll("\uFEFF", ""))) {
					if (firstTime) {
						nonExistColumnHeaders = actualHeadersInFile[i];
						firstTime = false;
					} else {
						nonExistColumnHeaders += "," + actualHeadersInFile[i];
					}
				}
			}

			if (null != nonExistColumnHeaders) {
				throw new APIExceptions("The imported file headers ["
						+ nonExistColumnHeaders
						+ "] are not valid. Please update the file and retry.");
			}

			boolean isApplicable = false;
			boolean isTestCaseNoAvailable = false;
			int count = 1;
			while (csvReader.readRecord()) {
				isApplicable = false;
				isTestCaseNoAvailable = false;
				ImportTestCaseBean bean = new ImportTestCaseBean();
				bean.setTestCaseIdRef(csvReader.get(PintailerConstants.IMPORT_CSV_COLUMN_TC_ID_REF).trim());
				bean.setModuleName(csvReader.get(PintailerConstants.IMPORT_CSV_COLUMN_MODULE_NAME).trim());
				bean.setFunctionality(csvReader.get(PintailerConstants.IMPORT_CSV_COLUMN_FUNCTIONALITY).trim());
				bean.setSubFunctionality(csvReader.get(PintailerConstants.IMPORT_CSV_COLUMN_SUB_FUNCTIONALITY).trim());
				bean.setTestSummary(csvReader.get(PintailerConstants.IMPORT_CSV_COLUMN_TEST_SUMMARY).trim());
				bean.setPreCondition(csvReader.get(PintailerConstants.IMPORT_CSV_COLUMN_PRE_CONDITION).trim());
				bean.setExecutionSteps(csvReader.get(PintailerConstants.IMPORT_CSV_COLUMN_EXECUTION_STEPS).trim());
				bean.setExpectedResult(csvReader.get(PintailerConstants.IMPORT_CSV_COLUMN_EXPECTED_RESULT).trim());
				bean.setActualResult(csvReader.get(PintailerConstants.IMPORT_CSV_COLUMN_ACTUAL_RESULT).trim());
				bean.setTester(csvReader.get(PintailerConstants.IMPORT_CSV_COLUMN_TESTER).trim());
				bean.setExecutionDate(csvReader.get(PintailerConstants.IMPORT_CSV_COLUMN_EXECUTION_DATE).trim());
				bean.setTestResults(csvReader.get(PintailerConstants.IMPORT_CSV_COLUMN_TEST_RESULTS).trim());
				bean.setTestData(csvReader.get(PintailerConstants.IMPORT_CSV_COLUMN_TEST_DATA).trim());
				bean.setLinkedDefect(csvReader.get(PintailerConstants.IMPORT_CSV_COLUMN_LINKED_DEFECTS).trim());
				bean.setEnvironment(csvReader.get(PintailerConstants.IMPORT_CSV_COLUMN_ENVIRONMENT).trim());
				bean.setCriticality(csvReader.get(PintailerConstants.IMPORT_CSV_COLUMN_CRITICALITY).trim());
				bean.setIsAutomatable(csvReader.get(PintailerConstants.IMPORT_CSV_COLUMN_IS_AUTOMATABLE).trim());
				bean.setRemarks(csvReader.get(PintailerConstants.IMPORT_CSV_COLUMN_REMARKS).trim());

				// Set fileName as combination of moduleName and functionality
				String moduleName = bean.getModuleName().replaceAll("\\s+", "_")
						.replaceAll("[^a-zA-Z0-9_]+", "")
						.toLowerCase();
				String functionality = bean.getFunctionality().replaceAll("\\s+", "_")
						.replaceAll("[^a-zA-Z0-9_]+", "")
						.toLowerCase();
				String fileName = moduleName + "_" + functionality + ".java";
				bean.setFileName(fileName);

				// Set automatetTestCaseNoFromFile from Test Summary
				String testSummary = bean.getTestSummary();
				String automatetTestCaseNo = testSummary.replaceAll("\\s+", "_")
						.replaceAll("[^a-zA-Z0-9_]+", "")
						.toLowerCase();
				bean.setAutomatetTestCaseNoFromFile(automatetTestCaseNo);
				isTestCaseNoAvailable = true;

				String isApplicableFlg = csvReader
						.get(PintailerConstants.IMPORT_CSV_COLUMN_APPLICABLE)
						.trim();
				if (null != isApplicableFlg
						&& isApplicableFlg.toLowerCase().equals("yes")) {
					isApplicable = true;
				}
				bean.setApplicable(isApplicableFlg);

				bean.setCreationDate(csvReader
						.get(PintailerConstants.IMPORT_CSV_COLUMN_CREATION_DATE)
						.trim());

				// Is automatic mapping required flag initialized in bean as
				// per the values in column
				if (isApplicable && isTestCaseNoAvailable) {
					bean.setAutomaticMappingRequired(true);
				} else {
					bean.setAutomaticMappingRequired(false);
				}

				StringBuilder error = new StringBuilder();

				if (bean.getModuleName() == null
						|| bean.getModuleName().equals("")) {
					error.append(" Module Name,");
				}
				if (bean.getFunctionality() == null
						|| bean.getFunctionality().equals("")) {
					error.append(" Functionaity Name,");
				}
				if (bean.getSubFunctionality() == null
						|| bean.getSubFunctionality().equals("")) {
					error.append(" Sub Functionaity Name,");
				}
				if (bean.getTestSummary() == null
						|| bean.getTestSummary().equals("")) {
					error.append(" Test Summary,");
				}
				if (bean.getExecutionSteps() == null
						|| bean.getExecutionSteps().equals("")) {
					error.append(" Execution Steps,");
				}
				if (bean.getExpectedResult() == null
						|| bean.getExpectedResult().equals("")) {
					error.append(" Expected Results,");
				}

				/*
				 *
				 *
				 * Commenting because the test results are not the mendetory value
				 *
				 *
				 * */
//				if (bean.getTestResults() == null
//						|| bean.getTestResults().equals("")) {
//					error.append(" Test Results,");
//				}

				if (bean.getCriticality() == null
						|| bean.getCriticality().equals("")) {
					bean.setCriticality("P3");
				}
				if (!error.toString().isEmpty()) {
					throw new EmptyDataException(LocalUtils.getStringLocale(
							"fw_test_mgmt_locale", "MandatoryValuesCSV")
							+ " Error in line " + count + " for column[s] : "
							+ error.toString().trim().substring(0,
							error.toString().length() - 1));
				} else {
					list.add(bean);
				}
				count++;
			}
		} catch (APIExceptions e) {
			String message = LocalUtils.getStringLocale("fw_test_mgmt_locale",
					"TestCaseImport") + " Issue : " + e.getMessage();
			log.error(message);
			throw new APIExceptions(message);
		}
		return list;

	}

	// Fetch all the test cases of the given module and all of its children. It
	// was changed on 11 Sep 2018 for bug 68 in bit bucket. Initially
	// getTestCaseByModuleId method of manager class was getting called which is
	// updated to the method used to get the data for reports page. This change
	// is done to get all the test cases on test case mapping and release
	// mapping pages when user select high level module
	@Override
	public List<TestCaseClientBean> getTestCaseByModuleId(int clientProjectId,
			int releaseId, long moduleId, String applicable, String isDeleted)
			throws APIExceptions {
		try {
//			if (releaseId == 0) {
//				final List<TestCase> testCaseList = getTestCaseBeanListByModuleId(
//						moduleId, applicable, isDeleted);
//
//				List<TestCaseClientBean> finalResult = new ArrayList<TestCaseClientBean>();
//				for (TestCase testCase : testCaseList) {
//					finalResult.add(mapTestCaseWithClientBean(testCase, null));
//				}
//				return finalResult;
//			} else {
			List<Long> allChildModules = modulesService
					.getAllChildModules(moduleId);
			String moduleIds = allChildModules.stream().map(Object::toString)
					.collect(Collectors.joining(", "));

			List<TestCaseClientBean> testCaseList = getTestCaseList(
					clientProjectId, releaseId, moduleIds, null, applicable,
					null, null, null, null, 0, 0, null, null, isDeleted, false);

			if (null == testCaseList || testCaseList.isEmpty()) {
				return new ArrayList<TestCaseClientBean>();
			}

			/*
			 * updating the mapping information i.e. if mapping exist, the flag
			 * is set true else set false
			 */
			String testCaseIds = testCaseList.stream()
					.map(e -> new String("" + e.getTestCaseSequenceId()))
					.collect(Collectors.joining(", "));
			Map<Integer, List<TestCaseMap>> testCaseMapByTestCaseIds = testCaseMapManager
					.getTestCaseMapByTestCaseIds(testCaseIds);
			if (null != testCaseMapByTestCaseIds) {
				Iterator<TestCaseClientBean> testCaseClientBean = testCaseList
						.listIterator();
				while (testCaseClientBean.hasNext()) {
					TestCaseClientBean bean = testCaseClientBean.next();
					bean.setAutomationMappingExists(false);
					if (null != testCaseMapByTestCaseIds
							.get(bean.getTestCaseSequenceId())) {
						bean.setAutomationMappingExists(true);
					}
				}
			}
			releaseMapServiceImpl.updateVersionInfo(clientProjectId, releaseId,
					testCaseList);
			return testCaseList;
//			}
		} catch (APIExceptions e) {
			log.error(e);
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "TestCaseByModule"));
		} catch (NullPointerException e) {
			log.error(e);
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "TestCaseByModule"));
		}
	}

	@Override
	public List<TestCase> getTestCaseBeanListByModuleId(long moduleId,
			String applicable, String isDeleted) throws APIExceptions {
		try {

			Modules module = modulesService.getModulesById(moduleId);
			ClientProjects clientProject = clientProjectsManager
					.getClientProjectsById(module.getClientProjectsId());

			List<Long> allChildModules = modulesService
					.getAllChildModules(moduleId);
//			StringBuilder builder = new StringBuilder();
//			boolean firstTime = true;
//
//			for (int i = 0; i < allChildModules.size(); i++) {
//				if (firstTime) {
//					builder.append(allChildModules.get(i));
//					firstTime = false;
//				} else {
//					builder.append(",").append(allChildModules.get(i));
//				}
//			}
//			String moduleIds = builder.toString();
			String moduleIds = allChildModules.stream().map(Object::toString)
					.collect(Collectors.joining(", "));
			if (null == moduleIds || moduleIds.equals("")
					|| moduleIds.equals("0")) {
				throw new APIExceptions(
						"Error: Given module id does not exist in DB.");
			}

			return testCaseManager.getAllTestCases(
					clientProject.getClientProjectId(), 0, moduleIds, null,
					applicable, null, null, null, null, 0, 0, null, null,
					isDeleted);
		} catch (APIExceptions e) {
			log.error(e);
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "TestCaseByModule"));
		} catch (NullPointerException e) {
			log.error(e);
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "TestCaseByModule"));
		}
	}

	@Override
	public Set<String> getProjectSpecificTagList(int clientProjectId)
			throws APIExceptions {
		List<String> testCaseTagList = new ArrayList<String>();
		Set<String> tagSet = new HashSet<String>();
		testCaseTagList = testCaseManager
				.getProjectTestCaseTagsDetails(clientProjectId);
		for (String tagList : testCaseTagList) {
			String tags = tagList.toUpperCase();
			if (tags.contains(",")) {
				String tagArray[] = tags.split(",");
				for (int i = 1; i < tagArray.length; i++) {
					tagSet.add(tagArray[i]);
				}
			} else {
				tagSet.add(tags);
			}
		}
		return tagSet;
	}

	@Override
	public List<TestCaseClientBean> getTestCaseList(int clientProjectId,
			int releaseId, String moduleIds, String tags, String applicable,
			String testCaseIds, String searchTxt, String sortByColumn,
			String ascOrDesc, int limit, int pageNumber, String startDate,
			String endDate, String isDeleted, boolean isAutomationInfoRequired)
			throws APIExceptions {
		List<TestCase> testCases = getAllTestCases(clientProjectId, releaseId,
				moduleIds, tags, applicable, testCaseIds, searchTxt,
				sortByColumn, ascOrDesc, limit, pageNumber, startDate, endDate,
				isDeleted);
		// getAllTestCases(clientProjectId, 0, null, null, applicable,
		// testCaseIds, searchTxt, sortByColumn, ascOrDesc, limit,
		// pageNumber);
		if (null == testCases || testCases.isEmpty()) {
			return new ArrayList<TestCaseClientBean>();
		}
		List<TestCaseClientBean> testCaseClientBeanList = new ArrayList<TestCaseClientBean>();
		long t1 = System.currentTimeMillis();
		Map<String, String[]> moduleHierarchy = null;
		if (!ValueValidations.isValueValid(moduleIds)
				|| moduleIds.equals("0")) {
			moduleIds = String.join(",",
					testCases.stream().map(e -> "" + e.getModuleId()).distinct()
							.collect(Collectors.toList()));
			moduleHierarchy = modulesService.getModuleHierarchy(moduleIds);
		}

		for (TestCase testCase : testCases) {
			testCaseClientBeanList
					.add(mapTestCaseWithClientBean(clientProjectId, testCase,
							moduleHierarchy, isAutomationInfoRequired));
		}
		testCaseClientBeanList = updateTestCaseVersionInfo(
				testCaseClientBeanList);

		testCaseClientBeanList = releaseMapServiceImpl
				.updateTestCaseCompulsoryExecutionFlag(0,
						testCaseClientBeanList);
		long t2 = System.currentTimeMillis();
		log.info("Time taken in fetching test case list = " + (t2 - t1)
				+ " milli seconds");
		return testCaseClientBeanList;
	}

	private List<TestCaseClientBean> updateTestCaseVersionInfo(
			List<TestCaseClientBean> testCases) throws APIExceptions {
		String ids = getSelectedTestCaseIds(testCases);
		final Map<Integer, List<TestCaseVersion>> multipleTestCasesVersionMap = testCaseManager
				.getMultipleTestCasesVersionMap(ids);
		final List<TestCaseClientBean> updatedTestCases = new ArrayList<TestCaseClientBean>();
		for (TestCaseClientBean testCase : testCases) {
			List<TestCaseVersion> testCaseVerions = multipleTestCasesVersionMap
					.get(testCase.getTestCaseSequenceId());
			if (null != testCaseVerions) {
				// currently sending only version string. When user click on the
				// get version info button on the UI, all the version info will
				// be fetched. This is to reduce the payload size and avoid
				// performance impact.
//				testCase.setTestCaseVersionList(testCaseVerions);
				testCase.setLatestVersion(
						testCaseVerions.get(0).getTestCaseVersion());
			}
			updatedTestCases.add(testCase);
		}
		return updatedTestCases;
	}

	private String getSelectedTestCaseIds(List<TestCaseClientBean> testCases) {
		String testCaseIds = null;
		boolean firstTime = true;
		for (TestCaseClientBean testCase : testCases) {
			if (firstTime) {
				testCaseIds = "" + testCase.getTestCaseSequenceId();
				firstTime = false;
			} else {
				testCaseIds += "," + testCase.getTestCaseSequenceId();
			}
		}
		return testCaseIds;
	}

	private TestCaseClientBean mapTestCaseWithClientBean(int clientProjectId,
			TestCase testCase, Map<String, String[]> moduleHierarchy,
			boolean isAutomationInfoRequired) throws APIExceptions {
		TestCaseClientBean testCaseClientBean = new TestCaseClientBean();
		testCaseClientBean.setTestCaseSequenceId(testCase.getTestCaseId());
		testCaseClientBean.setTestCaseNo(testCase.getTestCaseNo());
		testCaseClientBean.setTestData(testCase.getTestData());
		testCaseClientBean.setModuleId(testCase.getModuleId());
		testCaseClientBean.setTestSummary(testCase.getTestSummary());
		testCaseClientBean.setPreCondition(testCase.getPreCondition());
		testCaseClientBean.setTags(testCase.getTags());
		testCaseClientBean.setExecutionSteps(testCase.getExecutionSteps());
		testCaseClientBean.setExpectedResult(testCase.getExpectedResult());
		testCaseClientBean.setAutomatable(testCase.isAutomatable());
		testCaseClientBean.setRemarks(testCase.getRemarks());
		testCaseClientBean.setFileName(testCase.getFileName());
		testCaseClientBean.setAutomatedTestCaseNoFromFile(
				testCase.getAutomatedTestCaseNoFromFile());
		testCaseClientBean.setManualReason(testCase.getManualReason());
		testCaseClientBean.setApplicable(testCase.isApplicable());
		testCaseClientBean.setCreatedBy(testCase.getCreatedBy());
		testCaseClientBean.setModifiedBy(testCase.getModifiedBy());
		testCaseClientBean.setCreatedDate(testCase.getCreatedDate());
		testCaseClientBean.setDeleted(testCase.isDeleted());

//		if (!(null == moduleHierarchy || moduleHierarchy.trim().equals("")
//				|| moduleHierarchy.trim().equals("null"))) {
//			testCaseClientBean.setModulesNameHierarchy(moduleHierarchy);
//		} else {
//			testCaseClientBean.setModulesNameHierarchy(
//					getFinalModuleHierarchy(testCase.getModuleId()));
		if (!(null == moduleHierarchy || moduleHierarchy.isEmpty())) {
			testCaseClientBean.setModulesNameHierarchy(String.join("@#",
					moduleHierarchy.get("" + testCase.getModuleId())));
		}
//		}

		testCaseClientBean.setLatestVersion(testCase.getLatestVersion());

		if (isAutomationInfoRequired) {
			testCaseClientBean = getTestCaseMappedFileNameAndSteps(
					clientProjectId, testCaseClientBean);
		}
		return testCaseClientBean;
	}

	/**
	 * This method is added to give the file name and automated test steps
	 * mapped to the test case. Originally this is added to give the latest test
	 * steps info for TestNg test cases. Previously the mapped test steps were
	 * getting stored in the test case table. So change in the mapping on the
	 * mapping page was not reflected on the test definition page as the
	 * information was coming from the test case table only. To avoid incorrect
	 * information, the mapped test steps info is coming from respective table
	 * and assigned to the test case client bean on the test definition page.
	 */
	private TestCaseClientBean getTestCaseMappedFileNameAndSteps(
			int clientProjectId, TestCaseClientBean testCaseClientBean)
			throws NumberFormatException, APIExceptions {
		List<TestCaseMapVersion> testCaseMapVersionList = testCaseMapManager
				.getTestCaseMapVersion(
						testCaseClientBean.getTestCaseSequenceId(),
						Integer.parseInt(testCaseClientBean.getLatestVersion()
								.replace("V", "")));
		if (null == testCaseMapVersionList
				|| testCaseMapVersionList.isEmpty()) {
//			log.info("No mapping is available for the given test case id ["
//					+ testCaseClientBean.getTestCaseSequenceId() + "]");
			testCaseClientBean.setFileName("");
			testCaseClientBean.setAutomatedTestCaseNoFromFile("");
			return testCaseClientBean;
		}

		TestCaseMapVersion testCaseMapVersion = testCaseMapVersionList.get(0);

		// scenario version and scenario-step mapping version
		int testScenarioStepVersionId = testCaseMapVersion
				.getTestScenarioStepVersionId();

		if (testScenarioStepVersionId == 0
				&& testCaseMapVersion.getSelectedTestStepsIdAndVersion()
						.equals(PintailerConstants.TEST_CASE_STEP_NO_MAPPING)) {
			log.info(
					"Mapping is no longer available for the given test case id ["
							+ testCaseClientBean.getTestCaseSequenceId() + "]");
			testCaseClientBean.setFileName("");
			testCaseClientBean.setAutomatedTestCaseNoFromFile("");
			return testCaseClientBean;
		} else if ((testScenarioStepVersionId == 0
				&& !testCaseMapVersion.getSelectedTestStepsIdAndVersion()
						.equals(PintailerConstants.TEST_CASE_STEP_NO_MAPPING))
				|| (testScenarioStepVersionId != 0 && testCaseMapVersion
						.getSelectedTestStepsIdAndVersion()
						.equals(PintailerConstants.TEST_CASE_STEP_NO_MAPPING))) {
			log.error("There is some invalid version data of the mapping of "
					+ "the test cases id ["
					+ testCaseClientBean.getTestCaseSequenceId() + "].");
			testCaseClientBean.setFileName("");
			testCaseClientBean.setAutomatedTestCaseNoFromFile("");
			return testCaseClientBean;
		}

		// Getting scenario and step mapping specific version
		TestScenarioStepVersion scenarioStepMappingVersion = testScenarioStepManager
				.getScenarioStepMappingVersion(testScenarioStepVersionId);
		int testScenariosId = scenarioStepMappingVersion.getTestScenariosId();
//					int testScenariosVersionId = scenarioStepMappingVersion
//							.getTestScenariosVersionId();

		String featureFileName = testScenariosManager
				.getTestScenariosById(testScenariosId, "false")
				.getFeatureFileName();

		// return empty values for test case having automated using cucumber
		if (featureFileName.toLowerCase().endsWith(".feature")) {
			testCaseClientBean.setFileName("");
			testCaseClientBean.setAutomatedTestCaseNoFromFile("");
			return testCaseClientBean;
		}
		// selected test steps info
		String selectedTestStepsIdAndVersion = testCaseMapVersion
				.getSelectedTestStepsIdAndVersion();
		String[] split = selectedTestStepsIdAndVersion.split(",");
		String separator = "";
		String mappedTestSteps = "";
		for (String step : split) {
			mappedTestSteps += separator + testStepManager
					.getTestStepById(Long.parseLong(step.split("::")[0]),
							clientProjectId)
					.getName();
			separator = ",";
		}

		testCaseClientBean.setFileName(featureFileName);
		testCaseClientBean
				.setAutomatedTestCaseNoFromFile(mappedTestSteps.trim());
		return testCaseClientBean;
	}

	@Override
	public String getFinalModuleHierarchy(long moduleId) throws APIExceptions {
		String modulesHierarchy = modulesService.getModuleHierarchy(moduleId)
				.get(0).trim();
		modulesHierarchy = modulesHierarchy.replace("{", "").replace("}", "")
				.trim();
		while (modulesHierarchy.indexOf("#") > 0) {
			int startIndex = modulesHierarchy.indexOf("#");
			int endIndex = startIndex;
			while (!(modulesHierarchy.charAt(endIndex) == ','
					|| endIndex == modulesHierarchy.length() - 1)) {
				endIndex++;
			}
			String temp = modulesHierarchy.substring(startIndex, endIndex + 1);
			modulesHierarchy = modulesHierarchy.replace(temp, "@");
		}
		String[] hierarchyArr = modulesHierarchy.split("@");
		for (int i = 0; i < hierarchyArr.length; i++) {
			hierarchyArr[i] = hierarchyArr[i].replaceAll("^\"|\"$", "");
			// hierarchyArr[i] = hierarchyArr[i].replaceAll("\\\\", "");
		}
		ArrayUtils.reverse(hierarchyArr);
		return String.join("@#", hierarchyArr);
	}

	@Override
	public int getTestCasesCount(int clientProjectId, String applicable,
			String searchTxt) throws APIExceptions {
		return testCaseManager.getTestCasesCount(clientProjectId, applicable,
				searchTxt);
	}

	@Override
	public List<TestCaseVersion> getTestCaseVersion(int testCaseId)
			throws APIExceptions {
		final List<TestCaseVersion> testCaseVersionList = testCaseManager
				.getTestCaseVersion(testCaseId);
		for (TestCaseVersion testCaseVersion : testCaseVersionList) {
			testCaseVersion.setModulesNameHierarchy(testCaseService
					.getFinalModuleHierarchy(testCaseVersion.getModuleId()));
		}
		return testCaseVersionList;
	}

	@Override
	public TestCaseVersion getTestCaseVersion(int testCaseId, int versionId)
			throws APIExceptions {
		return testCaseManager.getTestCaseVersion(testCaseId, versionId);
	}

	@Override
	public List<Integer> getDeletedTestCaseIdsForGivenTime(int clientProjectId,
			String startDate, String endDate) throws APIExceptions {
		return testCaseManager.getDeletedTestCaseIdsForGivenTime(
				clientProjectId, startDate, endDate);
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
