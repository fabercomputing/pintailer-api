package com.fw.services.impl;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import com.fw.dao.*;
import com.fw.domain.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fw.bean.ExecutionDataBean;
import com.fw.enums.TestResults;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IReleaseMapService;
import com.fw.services.IReleaseTestCaseMappingService;
import com.fw.services.ITestCaseExecutionsService;
import com.fw.services.ITestCaseService;
import com.fw.services.ITestScenarioStepService;
import com.fw.utils.ApplicationCommonUtil;
import com.fw.utils.GenerateExecutionReports;
import com.fw.utils.GenerateUniqueHash;
import com.fw.utils.ValueValidations;

@Service
public class TestCaseExecutionsServiceImpl
		implements ITestCaseExecutionsService {

	private Logger log = Logger.getLogger(TestCaseExecutionsServiceImpl.class);

	@Autowired
	IReleaseMapManager releaseMapManager;

	@Autowired
	ITestCaseExecutionsManager testCaseExecutionsManager;

	@Autowired
	ITestScenariosManager testScenarioManager;

	@Autowired
	ITestCaseMapManager testCaseMapManager;

	@Autowired
	IClientProjectsManager clientProjectsManagerImpl;

	@Autowired
	ILinkedTicketManager linkedTicketManagerImpl;

	@Autowired
	ITestStepManager testStepManagerImpl;

	@Autowired
	ITestCaseManager testCaseManagerImpl;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	@Transactional
	public List<TestCaseExecutions> addTestCaseExecutions(
			TestCaseExecutions logEntity) throws APIExceptions {
		if (logEntity != null) {
			List<TestCaseExecutions> testCaseExecutions = new ArrayList<TestCaseExecutions>();
			List<TestCaseMap> testCaseMapByTestCaseId = testCaseMapManager
					.getTestCaseMapByTestCaseId(logEntity.getTestCaseId());
			// If automated test case is executed manually, then execution data
			// will be generated according to the number of step mapped to the
			// particular test case
			boolean firstTime = true;
			if (!(null == testCaseMapByTestCaseId
					|| testCaseMapByTestCaseId.isEmpty())) {
				for (TestCaseMap testCaseMap : testCaseMapByTestCaseId) {
					logEntity.setTestStepId(testCaseMap.getTestStepId());
					testCaseExecutions.add(testCaseExecutionsManager
							.persistTestCaseExecutions(logEntity));
					// Saving the linked bug information for the current
					// execution for one time only if multiple test steps are
					// map to the same test case
					if (firstTime) {
						createLinkedTicket(testCaseExecutions.get(0));
						firstTime = false;
					}
				}
			} else {
				// Saving the linked bug information for the current
				// execution
				testCaseExecutions.add(testCaseExecutionsManager
						.persistTestCaseExecutions(logEntity));
				createLinkedTicket(testCaseExecutions.get(0));
			}
			return testCaseExecutions;
		} else
			return null;
	}

	// add the linked bug against an execution if in DB. Previously it was
	// associated with test case id but with new requirement the association is
	// updated with execution id
	@Transactional
	private void createLinkedTicket(final TestCaseExecutions testCaseExecutions)
			throws APIExceptions {
		String bug = testCaseExecutions.getLinkedBug();
		if (null == bug || bug.trim().equals("") || bug.trim().equals("null")) {
			return;
		}
		bug = bug.trim();
		String[] linkedDefectsArr = bug.split(",");
		for (int i = 0; i < linkedDefectsArr.length; i++) {
			LinkedTicket linkedTicket = new LinkedTicket();
			linkedTicket.setTestExecutionId(
					testCaseExecutions.getTestCaseExecutionsId());
			linkedTicket.setTicketNumber(linkedDefectsArr[i].trim());
			linkedTicket.setApplicable(true);
			linkedTicket.setCreatedBy(testCaseExecutions.getTestRunBy());
			linkedTicket.setModifiedBy(testCaseExecutions.getTestRunBy());

			try {
				linkedTicketManagerImpl.persistLinkedTicket(linkedTicket);
			} catch (APIExceptions e) {
				throw new APIExceptions(
						"Error occured while saving the linked bug for the current execution : "
								+ e.getMessage());
			}
		}
	}

	@Override
	public List<TestCaseExecutions> getTestCaseExecutions()
			throws APIExceptions {
		return testCaseExecutionsManager.getAllTestCaseExecutions();
	}

	@Override
	public TestCaseExecutions getTestCaseExecutionsById(long executionId,
			int testCaseId, int releaseId, int environmentId)
			throws APIExceptions {
		return testCaseExecutionsManager.getTestCaseExecutionsById(executionId,
				testCaseId, releaseId, environmentId);
	}

	@Override
	@Transactional
	public List<TestCaseExecutions> importTestCaseExecutions(
			MultipartFile uploadfile, int clientProjectId,
			final int environmentId, final int releaseId, boolean isSync)
			throws APIExceptions, Exception {
//		String projectName = clientProjectsManagerImpl
//				.getClientProjectsById(clientProjectId).getName();
		String fileType = uploadfile.getOriginalFilename().substring(
				uploadfile.getOriginalFilename().lastIndexOf(".") + 1);
		ArrayList<ExecutionDataBean> results;

		if (fileType.equalsIgnoreCase("json")) {
			results = GenerateExecutionReports.readJSON(uploadfile);
		} else if (fileType.equalsIgnoreCase("xml")) {
			results = GenerateExecutionReports.readXML(uploadfile);
		} else if (fileType.equalsIgnoreCase("csv")) {
			results = GenerateExecutionReports
					.processExecutionCSVFile(uploadfile);
		} else {
			return null;
		}

		if (null == results || results.size() == 0) {
			throw new APIExceptions(
					"Imported execution file is not valid. System is not able to fetch any detail from it.");
		}

		if (!fileType.equalsIgnoreCase("csv")) {
//			importDataFromJSONAndXML(results, clientProjectId, environmentId,
//					releaseId, projectName, isSync);
			importDataFromJSONAndXMLForReleaseTCMappingVersion(clientProjectId,
					releaseId, environmentId, results, isSync);
		} else {
			importDataFromCSV(results, clientProjectId, environmentId,
					releaseId);
		}

		return null;
	}

	@Override
	@Transactional
	public void importTestCaseExecutionsNew(
			MultipartFile uploadfile, int clientProjectId,
			final int environmentId, final int releaseId, boolean isSync)
			throws APIExceptions, Exception {
		String fileType = uploadfile.getOriginalFilename().substring(
				uploadfile.getOriginalFilename().lastIndexOf(".") + 1);
		ArrayList<ExecutionDataBean> results;

		if (fileType.equalsIgnoreCase("json")) {
			results = GenerateExecutionReports.readJSON(uploadfile);
		} else if (fileType.equalsIgnoreCase("xml")) {
			results = GenerateExecutionReports.readXMLNew(uploadfile);
		} else if (fileType.equalsIgnoreCase("csv")) {
			results = GenerateExecutionReports
					.processExecutionCSVFile(uploadfile);
		} else {
			return;
		}

		if (results.size() == 0) {
			throw new APIExceptions(
					"Imported execution file is not valid. System is not able to fetch any detail from it.");
		}

		if (!fileType.equalsIgnoreCase("csv")) {
			importDataFromJSONAndXMLForReleaseTCMappingVersionNew(clientProjectId,
					releaseId, environmentId, results);
		} else {
			importDataFromCSV(results, clientProjectId, environmentId,
					releaseId);
		}

	}

	@Transactional
	private int importDataFromCSV(final ArrayList<ExecutionDataBean> results,
			final int clientProjectId, final int environmentId,
			final int releaseId) throws APIExceptions {
		try {
			int totalDataAdded = 0;
			for (ExecutionDataBean result : results) {
				// in case user import the test cases which are already
				// automated
				// but has to be execute manually
				long testStepId = 0;
				boolean isLinkedBugAdded = false;
				if (null != result.getMappedTestStepDefinitions()) {
					for (String stepDefinition : result
							.getMappedTestStepDefinitions()) {
						TestStep testStep = testStepManagerImpl
								.getTestStepIdByHashCode(GenerateUniqueHash
										.getTestStepHash(clientProjectId,
												stepDefinition));
						if (null != testStep) {
							List<TestCaseMap> testCaseMap = testCaseMapManager
									.getTestCaseMapByTestCaseIdAndTestStepId(
											result.getTestCaseId(),
											testStep.getTestStepId(),
											clientProjectId);
							if (null != testCaseMap && testCaseMap.size() > 0) {
								testStepId = testStep.getTestStepId();
								TestCaseExecutions bean = getBean(new Object[] {
										result.getTestCaseId(), testStepId,
										applicationCommonUtil.getCurrentUser(),
										result.getStartTime(),
										result.getStatus(), environmentId,
										result.getDuration(),
										applicationCommonUtil.getCurrentUser(),
										applicationCommonUtil.getCurrentUser(),
										false, result.getRemarks(),
										releaseId });
								bean = testCaseExecutionsManager
										.persistTestCaseExecutions(bean);

								// Storing the linked ticket information to
								// database
								// for specific execution id
								bean.setLinkedBug(result.getLinkedDefects());
								if (!isLinkedBugAdded) {
									createLinkedTicket(bean);
									isLinkedBugAdded = true;
								}
								totalDataAdded++;
							}
						} else {
							log.error("Test step with description ["
									+ stepDefinition + "] does not exists");
						}
					}
				}

				if (testStepId <= 0) {
					// Creating list of object array containing arguments for
					// the
					// execution table query
					TestCaseExecutions bean = getBean(
							new Object[] { result.getTestCaseId(), 0,
									applicationCommonUtil.getCurrentUser(),
									result.getStartTime(), result.getStatus(),
									environmentId, result.getDuration(),
									applicationCommonUtil.getCurrentUser(),
									applicationCommonUtil.getCurrentUser(),
									false, result.getRemarks(), releaseId });
					bean = testCaseExecutionsManager
							.persistTestCaseExecutions(bean);
					// Storing the linked ticket information to database for
					// specific execution id
					bean.setLinkedBug(result.getLinkedDefects());
					if (!isLinkedBugAdded) {
						createLinkedTicket(bean);
						isLinkedBugAdded = true;
					}
					totalDataAdded++;
				}
			}
			return totalDataAdded;
		} catch (Exception e) {
			String message = "Some error occured while fetching the test case "
					+ "execution details from CSV: " + e.getMessage();
			log.info(message);
			throw new APIExceptions(message);
		}
	}

	private TestCaseExecutions getBean(Object[] args) throws APIExceptions {
		try {
			TestCaseExecutions testCaseExecutions = new TestCaseExecutions();
			testCaseExecutions
					.setTestCaseId(Integer.parseInt(args[0].toString()));
			testCaseExecutions
					.setTestStepId(Long.parseLong(args[1].toString()));
			testCaseExecutions.setTestRunBy(args[2].toString());
			testCaseExecutions.setExecutionDate((Timestamp) args[3]);
			testCaseExecutions
					.setTestResult(TestResults.fromString(args[4].toString()));
			testCaseExecutions
					.setEnvironmentId(Integer.parseInt(args[5].toString()));
			testCaseExecutions
					.setActualLOE((int) Float.parseFloat(args[6].toString()));
			testCaseExecutions.setCreatedBy(args[7].toString());
			testCaseExecutions.setModifiedBy(args[8].toString());
			testCaseExecutions.setDeleted(Boolean.valueOf(args[9].toString()));
			testCaseExecutions.setActualResult(args[10].toString());
			testCaseExecutions
					.setReleaseId(Integer.parseInt(args[11].toString()));
			return testCaseExecutions;
		} catch (Exception e) {
			String message = "Some error occured while fetching the test case "
					+ "execution details : " + e.getMessage();
			log.info(message);
			throw new APIExceptions(message);
		}
	}

	@Transactional
	private int importDataFromJSONAndXML(
			final ArrayList<ExecutionDataBean> results,
			final int clientProjectId, final int environmentId,
			final int releaseId, String projectName, boolean isSync)
			throws APIExceptions {
		// query batch size
		int count = 0;

		// contains test case Id information based on test step id and scenario
		// ID in test case and test step mapping table
		List<TestCaseMap> testCaseMaps = null;

		List<Object[]> args = new ArrayList<Object[]>();
		int totalDataAdded = 0;
		boolean isExecutionReportValid = false;
		final String currentUser = applicationCommonUtil.getCurrentUser();
		for (ExecutionDataBean result : results) {
			// Hash code of the test step given in json, xml
			String stepHashCode = GenerateUniqueHash.getTestStepHash(
					clientProjectId, result.getStepDefinition());

			// Getting scenario ID from scenario table
			int testScenarioId = testScenarioManager
					.getTestScenarioIdByScenarioAndFeatureFile(clientProjectId,
							result.getScenarioName(), result.getFeatureName());
			if (testScenarioId == 0) {
				log.info("Test scenario is not available for project ["
						+ projectName + "] with test step ["
						+ result.getStepDefinition() + "] inside feature ["
						+ result.getFeatureName() + "] with scenario name ["
						+ result.getScenarioName() + "]");
				continue;
			}

			// Executing query to fetch test case id from mapping table. Here
			// isSync flag is correspondent to the more accurate execution
			// calculations based on the line number in feature file and the
			// cucumber execution file. If true, execution report will be read
			// based on the test step line number in the feature file. If found,
			// execution of the respective test case will be marked. This is to
			// handle the case where single test step is mapped to multiple test
			// cases. If not sync, all the test cases will be marked if the
			// common mapped test step is executed once. In sync, it will not
			// happen.
			if (isSync) {
				testCaseMaps = testCaseMapManager.getInfo(clientProjectId,
						stepHashCode, testScenarioId, result.getLineNumber());
			} else {
				testCaseMaps = testCaseMapManager.getInfo(clientProjectId,
						stepHashCode, testScenarioId, 0);
			}

			if (null == testCaseMaps || testCaseMaps.isEmpty()) {
				log.debug("Test case Id mapping is not available for project ["
						+ projectName + "] with test step ["
						+ result.getStepDefinition() + "] inside feature ["
						+ result.getFeatureName() + "] and scenario ["
						+ result.getScenarioName() + "]");
				continue;
			}

			isExecutionReportValid = true;

			// Creating list of object array containing arguments for the
			// execution table query
			String testCaseSummary = null;
			if (null != result.getStepComment()) {
				testCaseSummary = result.getStepComment();
				log.info("Saving execution info for test case : "
						+ testCaseSummary);
				for (int i = 0; i < testCaseMaps.size(); i++) {
					if (testCaseManagerImpl
							.getTestCaseById(
									testCaseMaps.get(i).getTestCaseId(),
									"false")
							.getTestSummary().trim().equals(testCaseSummary)) {
						args.add(new Object[] {
								testCaseMaps.get(i).getTestCaseId(),
								testCaseMaps.get(i).getTestStepId(),
								applicationCommonUtil.getCurrentUser(),
								result.getStartTime(), result.getStatus(),
								environmentId, result.getDuration(),
								currentUser, currentUser, false,
								result.getStatus(), releaseId,
								result.getStepKeyword() });
					}
				}
			} else {
				for (TestCaseMap testCaseMap : testCaseMaps) {
					args.add(new Object[] { testCaseMap.getTestCaseId(),
							testCaseMap.getTestStepId(),
							applicationCommonUtil.getCurrentUser(),
							result.getStartTime(), result.getStatus(),
							environmentId, result.getDuration(), currentUser,
							currentUser, false, result.getStatus(), releaseId,
							result.getStepKeyword() });
				}
			}

			if (args.isEmpty()) {
				throw new APIExceptions(
						"Test case details is not available for "
								+ "execution report processing. Please check the data "
								+ "for test case[" + testCaseSummary + "]");
			}

			if (count == 500) {
				count = 0;
				totalDataAdded += testCaseExecutionsManager
						.persistTestCaseExecutionsInBatch(args);
				args.clear();
			}
		}

		if (!isExecutionReportValid) {
			throw new APIExceptions("The execution file does not contain the "
					+ "information related to existing features in the system. "
					+ "The imported execution details are for feature file : ["
					+ results.get(0).getFeatureName() + "]");
		}
		// insert remaining data
		totalDataAdded += testCaseExecutionsManager
				.persistTestCaseExecutionsInBatch(args);
		return totalDataAdded;
	}

	@Override
	public List<TestCaseExecutions> getAllLatestTestCaseExecutions(
			int releaseId, int environmentId, int testCaseId,
			String testStepIds) throws APIExceptions {
		return testCaseExecutionsManager.getAllLatestTestCaseExecutions(
				releaseId, environmentId, testCaseId, testStepIds);
	}

	@Override
	public Map<Integer, List<TestCaseExecutions>> getAllLatestTestCaseExecutions(
			int releaseId, int environmentId, String testCaseIds)
			throws APIExceptions {
		return testCaseExecutionsManager.getAllLatestTestCaseExecutions(
				releaseId, environmentId, testCaseIds);
	}

	@Autowired
	IReleaseMapService releaseMapService;

	@Autowired
	IReleaseTestCaseMappingService releaseTestCaseMappingService;

	@Autowired
	ITestScenarioStepService testScenarioStepService;

	@Autowired
	ITestCaseService testCaseService;

	private int importDataFromJSONAndXMLForReleaseTCMappingVersion(
			final int clientProjectId, final int releaseId,
			final int environmentId, final ArrayList<ExecutionDataBean> results,
			boolean isSync) throws APIExceptions {
		// Fetching release and test case (Id and version) relations
		List<TestCaseClientBean> releasesMaps = testCaseService
				.getTestCaseByModuleId(clientProjectId, releaseId, 0, "true",
						"false");
		List<Object[]> args = new ArrayList<Object[]>();
		String currentUser = applicationCommonUtil.getCurrentUser();
		int totalDataAdded = 0;
		int count = 0;
		String errorTestCaseIds = "";
		String separator = "";
		for (TestCaseClientBean testCaseClientBean : releasesMaps) {
			int testCaseId = testCaseClientBean.getTestCaseSequenceId();

			/* If test case is manual or */
			if (!testCaseClientBean.isAutomationMappingExists()) {
				log.info("Mapping is not available for the test case with id ["
						+ testCaseId + "]");
				continue;
			}
			String testCaseSelectedVersion = testCaseClientBean
					.getSelectedVersion();

			// Fetching the release and test case mapping relation for specific
			// test case and its version
			List<ReleaseTestCaseMapping> releaseTestCaseMapping = releaseTestCaseMappingService
					.getReleaseTestCaseMapping(clientProjectId, releaseId,
							testCaseId, Integer.parseInt(
									testCaseSelectedVersion.replace("V", "")));
			if (null == releaseTestCaseMapping
					|| releaseTestCaseMapping.isEmpty()) {
				errorTestCaseIds += separator + testCaseId + ":"
						+ testCaseSelectedVersion;
				separator = ",";
				continue;
			}
			int testCaseMapVersionId = releaseTestCaseMapping.get(0)
					.getTestCaseMapVersionId();

			// Fetching the scenario step mapping info and selected test step Id
			// s and their version info.
			TestCaseMapVersion testCaseMapVersion = testCaseMapManager
					.getTestCaseMapVersion(testCaseMapVersionId);
			int testScenarioStepVersionId = testCaseMapVersion
					.getTestScenarioStepVersionId();

			String[] selectedTestStepsIdAndVersion = testCaseMapVersion
					.getSelectedTestStepsIdAndVersion().split(",");

			// Fetching scenario info and selected test steps according to
			// selected version
			TestScenarioStepVersion scenarioStepMapping = testScenarioStepService
					.getScenarioStepMappingById(clientProjectId,
							testScenarioStepVersionId);
			TestScenarios testScenarios = scenarioStepMapping
					.getTestScenarios();
			List<TestStep> testSteps = scenarioStepMapping.getTestSteps();

			List<ExecutionDataBean> scenarioExecutionDataBean = results.stream()
					.filter(e -> e.getScenarioName()
							.equals(testScenarios.getName()))
					.collect(Collectors.toList());

			Map<String, Long> stepInfoMap = new LinkedHashMap<String, Long>();
			for (String stepAndVersion : selectedTestStepsIdAndVersion) {
				long stepId = Long.parseLong(stepAndVersion.split("::")[0]);
				String stepName = "";
				TestStep testStepById = testStepManagerImpl
						.getTestStepById(stepId, clientProjectId);
				stepName = testStepById.getName();
				if (!testStepById.getStepLatestVersion()
						.equals(stepAndVersion.split("::")[1])) {
					TestStepVersion testStepsVersionByStepIdAndVersionId = testStepManagerImpl
							.getTestStepsVersionByStepIdAndVersionId(stepId,
									Integer.parseInt(
											stepAndVersion.split("::")[1]
													.replace("V", "")));
					stepName = testStepsVersionByStepIdAndVersionId.getName();
				}
				if(stepName != null) {
				stepInfoMap.put(stepName.trim(), stepId);
			}
			}

//			int index = 0;
//			while (index < scenarioExecutionDataBean.size()) {
//				if (!testSteps.get(index).getName()
//						.equals(scenarioExecutionDataBean.get(index)
//								.getStepDescription())) {
//					throw new APIExceptions(
//							"Executed test steps are not in sync with the "
//							+ "configured test steps for release ["
//									+ releaseId + "]");
//				}

//				if()
			for (ExecutionDataBean executionDataBean : scenarioExecutionDataBean) {
				Long testStepId = stepInfoMap
						.get(executionDataBean.getStepDefinition());

				if (null == stepInfoMap
						.get(executionDataBean.getStepDefinition())
						|| testStepId == 0) {
					continue;
				}
				args.add(new Object[] { testCaseId, testStepId, currentUser,
						executionDataBean.getStartTime(),
						executionDataBean.getStatus(), environmentId,
						executionDataBean.getDuration(), currentUser,
						currentUser, false, executionDataBean.getStatus(),
						releaseId, executionDataBean.getStepKeyword() });

				if (count == 500) {
					count = 0;
					totalDataAdded += testCaseExecutionsManager
							.persistTestCaseExecutionsInBatch(args);
					args.clear();
				}
			}
		}

		// insert remaining data
		totalDataAdded += testCaseExecutionsManager
				.persistTestCaseExecutionsInBatch(args);
		if (totalDataAdded > 0) {
			if (ValueValidations.isValueValid(errorTestCaseIds)) {
				throw new APIExceptions(
						"Partial test case execution details added as for "
								+ "test case id and selected version ["
								+ errorTestCaseIds
								+ "] mapping is not available for the given release. "
								+ "Please map the different version or add "
								+ "the mapping for the selected test case version.");
			}
			return totalDataAdded;
		} else {
			throw new APIExceptions("The execution file does not contain the "
					+ "information related to existing features in the system. "
					+ "The imported execution details are for feature file : ["
					+ results.get(0).getFeatureName() + "]");
		}
	}

	private void addVersionInfo(AddReleaseMapBean addReleaseMapBean)
			throws APIExceptions {
		ReleaseMapVersion releaseMapVersion = new ReleaseMapVersion();
		releaseMapVersion.setReleaseId(addReleaseMapBean.getReleaseId());
		List<String> testCaseIds = addReleaseMapBean.getTestCaseIds();
		releaseMapVersion.setTestCaseIds(testCaseIds.stream()
				.map(Object::toString).collect(Collectors.joining(", ")));
		releaseMapManager.persistReleaseMapVersion(releaseMapVersion);
	}

	private void importDataFromJSONAndXMLForReleaseTCMappingVersionNew(
			final int clientProjectId, final int releaseId,
			final int environmentId, final ArrayList<ExecutionDataBean> results) throws APIExceptions {

		// Retrieve the list of TestCaseClientBean objects for the specified clientProjectId and releaseId
		List<TestCaseClientBean> releasesMaps = getTestCaseByModuleId(clientProjectId, releaseId);

		// do automatic mapping
		if (releasesMaps.isEmpty()) {
			List<TestCaseClientBean> testCasesList = testCaseService.getTestCaseList(
					clientProjectId, 0, String.valueOf(0), null, "true",
					null, null, null, null, 0,
					0, null, null, "false", false
			);

			List<Object[]> args = new ArrayList<>();
			AddReleaseMapBean addReleaseMapBean = new AddReleaseMapBean();
			String currentUser = applicationCommonUtil.getCurrentUser();
			addReleaseMapBean.setReleaseId(releaseId);
			addReleaseMapBean.setCreatedBy(currentUser);
			addReleaseMapBean.setModifiedBy(currentUser);

			List<String> testcaseIds = new ArrayList<>();
			for (TestCaseClientBean testCase : testCasesList) {
				String testCaseId = testCase.getTestCaseSequenceId() + "::" + testCase.getLatestVersion();
				testcaseIds.add(testCaseId);
			}
			addReleaseMapBean.setTestCaseIds(testcaseIds);

			int batchCount = 0;
			for (String testCaseId : addReleaseMapBean.getTestCaseIds()) {
				batchCount++;
				String[] testCaseInfo = testCaseId.split("::");
				if (!ValueValidations.isValueValid(testCaseInfo[0])
						|| testCaseInfo[0].equals("0")
						|| ValueValidations.isValueNull(testCaseInfo[1])) {
					throw new APIExceptions("Invalid test case id is given for release mapping");
				}
				args.add(new Object[] {
						addReleaseMapBean.getReleaseId(),
						Integer.parseInt(testCaseInfo[0]),
						Integer.parseInt(testCaseInfo[1].replace("V", "")),
						addReleaseMapBean.getCreatedBy(),
						addReleaseMapBean.getModifiedBy()
				});

				if (batchCount == 50) {
					releaseMapManager.persistReleaseMapInBatch(args);
					args.clear();
					batchCount = 0;
				}
			}

			releaseMapManager.persistReleaseMapInBatch(args);
			releaseTestCaseMappingService
					.persistReleaseTestCaseMappingInBatch(clientProjectId,
							addReleaseMapBean.getReleaseId(),
							addReleaseMapBean.getTestCaseIds());
			addVersionInfo(addReleaseMapBean);
			releasesMaps = getTestCaseByModuleId(clientProjectId, releaseId);
		}

		// Initialize variables
		List<Object[]> args = new ArrayList<>();
		String currentUser = applicationCommonUtil.getCurrentUser();
		int totalDataAdded = 0;
		int count = 0;
		StringBuilder errorTestCaseIds = new StringBuilder();
		String separator = "";

		// Iterate over each TestCaseClientBean
		for (TestCaseClientBean testCaseClientBean : releasesMaps) {
			int testCaseId = testCaseClientBean.getTestCaseSequenceId();

			// Skip the test case if automation mapping doesn't exist
			if (!testCaseClientBean.isAutomationMappingExists()) {
				logSkippedTestCase(testCaseId);
				continue;
			}

			String testCaseSelectedVersion = testCaseClientBean.getSelectedVersion();

			// Retrieve the list of ReleaseTestCaseMapping objects for the specified clientProjectId, releaseId, testCaseId, and testCaseSelectedVersion
			List<ReleaseTestCaseMapping> releaseTestCaseMapping = getReleaseTestCaseMapping(clientProjectId, releaseId, testCaseId, testCaseSelectedVersion);

			// If the mapping is not available, add the test case ID to the errorTestCaseIds string
			if (releaseTestCaseMapping.isEmpty()) {
				addErrorTestCaseId(errorTestCaseIds, separator, testCaseId, testCaseSelectedVersion);
				separator = ",";
				continue;
			}

			int testCaseMapVersionId = releaseTestCaseMapping.get(0).getTestCaseMapVersionId();

			// Retrieve the TestCaseMapVersion object for the specified testCaseMapVersionId
			TestCaseMapVersion testCaseMapVersion = getTestCaseMapVersion(testCaseMapVersionId);

			String[] selectedTestStepsIdAndVersion = testCaseMapVersion.getSelectedTestStepsIdAndVersion().split(",");

			// Filter the execution data beans based on the testCaseClientBean
			if(Objects.equals(testCaseClientBean.getAutomatedTestCaseNoFromFile(), "verify_turnaround_time_edit_functionality")){
				System.out.println(testCaseClientBean.getAutomatedTestCaseNoFromFile());
			}
			List<ExecutionDataBean> scenarioExecutionDataBean = filterExecutionDataBeans(results, testCaseClientBean);

			// Retrieve the step information map for the selected test steps
			Map<String, Long> stepInfoMap = getStepInfoMap(selectedTestStepsIdAndVersion, clientProjectId);

			// Iterate over each execution data bean
			for (ExecutionDataBean executionDataBean : scenarioExecutionDataBean) {
				Long testStepId = stepInfoMap.get(executionDataBean.getStepDefinition().toLowerCase());

				// If the test step ID is null or 0, check in description annotation
				if (testStepId == null || testStepId == 0) {
					testStepId = stepInfoMap.get(executionDataBean.getScenarioName().toLowerCase());
					if (testStepId == null || testStepId == 0) {
						continue;
					}
				}

				// Add the execution data to the arguments list for batch insertion
				args.add(new Object[]{
						testCaseId, testStepId, currentUser,
						executionDataBean.getStartTime(), executionDataBean.getStatus(),
						environmentId, executionDataBean.getDuration(),
						currentUser, currentUser, false,
						executionDataBean.getStatus(), releaseId,
						executionDataBean.getStepKeyword()
				});

				// Check if the batch size has reached the limit (500)
				if (++count == 500) {
					totalDataAdded += persistTestCaseExecutionsInBatch(args);
					count = 0;
					args.clear();
				}
			}
		}

		// Persist the remaining test case executions in the batch
		totalDataAdded += persistTestCaseExecutionsInBatch(args);

		if (totalDataAdded > 0) {
			// If some data was added and there are errorTestCaseIds, throw an exception with the error details
			if (errorTestCaseIds.length() > 0) {
				throw new APIExceptions("Partial test case execution details added as for " +
						"test case id and selected version [" + errorTestCaseIds.toString() +
						"] mapping is not available for the given release. " +
						"Please map the different version or add " +
						"the mapping for the selected test case version.");
			}
		} else {
			// If no data was added, throw an exception with the appropriate message
			throw new APIExceptions("The execution file does not contain the " +
					"information related to existing features in the system. " +
					"The imported execution details are for feature file : [" +
					results.get(0).getFeatureName() + "]");
		}
	}

	// Retrieve the list of TestCaseClientBean objects for the specified clientProjectId and releaseId
	private List<TestCaseClientBean> getTestCaseByModuleId(int clientProjectId, int releaseId) throws APIExceptions {
		return testCaseService.getTestCaseByModuleId(clientProjectId, releaseId, 0, "true", "false");
	}

	// Retrieve the list of ReleaseTestCaseMapping objects for the specified clientProjectId, releaseId, testCaseId, and testCaseSelectedVersion
	private List<ReleaseTestCaseMapping> getReleaseTestCaseMapping(int clientProjectId, int releaseId, int testCaseId, String testCaseSelectedVersion) throws APIExceptions {
		return releaseTestCaseMappingService.getReleaseTestCaseMapping(clientProjectId, releaseId, testCaseId, getVersionNumber(testCaseSelectedVersion));
	}

	// Retrieve the TestCaseMapVersion object for the specified testCaseMapVersionId
	private TestCaseMapVersion getTestCaseMapVersion(int testCaseMapVersionId) throws APIExceptions {
		return testCaseMapManager.getTestCaseMapVersion(testCaseMapVersionId);
	}

	// Filter the execution data beans based on the testCaseClientBean
	private List<ExecutionDataBean> filterExecutionDataBeans(ArrayList<ExecutionDataBean> results, TestCaseClientBean testCaseClientBean) {
		return results.stream()
				.filter(e -> (e.getStepDefinition().equalsIgnoreCase(testCaseClientBean.getAutomatedTestCaseNoFromFile())
						|| (!Objects.equals(e.getScenarioName(), "") && e.getScenarioName().equalsIgnoreCase(testCaseClientBean.getAutomatedTestCaseNoFromFile()))))
				.collect(Collectors.toList());
	}

	// Retrieve the step information map for the selected test steps
	private Map<String, Long> getStepInfoMap(String[] selectedTestStepsIdAndVersion, int clientProjectId) throws APIExceptions {
		Map<String, Long> stepInfoMap = new LinkedHashMap<>();

		for (String stepAndVersion : selectedTestStepsIdAndVersion) {
			long stepId = Long.parseLong(stepAndVersion.split("::")[0]);
			String stepName = getStepName(stepId, stepAndVersion, clientProjectId);

			if (stepName != null) {
				stepInfoMap.put(stepName.trim().toLowerCase(), stepId);
			}
		}

		return stepInfoMap;
	}

	// Retrieve the step name for the given stepId and stepAndVersion
	private String getStepName(long stepId, String stepAndVersion, int clientProjectId) throws APIExceptions {
		TestStep testStepById = testStepManagerImpl.getTestStepById(stepId, clientProjectId);
		String stepName = testStepById.getName();

		if (!testStepById.getStepLatestVersion().equals(stepAndVersion.split("::")[1])) {
			TestStepVersion testStepsVersionByStepIdAndVersionId = testStepManagerImpl
					.getTestStepsVersionByStepIdAndVersionId(stepId, getVersionNumber(stepAndVersion));
			stepName = testStepsVersionByStepIdAndVersionId.getName();
		}

		return stepName;
	}

	// Add the test case ID and selected version to the errorTestCaseIds string
	private void addErrorTestCaseId(StringBuilder errorTestCaseIds, String separator, int testCaseId, String testCaseSelectedVersion) {
		errorTestCaseIds.append(separator).append(testCaseId).append(":").append(testCaseSelectedVersion);
	}

	// Persist the test case executions in batch using the provided arguments
	private int persistTestCaseExecutionsInBatch(List<Object[]> args) throws APIExceptions {
		return testCaseExecutionsManager.persistTestCaseExecutionsInBatch(args);
	}

	// Extract the version number from the versionString
	private int getVersionNumber(String versionString) {
		String versionNumber = versionString.replace("V", "");
		return Integer.parseInt(versionNumber);
	}

	// Log a message indicating that the test case is skipped due to missing mapping
	private void logSkippedTestCase(int testCaseId) {
		log.info("Mapping is not available for the test case with id [" + testCaseId + "]");
	}

}
