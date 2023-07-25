package com.fw.services.impl;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fw.dao.ITestCaseManager;
import com.fw.dao.ITestCaseMapManager;
import com.fw.dao.ITestScenarioStepManager;
import com.fw.dao.ITestScenariosManager;
import com.fw.dao.ITestStepManager;
import com.fw.domain.ReleaseTestCaseMapping;
import com.fw.domain.TestCase;
import com.fw.domain.TestCaseMap;
import com.fw.domain.TestCaseMapVersion;
import com.fw.domain.TestScenarioStepVersion;
import com.fw.domain.TestScenarios;
import com.fw.domain.TestStep;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;
import com.fw.services.IReleaseTestCaseMappingService;
import com.fw.services.ITestCaseMapService;
import com.fw.services.ITestCaseService;
import com.fw.utils.ApplicationCommonUtil;
import com.fw.utils.ValueValidations;

@Service
public class TestCaseMapServiceImpl implements ITestCaseMapService {

	private Logger log = Logger.getLogger(TestCaseMapServiceImpl.class);

	@Autowired
	ITestCaseMapManager testCaseMapManager;

	@Autowired
	ITestScenarioStepManager testScenarioStepManager;

	@Autowired
	ITestScenariosManager testScenariosManager;

	@Autowired
	ITestCaseManager testCaseManager;

	@Autowired
	ITestStepManager testStepManager;

	@Autowired
	ITestCaseService testCaseService;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Autowired
	IReleaseTestCaseMappingService releaseTestCaseMappingService;

	@Override
	@Transactional
	public int addTestCaseMap(int clientProjectId, int releaseId,
			int testCaseId, String testCaseVersionId,
			String selectedTestStepIds, int testScenarioId,
			int testScenarioStepVersionId) throws APIExceptions {
		if (releaseId == 0) {
			if (testCaseId > 0 && testScenarioId > 0) {
				int count = 0;
				if (ValueValidations.isValueValid(selectedTestStepIds)) {
					String[] split = selectedTestStepIds.split(",");
					String temp = "";
					String separator = "";
					for (String s : split) {
						temp += separator + s.split(":")[0];
						separator = ",";
					}
					count = testCaseMapManager.persistTestCaseMap(testCaseId,
							temp, testScenarioId);
				}

				// Storing the test case mapping version info for latest test
				// cases and steps and scenario version
				TestCaseMapVersion testCaseMapVersion = new TestCaseMapVersion();
				testCaseMapVersion.setTestCaseId(testCaseId);
				testCaseMapVersion.setTestCaseVersionId(
						Integer.parseInt(testCaseVersionId.replace("V", "")));
				if (!ValueValidations.isValueValid(selectedTestStepIds)) {
					testCaseMapVersion.setTestScenarioStepVersionId(0);
					testCaseMapVersion.setSelectedTestStepsIdAndVersion(
							PintailerConstants.TEST_CASE_STEP_NO_MAPPING);
				} else {
					testCaseMapVersion.setTestScenarioStepVersionId(
							testScenarioStepVersionId);
					testCaseMapVersion.setSelectedTestStepsIdAndVersion(
							selectedTestStepIds);
				}
				testCaseMapVersion = testCaseMapManager
						.persistTestCaseMapVersion(testCaseMapVersion);
				return count;
			} else
				throw new APIExceptions(
						"Insufficient information provided for the test case and test step mapping");
		} else {
			// Storing the test case mapping version info for previous test
			// cases version
			TestCaseMapVersion testCaseMapVersion = new TestCaseMapVersion();
			testCaseMapVersion.setTestCaseId(testCaseId);
			testCaseMapVersion.setTestCaseVersionId(
					Integer.parseInt(testCaseVersionId.replace("V", "")));
			testCaseMapVersion
					.setTestScenarioStepVersionId(testScenarioStepVersionId);

			if (!ValueValidations.isValueValid(selectedTestStepIds)) {
				testCaseMapVersion.setSelectedTestStepsIdAndVersion(
						PintailerConstants.TEST_CASE_STEP_NO_MAPPING);
			} else {
				if (selectedTestStepIds.endsWith(",")) {
					selectedTestStepIds = selectedTestStepIds.substring(0,
							selectedTestStepIds.length());
				}
				testCaseMapVersion
						.setSelectedTestStepsIdAndVersion(selectedTestStepIds);
			}
			testCaseMapVersion = testCaseMapManager
					.persistTestCaseMapVersion(testCaseMapVersion);

			int testCaseMapVersionId = testCaseMapVersion
					.getTestCaseMapVersionId();

			/*
			 * Storing the version info of release and test case mapping
			 */
			ReleaseTestCaseMapping releaseTestCaseMapping = new ReleaseTestCaseMapping();
			releaseTestCaseMapping.setClientProjectId(clientProjectId);
			releaseTestCaseMapping.setReleaseId(releaseId);
			releaseTestCaseMapping.setTestCaseId(testCaseId);
			releaseTestCaseMapping.setTestCaseVersionId(
					Integer.parseInt(testCaseVersionId.replace("V", "")));
			releaseTestCaseMapping
					.setTestCaseMapVersionId(testCaseMapVersionId);
			releaseTestCaseMapping.setDeleted(false);

			releaseTestCaseMappingService
					.persistReleaseTestCaseMapping(releaseTestCaseMapping);
			/*************************/
			return 0;
		}
	}

	@Override
	@Transactional
	public void updateTestCaseMapById(TestCaseMap testCaseMap)
			throws APIExceptions {
		if (testCaseMap != null) {
			TestCaseMap clientProjects = testCaseMapManager
					.getTestCaseMapById(testCaseMap.getTestCaseMapId());
			testCaseMap.setCreatedBy(clientProjects.getCreatedBy());
			testCaseMapManager.updateTestCaseMapById(testCaseMap);
		}
	}

	@Override
	@Transactional
	public ResponseEntity<Void> deleteTestCaseMap(long testCaseMapId)
			throws APIExceptions {
		testCaseMapManager.deleteTestCaseMapById(testCaseMapId);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Override
	public List<TestCaseMap> getTestCaseMap(int projectId)
			throws APIExceptions {
		return testCaseMapManager.getTestCaseMappings(projectId);
	}

	@Override
	public TestCaseMap getTestCaseMapById(long id) throws APIExceptions {
		return testCaseMapManager.getTestCaseMapById(id);
	}

	/*
	 * This returns the test steps which are mapped to the test case. In case of
	 * release id is given, the details will be fetched from
	 * release_testcaseMap_version table where single test case with all its
	 * test steps info will be stored in single row. This also contains the
	 * version details of test cases, test steps and test scenario step mapping.
	 */
	@Override
	public List<TestCaseMap> getTestStepByTestCaseId(int clientProjectId,
			int releaseId, int testCaseId, String testCaseVersionId,
			boolean isReleaseTCMapMapping) throws APIExceptions {
		List<TestCaseMap> testScenarioStep = new ArrayList<TestCaseMap>();
		if (releaseId <= 0) {
			testScenarioStep = testCaseMapManager
					.getTestCaseMapByTestCaseId(testCaseId);
		} else {
			/*
			 * Flow -> Release - Test Case :: Mapping versions contains -> Test
			 * case - Test step :: Mapping versions Info which contains -> Test
			 * scenario - Test step :: mapping version info, Selected Test step
			 * Ids - Version
			 */
			int testCaseMapVersionId = 0;
			TestCaseMapVersion testCaseMapVersion = null;
			if (isReleaseTCMapMapping) {
				/*
				 * List of test case mapping versions based on selected test
				 * case Id and version
				 */
				List<TestCaseMapVersion> testCaseMapVersionList = testCaseMapManager
						.getTestCaseMapVersion(testCaseId, Integer
								.parseInt(testCaseVersionId.replace("V", "")));

				/*
				 * Return empty value if test case mapping not found or the
				 * mapping was deleted. In case of deleted, the data is removed
				 * from the master table but in version table a new entry will
				 * be added with selected test step id version as 'no_map'.
				 */
				if (null == testCaseMapVersionList || testCaseMapVersionList.isEmpty()
						|| testCaseMapVersionList.get(0)
								.getSelectedTestStepsIdAndVersion()
								.equals(PintailerConstants.TEST_CASE_STEP_NO_MAPPING)) {
					return testScenarioStep;
				}
				/*
				 * fetching test case map version id used for that specific test
				 * case id, version and test step mapping
				 */
//				testCaseMapVersionId = testCaseMapVersionList.get(0)
//						.getTestCaseMapVersionId();
				
				testCaseMapVersion = testCaseMapVersionList.get(0);
			} else {

				/*
				 * List of release and test case mapping versions based on
				 * selected test case version and release id
				 */
				List<ReleaseTestCaseMapping> releaseTestCaseMapping = releaseTestCaseMappingService
						.getReleaseTestCaseMapping(clientProjectId, releaseId,
								testCaseId, Integer.parseInt(
										testCaseVersionId.replace("V", "")));
				if (null == releaseTestCaseMapping
						|| releaseTestCaseMapping.isEmpty()) {
					// This condition will generally meet when a test case of
					// particular version is mapped to the release id. In that
					// case
					// if mapping is available for that specific version, it
					// will be
					// returned. This is the case when a old version (having
					// mapping) is mapped to the release and according to the
					// logic
					// the release and test case mapping version info will be
					// added
					// automatically so that user dont have to do the mapping
					// again.
					return testScenarioStep;
				}
				// fetching test case map version id used for that specific test
				// case id, version and test step mapping
				testCaseMapVersionId = releaseTestCaseMapping.get(0)
						.getTestCaseMapVersionId();
				
				// fetching the specific test case mapping version to get selected
				// step (possibly lower version) and also the scenario, scenario
				// version and scenario - step mapping
				testCaseMapVersion = testCaseMapManager
						.getTestCaseMapVersion(testCaseMapVersionId);
			}

			

			if (!ValueValidations.isValueValid(
					testCaseMapVersion.getSelectedTestStepsIdAndVersion())
					|| testCaseMapVersion.getSelectedTestStepsIdAndVersion()
							.equals(PintailerConstants.TEST_CASE_STEP_NO_MAPPING)) {
				return testScenarioStep;
			}

			// scenario version and scenario-step mapping version
			int testScenarioStepVersionId = testCaseMapVersion
					.getTestScenarioStepVersionId();

			// Getting scenario and step mapping specific version
			TestScenarioStepVersion scenarioStepMappingVersion = testScenarioStepManager
					.getScenarioStepMappingVersion(testScenarioStepVersionId);
			int testScenariosId = scenarioStepMappingVersion
					.getTestScenariosId();
//			int testScenariosVersionId = scenarioStepMappingVersion
//					.getTestScenariosVersionId();
			// selected test steps info
			String selectedTestStepsIdAndVersion = testCaseMapVersion
					.getSelectedTestStepsIdAndVersion();
			String[] split = selectedTestStepsIdAndVersion.split(",");
			for (String step : split) {
				/*
				 * in test case map table, individual data rows are added for
				 * test case Id s and test steps Id s for specific test
				 * scenario. In the test case mapping version table, a single
				 * row is added for a test case id and all the selected test
				 * steps along with their versions are stored as comma separated
				 * string value
				 */
				TestCaseMap testCaseMap = new TestCaseMap();
				testCaseMap.setTestCaseMapId(testCaseMapVersion.getTestCaseMapVersionId());
				testCaseMap.setTestCaseId(testCaseId);
				testCaseMap.setTestCaseVersionId(testCaseVersionId);
				testCaseMap.setTestScenarioId(testScenariosId);
				testCaseMap.setTestScenarioStepVersionId(
						testScenarioStepVersionId);
				testCaseMap.setTestStepId(Long.parseLong(step.split("::")[0]));
				testCaseMap.setTestStepVersionId(step.split("::")[1]);
				testScenarioStep.add(testCaseMap);
			}
		}
		return testScenarioStep;
	}

	@Override
	public Map<Integer, List<TestCaseMap>> getTestStepByTestCaseIds(
			String testCaseIds) throws APIExceptions {
		Map<Integer, List<TestCaseMap>> testScenarioSteps = new LinkedHashMap<Integer, List<TestCaseMap>>();
		testScenarioSteps = testCaseMapManager
				.getTestCaseMapByTestCaseIds(testCaseIds);
		return testScenarioSteps;
	}

	@Override
	@Transactional
	public ResponseEntity<Void> deleteTestStepByTestCaseId(int clientProjectId,
			int releaseId, int testCaseId, int testScenarioID)
			throws APIExceptions {
		if (releaseId <= 0) {
			testCaseMapManager.deleteTestCaseMapByTestCaseIdAndScenarioId(
					testCaseId, testScenarioID);
		}
//		releaseTestCaseMappingService.deleteReleaseMap(clientProjectId,
//				releaseId, "" + testCaseId);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Override
	@Transactional
	public void automaticMap(ArrayList<Object> data, int clientProjectId)
			throws APIExceptions {

		// List of hashcode of active test cases available in the DB
		Map<String, Integer> activeTestCaseHashCode = testCaseManager
				.getTestCaseHashCode(false, false, true);
		if (null == activeTestCaseHashCode) {
			activeTestCaseHashCode = new LinkedHashMap<String, Integer>();
		}

		// List of hashcode of in active test cases available in the DB
		Map<String, Integer> inActiveTestCaseHashCode = testCaseManager
				.getTestCaseHashCode(false, false, false);
		if (null == inActiveTestCaseHashCode) {
			inActiveTestCaseHashCode = new LinkedHashMap<String, Integer>();
		}

		// List of hashcode of test steps available in the DB
		Map<String, Long> testStepHashCode = testStepManager
				.getTestStepHashCode();
		if (null == testStepHashCode) {
			testStepHashCode = new LinkedHashMap<String, Long>();
		}

		List<TestCaseMap> testCaseMapList = testCaseMapManager
				.getTestCaseMappings(clientProjectId);
		List<String> existingMap = new ArrayList<String>();
		for (TestCaseMap testCaseMap : testCaseMapList) {
			existingMap.add(testCaseMap.getTestCaseId() + ","
					+ testCaseMap.getTestStepId() + ","
					+ testCaseMap.getTestScenarioId());
		}

		// Fetching the data having manual vs automation steps info
		@SuppressWarnings("unchecked")
		Map<TestCase, ArrayList<String>> manualVsAutomatedSteps = (Map<TestCase, ArrayList<String>>) (data
				.get(0));

		Set<TestCase> keySet = manualVsAutomatedSteps.keySet();
		int testCaseId = 0;
		for (TestCase testCase : keySet) {
			// Insert manual test case if does not exist in DB
			if (activeTestCaseHashCode.keySet()
					.contains(testCase.getHashCode())) {
				testCaseId = activeTestCaseHashCode.get(testCase.getHashCode());
			} else if (inActiveTestCaseHashCode.keySet()
					.contains(testCase.getHashCode())) {
				// Re activating the test case if it was set inactive/not
				// application through automatic test case creation or through
				// manual process.
				testCaseId = inActiveTestCaseHashCode
						.get(testCase.getHashCode());
				TestCase inactiveTestCase = testCaseManager
						.getTestCaseById(testCaseId, "false");
				inactiveTestCase.setApplicable(true);
				inactiveTestCase
						.setModifiedBy(applicationCommonUtil.getCurrentUser());
				int updateTestCaseById = testCaseManager
						.updateTestCaseById(inactiveTestCase);
				if (updateTestCaseById <= 0) {
					throw new APIExceptions(
							"Error occured while re activating the test case. "
									+ "Application behaviour will be unexpected.");
				} else {
					log.info("The test case with id [" + testCaseId
							+ "] is re activated");
				}

			} else {
				// Insert test case and get test id
				testCaseId = testCaseManager.persistTestCase(testCase)
						.getTestCaseId();
				// Insert into map to avoid duplicate insert
				activeTestCaseHashCode.put(testCase.getHashCode(), testCaseId);
			}

			ArrayList<String> arrayList = manualVsAutomatedSteps.get(testCase);
			String selectedTestStepIds = "";
			String separator = "";
			boolean isMappingExist = false;
			TestCaseMap testCaseMap = null;
			for (String step : arrayList) {
				String testStepHash = step.split(
						PintailerConstants.READ_FILE_OBJECT_SEPARATOR)[0];
				String testStepText = step.split(
						PintailerConstants.READ_FILE_OBJECT_SEPARATOR)[2];

				long testStepId = 0l;
				// Insert automated test case if does not exist in DB
				TestStep testStep = null;
				if (testStepHashCode.keySet().contains(testStepHash)) {
					testStepId = testStepHashCode.get(testStepHash);
				} else {
					// Insert test step and get test id
					testStep = new TestStep();
					testStep.setName(testStepText);
					testStep.setHashCode(testStepHash);
					testStep.setCreatedBy(
							applicationCommonUtil.getCurrentUser());
					testStep.setModifiedBy(
							applicationCommonUtil.getCurrentUser());
					testStep.setClientProjectId(clientProjectId);
					testStepId = testStepManager.persistTestStep(testStep)
							.getTestStepId();

					testStepHashCode.put(testStepHash, testStepId);
				}

				testStep = testStepManager.getTestStepById(testStepId,
						clientProjectId);
				selectedTestStepIds += separator + testStep.getTestStepId()
						+ "::" + testStep.getStepLatestVersion();
				separator = ",";

				testCaseMap = new TestCaseMap();
				testCaseMap.setTestCaseId(testCaseId);
				testCaseMap.setTestStepId(testStepId);
				testCaseMap.setTestScenarioId(testScenariosManager
						.getTestScenarioIdByScenarioAndFeatureFile(
								clientProjectId,
								step.split(
										PintailerConstants.READ_FILE_OBJECT_SEPARATOR)[4],
								step.split(
										PintailerConstants.READ_FILE_OBJECT_SEPARATOR)[3]));
				testCaseMap
						.setCreatedBy(applicationCommonUtil.getCurrentUser());
				testCaseMap
						.setModifiedBy(applicationCommonUtil.getCurrentUser());

				// To avoid duplicate mapping entry
				if (testCaseMap.getTestScenarioId() != 0
						&& !existingMap.contains(testCaseId + "," + testStepId
								+ "," + testCaseMap.getTestScenarioId())) {
					testCaseMapManager.persistTestCaseMap(testCaseId,
							"" + testStepId, testCaseMap.getTestScenarioId());

					existingMap.add(testCaseId + "," + testStepId + ","
							+ testCaseMap.getTestScenarioId());
					isMappingExist = true;
				}
			}
			// Creating mapping version data
			// Storing the test case mapping version info for latest
			// test cases and steps and scenario version
			if (isMappingExist) {
				TestCaseMapVersion testCaseMapVersion = new TestCaseMapVersion();
				testCaseMapVersion.setTestCaseId(testCaseId);
				TestCase testCaseById = testCaseManager
						.getTestCaseById(testCaseId, "false");
				testCaseMapVersion.setTestCaseVersionId(Integer.parseInt(
						testCaseById.getLatestVersion().replace("V", "")));

				List<TestScenarioStepVersion> scenarioStepMappingVersion = testScenarioStepManager
						.getScenarioStepMappingVersion(
								testCaseMap.getTestScenarioId(),
								Integer.parseInt(testScenariosManager
										.getTestScenariosById(
												testCaseMap.getTestScenarioId(),
												"false")
										.getScenarioLatestVersion()
										.replace("V", "")));
				testCaseMapVersion
						.setTestScenarioStepVersionId(scenarioStepMappingVersion
								.get(0).getTestScenarioStepVersionId());

				testCaseMapVersion
						.setSelectedTestStepsIdAndVersion(selectedTestStepIds);
				if (null == testCaseMapManager
						.persistTestCaseMapVersion(testCaseMapVersion)) {
					throw new APIExceptions(
							"Error occured while storing the auto map version "
									+ "info while importing the feature file.");
				}

				// TODO : release test case mapping handling
			}
		}
	}

	@Override
	public List<TestCaseMapVersion> getTestCaseMapVersion(int testCaseId,
			int testCaseVersionId) throws APIExceptions {
		return testCaseMapManager.getTestCaseMapVersion(testCaseId,
				testCaseVersionId);
	}

	@Override
	public TestCaseMapVersion getTestCaseExistingMappedScenarioInfo(
			int clientProjectId, int releaseId, int testCaseId,
			String testCaseVersionId) throws APIExceptions {
		TestCaseMapVersion testCaseMapVersion = null;
		if (releaseId <= 0) {
			List<TestCaseMap> testCaseMapList = testCaseMapManager
					.getTestCaseMapByTestCaseId(testCaseId);
			if (null == testCaseMapList || testCaseMapList.isEmpty()) {
				return new TestCaseMapVersion();
			}
//			testCaseMapVersion = getTestCaseMapVersion(testCaseId,
//					Integer.parseInt(testCaseVersionId.replace("V", "")))
//							.get(0);

			TestScenarios testScenarios = testScenariosManager
					.getTestScenariosById(
							testCaseMapList.get(0).getTestScenarioId(),
							"false");
//			testScenarios.setScenarioSelectedVersion(
//					"V" + testScenarios.getScenarioLatestVersion());
			testCaseMapVersion = new TestCaseMapVersion();
			testCaseMapVersion.setTestCaseId(testCaseId);
			testCaseMapVersion.setTestCaseVersionId(
					Integer.parseInt(testCaseVersionId.replace("V", "")));
			testCaseMapVersion.setTestScenarios(testScenarios);
			return testCaseMapVersion;
		} else {
			List<ReleaseTestCaseMapping> releaseTestCaseMapping = releaseTestCaseMappingService
					.getReleaseTestCaseMapping(clientProjectId, releaseId,
							testCaseId, Integer.parseInt(
									testCaseVersionId.replace("V", "")));

			/* Fetching test case mapping versions */
			if (null == releaseTestCaseMapping
					|| releaseTestCaseMapping.isEmpty()) {
				testCaseMapVersion = getTestCaseMapVersion(testCaseId,
						Integer.parseInt(testCaseVersionId.replace("V", "")))
								.get(0);
//				List<TestCaseMapVersion> testCaseMapVersionList = getTestCaseMapVersion(testCaseId,
//						Integer.parseInt(testCaseVersionId.replace("V", "")));
//				if(null==testCaseMapVersionList || testCaseMapVersionList.isEmpty()) {
//					return null;
//				}
//				testCaseMapVersion = testCaseMapVersionList
//								.get(0);
			} else {
				int testCaseMapVersionId = releaseTestCaseMapping.get(0)
						.getTestCaseMapVersionId();
				testCaseMapVersion = testCaseMapManager
						.getTestCaseMapVersion(testCaseMapVersionId);
			}

			if (testCaseMapVersion.getSelectedTestStepsIdAndVersion()
					.equals(PintailerConstants.TEST_CASE_STEP_NO_MAPPING)) {
				return null;
			}

			/* Fetching the test scenario and step mapping latest version */
			TestScenarioStepVersion scenarioStepMappingVersion = testScenarioStepManager
					.getScenarioStepMappingVersion(
							testCaseMapVersion.getTestScenarioStepVersionId());
			/* Fetching mapped scenario id and version */
			TestScenarios testScenarios = testScenariosManager
					.getTestScenariosById(
							scenarioStepMappingVersion.getTestScenariosId(),
							"false");
			testScenarios.setScenarioSelectedVersion("V"
					+ scenarioStepMappingVersion.getTestScenariosVersionId());
			testCaseMapVersion.setTestScenarios(testScenarios);
			return testCaseMapVersion;
		}
	}

	@Override
	public List<TestCaseMapVersion> getTestCaseMapMaxVersionForSpecificPeriod(
			int clientProjectId, String startDate, String endDate)
			throws APIExceptions {
		return testCaseMapManager.getTestCaseMapMaxVersionForSpecificPeriod(
				clientProjectId, startDate, endDate);
	}
}
