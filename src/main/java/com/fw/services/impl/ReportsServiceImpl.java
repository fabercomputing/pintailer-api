package com.fw.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fw.dao.ITestStepManager;
import com.fw.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fw.bean.AutomationReportBean;
import com.fw.bean.ExecutionReportBean;
import com.fw.bean.ProgressReportBean;
import com.fw.dao.ILinkedTicketManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;
import com.fw.services.IClientProjectsService;
import com.fw.services.IModulesService;
import com.fw.services.IReportsService;
import com.fw.services.ITestCaseExecutionsService;
import com.fw.services.ITestCaseMapService;
import com.fw.services.ITestCaseService;
import com.fw.services.ITestStepService;
import com.fw.utils.ApplicationCommonUtil;
import com.fw.utils.ReadAndWriteReport;
import com.fw.utils.ValueValidations;

@Service
public class ReportsServiceImpl implements IReportsService {

	private Logger log = Logger.getLogger(ReportsServiceImpl.class);

	// @Autowired
	// ITestCaseManager testCaseManager;

	@Autowired
	ITestCaseService testCaseService;

	@Autowired
	IClientProjectsService clientProjectsService;

	@Autowired
	IModulesService modulesService;

	// @Autowired
	// ITestCaseMapManager testCaseMapManager;
	@Autowired
	ITestCaseMapService testCaseMapService;

	// @Autowired
	// ITestCaseExecutionsManager testCaseExecutionsManagerImpl;

	@Autowired
	ITestCaseExecutionsService testCaseExecutionsService;

	@Autowired
	ILinkedTicketManager linkedTicketManager;

	@Autowired
	ITestStepService testStepServiceImpl;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Autowired
	ITestStepManager testStepManager;

	// used while creating the execution report object
	public static final String EXECUTION_REPORT_KEYWORD_STATUS_PASS = "pass";
	public static final String EXECUTION_REPORT_KEYWORD_STATUS_FAIL = "fail";
	public static final String EXECUTION_REPORT_KEYWORD_STATUS_SKIP = "skip";
	public static final String EXECUTION_REPORT_KEYWORD_STATUS_PENDING = "pending";
	public static final String EXECUTION_REPORT_KEYWORD_STATUS_BLOCK = "block";

	@Override
	public List<DashboardGraph> getDashboardGraphReport() throws APIExceptions {
		try {
			List<ClientProjects> clientProjectsList = clientProjectsService
					.getAssignedClientProjects();
			if (null == clientProjectsList) {
				return null;
			}
			List<DashboardGraph> infoList = new ArrayList<DashboardGraph>();
			for (ClientProjects clientProjects : clientProjectsList) {
				AutomationReportBean automationReport = getAutomationReport(
						clientProjects.getClientProjectId(), null, 0, "all",
						null, null);

				DashboardGraph dashboardGraph = new DashboardGraph();
				dashboardGraph.setType("Total");
				dashboardGraph.setTestCasesCount(
						"" + automationReport.getTotalTestCaseIds().size());
				dashboardGraph.setProject(clientProjects.getName());
				infoList.add(dashboardGraph);

				dashboardGraph = new DashboardGraph();
				dashboardGraph.setType("Automatable");
				dashboardGraph.setTestCasesCount("" + automationReport
						.getTotalAutomatableTestCaseIds().size());
				dashboardGraph.setProject(clientProjects.getName());
				infoList.add(dashboardGraph);

				dashboardGraph = new DashboardGraph();
				dashboardGraph.setType("Automation Done");
				dashboardGraph.setTestCasesCount(
						"" + automationReport.getAutomatedTestCaseIds().size());
				dashboardGraph.setProject(clientProjects.getName());
				infoList.add(dashboardGraph);

				dashboardGraph = new DashboardGraph();
				dashboardGraph.setType("Automation Pending");
				dashboardGraph.setTestCasesCount("" + (automationReport
						.getPendingAutomatedTestCaseIds().size()));
				dashboardGraph.setProject(clientProjects.getName());
				infoList.add(dashboardGraph);

				dashboardGraph = new DashboardGraph();
				dashboardGraph.setType("Non-Automatable");
				dashboardGraph.setTestCasesCount(
						"" + (automationReport.getTotalTestCaseIds().size()
								- automationReport
										.getTotalAutomatableTestCaseIds()
										.size()));
				dashboardGraph.setProject(clientProjects.getName());
				infoList.add(dashboardGraph);
			}
			return infoList;
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			// Cannot determine target DataSource for lookup key [dorado]
			if (message.contains("Cannot determine target DataSource")) {
				String temp = message.substring(message.indexOf("[") + 1,
						message.indexOf("]"));
				message = "User default organization [" + temp
						+ "] configuration does not exist. Please contact "
						+ "administrator and try again later";
				throw new APIExceptions(message);
			} else {
				throw new APIExceptions(e.getMessage());
			}
		}
	}

	@Override
	public AutomationReportBean getAutomationReport(int clientProjectId, String tagValue, long moduleId, String applicable, String startDate, String endDate) throws APIExceptions {
		AutomationReportBean automationReportBean = new AutomationReportBean();
		List<TestCase> testCaseList;
		List<TestCaseMap> testCaseMapList;
		List<TestCaseMap> uniqueTestCaseMapList;

		if (null != tagValue && tagValue.equalsIgnoreCase("all")) {
			tagValue = null;
		}

		String moduleIds = null;
		if (moduleId != 0) {
			Modules module = modulesService.getModulesById(moduleId);
			log.info("Getting report for module [" + module.getName() + "]");

			List<Long> allChildModules = modulesService.getAllChildModules(moduleId);
			moduleIds = allChildModules.stream()
					.map(String::valueOf)
					.collect(Collectors.joining(","));
		}

		testCaseList = testCaseService.getAllTestCases(clientProjectId, 0, moduleIds, tagValue, applicable, null, null, null, null, 0, 0, startDate, endDate, "false");
		testCaseMapList = testCaseMapService.getTestCaseMap(clientProjectId);

		uniqueTestCaseMapList = testCaseMapList.stream()
				.collect(Collectors.toMap(TestCaseMap::getTestCaseId, Function.identity(), (a, b) -> a))
				.values()
				.stream()
				.collect(Collectors.toList());

		// Process test cases and build the report
		List<Integer> totalTestCaseIds = new ArrayList<>();
		List<Integer> totalAutomatableTestCaseIds = new ArrayList<>();
		List<Integer> totalManualTestCaseIds = new ArrayList<>();
		List<Integer> automatedTestCaseIds = new ArrayList<>();
		List<Integer> pendingAutomatedTestCaseIds = new ArrayList<>();
		Map<String, List<Integer>> testCaseIdsWithCriticality = new LinkedHashMap<>();
		Map<String, List<Integer>> automableTestCaseIdsWithCriticality = new LinkedHashMap<>();
		Map<String, List<Integer>> automatedTestCaseIdsWithCriticality = new LinkedHashMap<>();

		for (TestCase testCase : testCaseList) {
			totalTestCaseIds.add(testCase.getTestCaseId());
			String tags = String.join(",", testCase.getTags());

			if (testCase.isAutomatable()) {
				totalAutomatableTestCaseIds.add(testCase.getTestCaseId());
				boolean isTestCaseAutomated = uniqueTestCaseMapList.stream()
						.anyMatch(mappedTestCase -> {
							try {
								return mappedTestCase.getTestCaseId() == testCase.getTestCaseId() &&
										testStepManager.getTestStepById(mappedTestCase.getTestStepId(), clientProjectId).isApplicable();
							} catch (APIExceptions e) {
								throw new RuntimeException(e);
							}
						});

				if (isTestCaseAutomated) {
					automatedTestCaseIds.add(testCase.getTestCaseId());
				} else {
					pendingAutomatedTestCaseIds.add(testCase.getTestCaseId());
				}

				automableTestCaseIdsWithCriticality.computeIfAbsent(tags, k -> new ArrayList<>()).add(testCase.getTestCaseId());
			} else {
				totalManualTestCaseIds.add(testCase.getTestCaseId());
			}

			testCaseIdsWithCriticality.computeIfAbsent(tags, k -> new ArrayList<>()).add(testCase.getTestCaseId());

			uniqueTestCaseMapList.stream()
					.filter(mappedTestCase -> mappedTestCase.getTestCaseId() == testCase.getTestCaseId())
					.forEach(mappedTestCase -> automatedTestCaseIdsWithCriticality.computeIfAbsent(tags, k -> new ArrayList<>()).add(mappedTestCase.getTestCaseId()));
		}

		// Calculate percentages
		float totalTestCaseCount = totalTestCaseIds.size();
		float totalAutomatableTestCaseCount = totalAutomatableTestCaseIds.size();
		float totalManualTestCaseCount = totalManualTestCaseIds.size();

		float automatablePercentage = (totalAutomatableTestCaseCount * 100) / totalTestCaseCount;
		float automatedPercentage = (automatedTestCaseIds.size() * 100) / totalAutomatableTestCaseCount;
		float pendingAutomatedPercentage = (pendingAutomatedTestCaseIds.size() * 100) / totalAutomatableTestCaseCount;
		float manualPercentage = (totalManualTestCaseCount * 100) / totalTestCaseCount;

		log.info("Total active test cases count =" + totalTestCaseCount);
		log.info("Total test cases count which can be automated =" + totalAutomatableTestCaseCount);
		log.info("Automation done test cases count =" + automatedTestCaseIds.size());
		log.info("Test cases count for which automation has to be done=" + pendingAutomatedTestCaseIds.size());
		log.info("Total active manual test cases = " + totalManualTestCaseCount);
		log.info("Total test cases percentage which can be automated =" + automatablePercentage);
		log.info("Automation done test cases percentage =" + automatedPercentage);
		log.info("Test cases percentage for which automation has to be done=" + pendingAutomatedPercentage);
		log.info("Manual test cases percentage =" + manualPercentage);

		automationReportBean.setClientProjectId(clientProjectId);
		automationReportBean.setTotalTestCaseIds(totalTestCaseIds);
		automationReportBean.setTotalAutomatableTestCaseIds(totalAutomatableTestCaseIds);
		automationReportBean.setTotalManualTestCaseIds(totalManualTestCaseIds);
		automationReportBean.setAutomatedTestCaseIds(automatedTestCaseIds);
		automationReportBean.setPendingAutomatedTestCaseIds(pendingAutomatedTestCaseIds);
		automationReportBean.setTestCaseIdsWithCriticality(testCaseIdsWithCriticality);
		automationReportBean.setAutomableTestCaseIdsWithCriticality(automableTestCaseIdsWithCriticality);
		automationReportBean.setAutomatedTestCaseIdsWithCriticality(automatedTestCaseIdsWithCriticality);
		automationReportBean.setAutomatablePercentage(automatablePercentage);
		automationReportBean.setAutomatedPercentage(automatedPercentage);
		automationReportBean.setPendingAutomatedPercentage(pendingAutomatedPercentage);
		automationReportBean.setManualPercentage(manualPercentage);

		return automationReportBean;
	}


	@Override
	public ExecutionReportBean getExecutionReport(int clientProjectId,
			int moduleId, int releaseId, int environmentId, String applicable)
			throws APIExceptions {
		long t1 = System.currentTimeMillis();
		updateDataSource();
		ExecutionReportBean executionReportBean = new ExecutionReportBean();

		executionReportBean.setClientOrganisation(
				applicationCommonUtil.getDefaultOrgInOriginalCase());
		executionReportBean.setClientProjectId(clientProjectId);
		executionReportBean.setModuleId(moduleId);
		executionReportBean.setReleaseId(releaseId);
		executionReportBean.setEnvId(environmentId);

		// Getting module Ids based on the module selected on UI for execution
		// report. If module not selected query will be executed without module
		// condition.
		String moduleIdString = null;
		boolean firstTime = true;
		if (moduleId != 0) {
			Modules module = modulesService.getModulesById(moduleId);
			log.info("Getting report for module [" + module.getName() + "]");

			List<Long> allChildModules = modulesService
					.getAllChildModules(moduleId);
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < allChildModules.size(); i++) {
				if (firstTime) {
					builder.append(allChildModules.get(i));
					firstTime = false;
				} else {
					builder.append(",").append(allChildModules.get(i));
				}
			}
			moduleIdString = builder.toString();
		}

		// Get all test cases based on the filters on the UI. Here tags based
		// filter is not allowed
		List<TestCase> totalTestCaseList = testCaseService.getAllTestCases(
				clientProjectId, releaseId, moduleIdString, null, applicable,
				null, null, null, null, 0, 0, null, null, "false");
		// testCaseService.getAllTestCases(
		// clientProjectId, releaseId, moduleIdString, null, applicable,
		// null, null, null, null, 0, 0);

		// Map with key as test case id and value as map with key as test step
		// execution status and list of test step id s with that status
		Map<Integer, Map<String, List<String>>> testStepsStatus = new HashMap<Integer, Map<String, List<String>>>();

		// for overall status of the test case to be displayed on the execution
		// report. It is a map with key as test status i.e. fail , pass, skip
		// and value is list of test cases Ids with that status
		Map<String, List<String>> testCaseStatus = new HashMap<String, List<String>>();
		testCaseStatus.put(EXECUTION_REPORT_KEYWORD_STATUS_PASS,
				new ArrayList<String>());
		testCaseStatus.put(EXECUTION_REPORT_KEYWORD_STATUS_FAIL,
				new ArrayList<String>());
		testCaseStatus.put(EXECUTION_REPORT_KEYWORD_STATUS_SKIP,
				new ArrayList<String>());
		testCaseStatus.put(EXECUTION_REPORT_KEYWORD_STATUS_PENDING,
				new ArrayList<String>());
		testCaseStatus.put(EXECUTION_REPORT_KEYWORD_STATUS_BLOCK,
				new ArrayList<String>());
		testCaseStatus.put("executionRemaining", new ArrayList<String>());

		// Stores overall time taken for the execution of the test cases. Map
		// has key as test execution status i.e. pass, fail etc. and value as
		// sum of execution duration for all the test steps of all the test
		// cases
		Map<String, Integer> testCaseDuration = new LinkedHashMap<String, Integer>();
		// testCaseDuration is initialized for both pass and fail keys with 0
		// value as UI API expects both keys
		testCaseDuration.put(EXECUTION_REPORT_KEYWORD_STATUS_PASS, 0);
		testCaseDuration.put(EXECUTION_REPORT_KEYWORD_STATUS_FAIL, 0);
		testCaseDuration.put(EXECUTION_REPORT_KEYWORD_STATUS_SKIP, 0);
		testCaseDuration.put(EXECUTION_REPORT_KEYWORD_STATUS_PENDING, 0);
		testCaseDuration.put(EXECUTION_REPORT_KEYWORD_STATUS_BLOCK, 0);

		String testCaseType = "";
		String manualTestCaseType = "M";
		String automableOnlyTestCaseType = "R";
		String automatedTestCaseType = "D";
		boolean isMappingAvailable;
		long t3 = System.currentTimeMillis();
		// getting the list of the test cases
		String testCaseIds = null;
		boolean firstTime1 = true;
		for (TestCase testCase : totalTestCaseList) {
			if (firstTime1) {
				testCaseIds = "" + testCase.getTestCaseId();
				firstTime1 = false;
			} else {
				testCaseIds += "," + testCase.getTestCaseId();
			}
		}
		// getting all the test case and test step mappings to avoid multipe
		// query execution
		Map<Integer, List<TestCaseMap>> testStepByTestCaseIds = testCaseMapService
				.getTestStepByTestCaseIds(testCaseIds);
		if (null == testStepByTestCaseIds) {
			log.info("No mapping is available for the given set of test cases "
					+ "for the execution report.");
			testStepByTestCaseIds = new LinkedHashMap<Integer, List<TestCaseMap>>();
		}
		Map<Integer, List<TestCaseExecutions>> allLatestTestCaseExecutions = testCaseExecutionsService
				.getAllLatestTestCaseExecutions(releaseId, environmentId,
						testCaseIds);
		if (null == allLatestTestCaseExecutions) {
			log.info("No execution details are available for the given set of "
					+ "test cases for the execution report.");
			allLatestTestCaseExecutions = new LinkedHashMap<Integer, List<TestCaseExecutions>>();
		}
		System.out.println("#########################" + (t3 - t1));
		for (TestCase testCase : totalTestCaseList) {
			isMappingAvailable = false;
			int testCaseId = testCase.getTestCaseId();
			StringBuilder testStepIds = new StringBuilder();
			// Fetching all the test step id's corresponding to the given test
			// case. Fetching all the mapping of the automated test cases.
			// Previously individual test case mapping was getting fetched. To
			// make it quick all the mappings are stored in Map in single query
			// and fetching value from it
			List<TestCaseMap> testCaseMapList = testStepByTestCaseIds
					.get(testCase.getTestCaseId());
			/*
			 * List<TestCaseMap> testCaseMapList = testCaseMapService
			 * .getTestStepByTestCaseId(testCaseId);
			 */
			if (null != testCaseMapList && testCaseMapList.size() > 0) {
				isMappingAvailable = true;
			}
			firstTime = true;
			if (isMappingAvailable) {
				for (TestCaseMap testCaseMap : testCaseMapList) {
					if (testCaseMap.getTestCaseId() == testCaseId) {
						if (firstTime) {
							testStepIds.append(testCaseMap.getTestStepId());
							firstTime = false;
						} else {
							testStepIds.append(",")
									.append(testCaseMap.getTestStepId());
						}
					}
				}
			}

			if (!testCase.isAutomatable()) {
				testCaseType = manualTestCaseType + "-" + testCaseId;
			} else if (testCase.isAutomatable() && !isMappingAvailable) {
				testCaseType = automableOnlyTestCaseType + "-" + testCaseId;
			} else {
				testCaseType = automatedTestCaseType + "-" + testCaseId;
			}

			// Fetching test execution details of the single test case at a
			// time. For manual or automatable test cases (automation not
			// done), test step id's will be passed as null to removed it from
			// filter condition in select query.
			List<TestCaseExecutions> testCaseExecutions = new ArrayList<TestCaseExecutions>();
			if (isMappingAvailable) {
				testCaseExecutions = testCaseExecutionsService
						.getAllLatestTestCaseExecutions(releaseId,
								environmentId, testCaseId,
								testStepIds.toString());
			} else {
				testCaseExecutions = allLatestTestCaseExecutions
						.get(testCase.getTestCaseId());
			}

			if (null == testCaseExecutions || testCaseExecutions.isEmpty()) {
				log.debug("Execution details are not available for test case ["
						+ testCase.getTestSummary() + "]");
				testCaseStatus.get("executionRemaining").add(testCaseType);
				continue;
			}

			Map<String, List<String>> tempTestStepStat = new LinkedHashMap<String, List<String>>();
			boolean isCompletePass = true;
			boolean isCompleteFail = true;
			boolean isCompleteSkip = true;
			boolean isCompletePending = true;
			boolean isCompleteBlock = true;
			boolean isPass = false;
			boolean isFail = false;
			boolean isSkip = false;
			boolean isPending = false;
			boolean isBlock = false;

			String keyword = "";

			Map<Long, String> testStepsNameByIds = new LinkedHashMap<Long, String>();
			if (isMappingAvailable) {
				testStepsNameByIds = testStepServiceImpl
						.getTestStepsNameByIds(testStepIds.toString());
			}

			/*
			 * In the below loop, the test steps individual status and name of
			 * the test step will be recorded. This information is displayed
			 * when user select the execution status from the drop-down on the
			 * Execution reports page. After that the test case appears with
			 * "Fetch Test Steps" button. Once click on it, steps mapped to that
			 * test case with its latest execution status is displayed
			 */
			for (TestCaseExecutions testCaseExecution : testCaseExecutions) {
				String testResult = testCaseExecution.getTestResult()
						.toDbString().toLowerCase();

				if (testResult.equals("passed")) {
					isCompleteFail = false;
					isCompleteSkip = false;
					isCompletePending = false;
					isCompleteBlock = false;
					isPass = true;
					keyword = EXECUTION_REPORT_KEYWORD_STATUS_PASS;
				} else if (testResult.equals("failed")) {
					isCompletePass = false;
					isCompleteSkip = false;
					isCompletePending = false;
					isCompleteBlock = false;
					isFail = true;
					keyword = EXECUTION_REPORT_KEYWORD_STATUS_FAIL;
				} else if (testResult.equals("skipped")) {
					isCompletePass = false;
					isCompleteFail = false;
					isCompletePending = false;
					isCompleteBlock = false;
					isSkip = true;
					keyword = EXECUTION_REPORT_KEYWORD_STATUS_SKIP;
				} else if (testResult.equals("pending")) {
					isCompletePass = false;
					isCompleteFail = false;
					isCompleteSkip = false;
					isCompleteBlock = false;
					isPending = true;
					keyword = EXECUTION_REPORT_KEYWORD_STATUS_PENDING;
				} else if (testResult.equals("blocked")) {
					isCompletePass = false;
					isCompleteFail = false;
					isCompleteSkip = false;
					isCompletePending = false;
					isBlock = true;
					keyword = EXECUTION_REPORT_KEYWORD_STATUS_BLOCK;
				}
				List<String> list = tempTestStepStat.get(keyword);
				if (null == list) {
					list = new ArrayList<String>();
				}
				if (isMappingAvailable && testStepsNameByIds != null) {
					list.add(testCaseExecution.getStepKeyword().concat(" ")
							.concat(testStepsNameByIds
									.get(testCaseExecution.getTestStepId())
									.trim()));
				} else {
					if (testCase.isAutomatable()) {
						list.add("Automatable but not automated test case");
					} else {
						list.add("Manual Step");
					}
				}

				tempTestStepStat.put(keyword, list);

				// Adding duration of a test case by adding duration of all
				// individual test steps in it. For skip, blocked and pending
				// test cases, 0 seconds will be added.
				testCaseDuration.put(keyword, (testCaseDuration.get(keyword)
						+ testCaseExecution.getActualLOE()));
			}

			testStepsStatus.put(testCaseId, tempTestStepStat);
			if (isCompletePass) {
				keyword = EXECUTION_REPORT_KEYWORD_STATUS_PASS;
			} else if (isCompleteFail || isFail) {
				keyword = EXECUTION_REPORT_KEYWORD_STATUS_FAIL;
			} else if (isCompletePending) {
				keyword = EXECUTION_REPORT_KEYWORD_STATUS_PENDING;
			} else if (isCompleteBlock) {
				keyword = EXECUTION_REPORT_KEYWORD_STATUS_BLOCK;
			} else if (!isFail && isBlock && (isPass || isSkip || isPending)) {
				keyword = EXECUTION_REPORT_KEYWORD_STATUS_BLOCK;
			} else if (isCompleteSkip) {
				keyword = EXECUTION_REPORT_KEYWORD_STATUS_SKIP;
			} else if (!isFail && !isBlock && isPass && (isPending || isSkip)) {
				keyword = EXECUTION_REPORT_KEYWORD_STATUS_SKIP;
			} else {
				keyword = EXECUTION_REPORT_KEYWORD_STATUS_SKIP;
			}

			// Adding test case status based on all of its mapped test step
			// status
			List<String> list = testCaseStatus.get(keyword);
			if (null == list) {
				list = new ArrayList<String>();
			}
			list.add(testCaseType);
			testCaseStatus.put(keyword, list);
		}

		executionReportBean.setTotalTestCaseCount(totalTestCaseList.size());
		executionReportBean
				.setTotalExecutedTestCaseCount(testStepsStatus.keySet().size());

		executionReportBean.setTestCaseExecutionStatusInfo(testCaseStatus);
		executionReportBean.setTestStepsStatus(testStepsStatus);
		executionReportBean.setDurationInfo(testCaseDuration);

		log.info("totalTestCaseCount ="
				+ executionReportBean.getTotalTestCaseCount());
		log.info("totalExecutedTestCaseCount ="
				+ executionReportBean.getTotalExecutedTestCaseCount());
		long t2 = System.currentTimeMillis();
		System.out.println("time takennnnnnnnnnnn : " + (t2 - t1));
		return executionReportBean;
	}

	@Override
	public String downloadClientReport(int clientProjectId, int releaseId,
			int environmentId, String format, String path, String applicable)
			throws APIExceptions {
		// Fetching top level module with parent id as 0 for report file
		// generation
		List<Modules> topParentModules = modulesService
				.getTopParentModulesByProjectIdForReport(clientProjectId);

		final String[] header = new String[] {
				PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_SECTION,
				PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_TOTAL,
				PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_EXECUTED,
				PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_PASSED,
				PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_FAILED,
				PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_PENDING,
				PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_BLOCKED,
				PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_BLOCKER_BUGS,
				PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_STATUS,
				PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_BACKLOG,
				PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_LINKED_BUGS };
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();

		for (Modules module : topParentModules) {
			Map<String, String> value = new LinkedHashMap<String, String>();
			ExecutionReportBean executionReport = getExecutionReport(
					clientProjectId,
					Integer.parseInt("" + module.getModuleId()), releaseId,
					environmentId, applicable);

			value.put(
					PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_SECTION,
					module.getName());
			value.put(PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_TOTAL,
					"" + executionReport.getTotalTestCaseCount());
			value.put(
					PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_EXECUTED,
					"" + executionReport.getTotalExecutedTestCaseCount());

			value.put(
					PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_PASSED,
					"" + executionReport.getTestCaseExecutionStatusInfo()
							.get(EXECUTION_REPORT_KEYWORD_STATUS_PASS).size());
			value.put(
					PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_FAILED,
					"" + executionReport.getTestCaseExecutionStatusInfo()
							.get(EXECUTION_REPORT_KEYWORD_STATUS_FAIL).size());
			value.put(
					PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_PENDING,
					"" + executionReport.getTestCaseExecutionStatusInfo()
							.get(EXECUTION_REPORT_KEYWORD_STATUS_PENDING)
							.size());
			value.put(
					PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_BLOCKED,
					"" + executionReport.getTestCaseExecutionStatusInfo()
							.get(EXECUTION_REPORT_KEYWORD_STATUS_BLOCK).size());

			// Currently value in reports are either 0 or empty. Will add
			// logical later if available.
			value.put(
					PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_BLOCKER_BUGS,
					"0");

			if (executionReport.getTotalTestCaseCount() == executionReport
					.getTotalExecutedTestCaseCount()) {
				value.put(
						PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_STATUS,
						PintailerConstants.DOWNLOAD_EXECUTION_REPORT_STATUS_COMPLETED);
			} else if (executionReport.getTestCaseExecutionStatusInfo()
					.get(EXECUTION_REPORT_KEYWORD_STATUS_PENDING).size() > 0) {
				value.put(
						PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_STATUS,
						PintailerConstants.DOWNLOAD_EXECUTION_REPORT_STATUS_PENDING);
			} else if ((executionReport
					.getTotalTestCaseCount() > executionReport
							.getTotalExecutedTestCaseCount())
					|| executionReport.getTotalExecutedTestCaseCount() == 0) {
				value.put(
						PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_STATUS,
						PintailerConstants.DOWNLOAD_EXECUTION_REPORT_STATUS_IN_PROCESS);
			} else {
				value.put(
						PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_STATUS,
						PintailerConstants.DOWNLOAD_EXECUTION_REPORT_STATUS_NOT_DETERMINED);
			}

			// Getting linked bugs info
			// 1. Getting all module ids corresponding to the parent id
			// List<Long> allChildModules = modulesService
			// .getAllChildModules(module.getModuleId());
			int ticketCount = 0;

			boolean firstTime = true;
			String testCaseIds = null;
			// for (int i = 0; i < allChildModules.size(); i++) {
			// List<TestCase> testCasesByModuleId = testCaseManager
			// .getTestCaseByModuleId(allChildModules.get(i));
			// for (TestCase testCase : testCasesByModuleId) {
			// if (firstTime) {
			// testCaseIds = "" + testCase.getTestCaseId();
			// firstTime = false;
			// } else {
			// testCaseIds += "," + testCase.getTestCaseId();
			// }
			// ticketCount++;
			// }
			// }

			List<TestCaseClientBean> testCasesList = testCaseService
					.getTestCaseByModuleId(clientProjectId, 0,
							module.getModuleId(), "true", "false");
			for (TestCaseClientBean testCase : testCasesList) {
				if (firstTime) {
					testCaseIds = "" + testCase.getTestCaseSequenceId();
					firstTime = false;
				} else {
					testCaseIds += "," + testCase.getTestCaseSequenceId();
				}
				ticketCount++;
			}

			String tickets = "";
			List<LinkedTicket> allLinkedTickets = linkedTicketManager
					.getAllLinkedTickets(releaseId, environmentId, testCaseIds);
			firstTime = true;
			for (int i = 0; i < allLinkedTickets.size(); i++) {
				if (firstTime) {
					tickets = allLinkedTickets.get(i).getTicketNumber().trim();
					firstTime = false;
				} else {
					if (!tickets.contains(
							allLinkedTickets.get(i).getTicketNumber().trim())) {
						tickets += allLinkedTickets.get(i).getTicketNumber()
								.trim();
					}
				}
			}
			value.put(
					PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_BACKLOG,
					"" + ticketCount);
			value.put(
					PintailerConstants.DOWNLOAD_EXECUTION_REPORT_COLUMN_LINKED_BUGS,
					tickets);

			data.add(value);
		}

		ReadAndWriteReport writeExcelReport = new ReadAndWriteReport();
		try {
			if (format.equalsIgnoreCase("xls")) {
				return writeExcelReport.writeXLSFile(header, data, path,
						"Report_", true);
			} else if (format.equalsIgnoreCase("xlsx")) {
				return writeExcelReport.writeXLSXFile(header, data, path,
						"Report_", true);
			} else {
				log.error("Unsupported file format [" + format
						+ "] is given for report");
			}
		} catch (IOException e) {
			log.error(
					"Some error occured while creating the excel report for download : "
							+ e.getMessage());
		}
		return null;
	}

	@Override
	public String downloadManualExecutionTemplate(String format,
			String fileName, String filePath, String testCaseIds)
			throws APIExceptions {
		if (null == fileName || fileName.equals("")
				|| fileName.equals("null")) {
			fileName = "Test_Cases";
		}

		if (null == filePath || filePath.equals("")
				|| filePath.equals("null")) {
			throw new APIExceptions("FilePath is not valid : " + filePath);
		}
		// Fetching top level module with parent id as 0 for report file
		// generation
		final String[] header = new String[] {
				PintailerConstants.IMPORT_CSV_COLUMN_SERIAL_NO,
				PintailerConstants.IMPORT_CSV_COLUMN_TC_ID_REF,
				PintailerConstants.IMPORT_CSV_COLUMN_MODULE_NAME,
				PintailerConstants.IMPORT_CSV_COLUMN_FUNCTIONALITY,
				PintailerConstants.IMPORT_CSV_COLUMN_SUB_FUNCTIONALITY,
				PintailerConstants.IMPORT_CSV_COLUMN_TEST_SUMMARY,
				PintailerConstants.IMPORT_CSV_COLUMN_PRE_CONDITION,
				PintailerConstants.IMPORT_CSV_COLUMN_EXECUTION_STEPS,
				PintailerConstants.IMPORT_CSV_COLUMN_EXPECTED_RESULT,
				PintailerConstants.IMPORT_EXECUTION_CSV_COLUMN_ADD_EXECUTION_DETAILS,
				PintailerConstants.IMPORT_EXECUTION_CSV_COLUMN_EXECUTION_DATE,
				PintailerConstants.IMPORT_CSV_COLUMN_TEST_RESULTS,
				PintailerConstants.IMPORT_CSV_COLUMN_REMARKS,
				PintailerConstants.IMPORT_CSV_COLUMN_LINKED_DEFECTS,
				PintailerConstants.IMPORT_EXECUTION_CSV_COLUMN_DURATION_IN_SECONDS,
				PintailerConstants.IMPORT_EXECUTION_CSV_COLUMN_TEST_CASE_ID,
				PintailerConstants.IMPORT_EXECUTION_CSV_COLUMN_TEST_STEP_DEFINITION };

		if (!ValueValidations.isValueValid(testCaseIds)) {
			testCaseIds = "";
//			throw new APIExceptions(
//					"Test cases are not available for selected organization. "
//							+ "Report cannot download.");
		}

		String[] testCaseIdsArr = testCaseIds.split(",");
		List<Integer> ids = new ArrayList<Integer>();
		for (int i = 0; i < testCaseIdsArr.length; i++) {
			if (ValueValidations.isValueValid(testCaseIdsArr[i].trim())) {
				ids.add(Integer.parseInt(testCaseIdsArr[i].trim()));
			}
		}

		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		int sequenceNo = 1;
		List<TestCase> allTestCases = testCaseService.getAllTestCases(0, 0,
				null, null, null, StringUtils.join(ids, ","), null, null, null,
				0, 0, null, null, null);
		String moduleIds = String.join(",",
				allTestCases.stream().map(e -> "" + e.getModuleId()).distinct()
						.collect(Collectors.toList()));
		Map<String, String[]> moduleHierarchy = modulesService
				.getModuleHierarchy(moduleIds);
		for (TestCase testCase : allTestCases) {
//			TestCase testCase = testCaseService.getTestCaseById(testCaseId,
//					"false");
			Map<String, String> value = new LinkedHashMap<String, String>();

			value.put(PintailerConstants.IMPORT_CSV_COLUMN_SERIAL_NO,
					"" + sequenceNo);
			value.put(PintailerConstants.IMPORT_CSV_COLUMN_TC_ID_REF,
					testCase.getTestCaseNo());

//			String hierarchy = modulesService
//					.getModuleHierarchy(testCase.getModuleId()).get(0).trim();
//			String[] hierarchyArr = hierarchy.substring(1, hierarchy.length())
//					.split(",");
//			ArrayUtils.reverse(hierarchyArr);
			String[] hierarchyArr = moduleHierarchy
					.get("" + testCase.getModuleId());
			value.put(PintailerConstants.IMPORT_CSV_COLUMN_MODULE_NAME,
					hierarchyArr.length >= 1 ? hierarchyArr[0] : "");
			value.put(PintailerConstants.IMPORT_CSV_COLUMN_FUNCTIONALITY,
					hierarchyArr.length >= 2 ? hierarchyArr[1] : "");
			value.put(PintailerConstants.IMPORT_CSV_COLUMN_SUB_FUNCTIONALITY,
					hierarchyArr.length >= 3 ? hierarchyArr[2] : "");

			value.put(PintailerConstants.IMPORT_CSV_COLUMN_TEST_SUMMARY,
					testCase.getTestSummary());
			value.put(PintailerConstants.IMPORT_CSV_COLUMN_PRE_CONDITION,
					testCase.getPreCondition());
			value.put(PintailerConstants.IMPORT_CSV_COLUMN_EXECUTION_STEPS,
					testCase.getExecutionSteps());
			value.put(PintailerConstants.IMPORT_CSV_COLUMN_EXPECTED_RESULT,
					testCase.getExpectedResult());

			value.put(
					PintailerConstants.IMPORT_EXECUTION_CSV_COLUMN_ADD_EXECUTION_DETAILS,
					"==>");

			value.put(
					PintailerConstants.IMPORT_EXECUTION_CSV_COLUMN_EXECUTION_DATE,
					PintailerConstants.IMPORT_EXECUTION_CSV_COLUMN_EXECUTION_DATE_FORMAT);
			value.put(PintailerConstants.IMPORT_CSV_COLUMN_TEST_RESULTS,
					"passed/failed/skipped");
			value.put(PintailerConstants.IMPORT_CSV_COLUMN_REMARKS, "");
			value.put(PintailerConstants.IMPORT_CSV_COLUMN_LINKED_DEFECTS, "");
			value.put(
					PintailerConstants.IMPORT_EXECUTION_CSV_COLUMN_DURATION_IN_SECONDS,
					"");
			value.put(
					PintailerConstants.IMPORT_EXECUTION_CSV_COLUMN_TEST_CASE_ID,
					"" + testCase.getTestCaseId());
			value.put(
					PintailerConstants.IMPORT_EXECUTION_CSV_COLUMN_TEST_STEP_DEFINITION,
					"");

			data.add(value);
			sequenceNo++;
		}

		ReadAndWriteReport readAndWriteReport = new ReadAndWriteReport();
		try {
			if (format.equalsIgnoreCase("csv")) {
				if (readAndWriteReport.writeCSVTemplate(header, data, filePath,
						fileName + ".csv")) {
					return fileName + ".csv";
				}
			} else {
				log.error("Unsupported file format [" + format
						+ "] is given for report");
			}
		} catch (Exception e) {
			log.error("Some error occured while creating the csv template "
					+ "for manual execution test cases for download : "
					+ e.getMessage());
		}
		return null;
	}

	@Override
	public String downloadTestCases(String format, String fileName,
			String filePath, String testCaseIds) throws APIExceptions {

		// Fetching top level module with parent id as 0 for report file
		// generation
//		if (!ValueValidations.isValueValid(testCaseIds)) {
//			throw new APIExceptions(
//					"Test cases are not available for selected organization. "
//							+ "Report cannot download.");
//		}

		if (null == fileName || fileName.equals("")
				|| fileName.equals("null")) {
			fileName = "All";
		}

//		String[] testCaseIdsArr = testCaseIds.split(",");
//		List<Integer> ids = new ArrayList<Integer>();
//		try {
//			for (int i = 0; i < testCaseIdsArr.length; i++) {
//				ids.add(Integer.parseInt(testCaseIdsArr[i].trim()));
//			}
//		} catch (NumberFormatException e) {
//			String message = "Test cases ids are not valid ids.Please send the correct numeric ids and try again.";
//			log.error(message);
//			throw new APIExceptions(message);
//		}

		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		int sequenceNo = 1;
		List<TestCase> allTestCases = testCaseService.getAllTestCases(0, 0,
				null, null, null, testCaseIds, null, null, null, 0, 0, null,
				null, null);
		String moduleIds = String.join(",",
				allTestCases.stream().map(e -> "" + e.getModuleId()).distinct()
						.collect(Collectors.toList()));
		Map<String, String[]> moduleHierarchy = modulesService
				.getModuleHierarchy(moduleIds);
		for (TestCase testCase : allTestCases) {
//			TestCase testCase = testCaseService.getTestCaseById(testCaseId,
//					"false");
			data.add(getColumnsToWrite(sequenceNo, testCase, moduleHierarchy));
			sequenceNo++;
		}

		return writeFile(format, filePath, fileName, data);
	}

	@Override
	public String getTestCasesOfSpecificActivityStatus(int clientProjectId,
			int moduleId, String isApplicable, String isDeleted,
			String fileFormat, String filePath, String fileName)
			throws APIExceptions {
		List<TestCase> testCaseList;
		if (moduleId > 0) {
			testCaseList = testCaseService.getTestCaseBeanListByModuleId(
					moduleId, String.valueOf(isApplicable),
					String.valueOf(isDeleted));
		} else {
			testCaseList = testCaseService.getAllTestCases(clientProjectId, 0,
					null, null, isApplicable, null, null, null, null, 0, 0,
					null, null, isDeleted);
		}
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		int sequenceNo = 1;
		String moduleIds = StringUtils
				.join(testCaseList.stream().map(e -> e.getModuleId()).distinct()
						.collect(Collectors.toList()), ",");
		Map<String, String[]> moduleHierarchy = modulesService
				.getModuleHierarchy(moduleIds);
		for (TestCase testCase : testCaseList) {
			data.add(getColumnsToWrite(sequenceNo, testCase, moduleHierarchy));
			sequenceNo++;
		}

		return writeFile(fileFormat, filePath, fileName, data);
	}

	private Map<String, String> getColumnsToWrite(int sequenceNo,
			TestCase testCase, Map<String, String[]> moduleHierarchy)
			throws APIExceptions {
		Map<String, String> value = new LinkedHashMap<String, String>();

		value.put(PintailerConstants.IMPORT_CSV_COLUMN_SERIAL_NO,
				"" + sequenceNo);
		value.put(PintailerConstants.IMPORT_CSV_COLUMN_TC_ID_REF,
				testCase.getTestCaseNo());

//		String hierarchy = modulesService
//				.getModuleHierarchy(testCase.getModuleId()).get(0).trim();
//		String[] hierarchyArr = hierarchy.substring(1, hierarchy.length())
//				.split(",");
//		ArrayUtils.reverse(hierarchyArr);

		String[] hierarchyArr = moduleHierarchy
				.get("" + testCase.getModuleId());

		value.put(PintailerConstants.IMPORT_CSV_COLUMN_MODULE_NAME,
				hierarchyArr.length >= 1 ? hierarchyArr[0] : "");
		value.put(PintailerConstants.IMPORT_CSV_COLUMN_FUNCTIONALITY,
				hierarchyArr.length >= 2 ? hierarchyArr[1] : "");
		value.put(PintailerConstants.IMPORT_CSV_COLUMN_SUB_FUNCTIONALITY,
				hierarchyArr.length >= 3 ? hierarchyArr[2] : "");

		value.put(PintailerConstants.IMPORT_CSV_COLUMN_TEST_SUMMARY,
				testCase.getTestSummary());
		value.put(PintailerConstants.IMPORT_CSV_COLUMN_PRE_CONDITION,
				testCase.getPreCondition());
		value.put(PintailerConstants.IMPORT_CSV_COLUMN_EXECUTION_STEPS,
				testCase.getExecutionSteps());
		value.put(PintailerConstants.IMPORT_CSV_COLUMN_EXPECTED_RESULT,
				testCase.getExpectedResult());

		value.put(PintailerConstants.IMPORT_CSV_COLUMN_ACTUAL_RESULT, "");
		value.put(PintailerConstants.IMPORT_CSV_COLUMN_TESTER,
				testCase.getCreatedBy());
		value.put(PintailerConstants.IMPORT_CSV_COLUMN_EXECUTION_DATE, "");
		value.put(PintailerConstants.IMPORT_CSV_COLUMN_TEST_RESULTS, "");
		value.put(PintailerConstants.IMPORT_CSV_COLUMN_TEST_DATA,
				testCase.getTestData());
		value.put(PintailerConstants.IMPORT_CSV_COLUMN_LINKED_DEFECTS, "");
		value.put(PintailerConstants.IMPORT_CSV_COLUMN_ENVIRONMENT, "");
		value.put(PintailerConstants.IMPORT_CSV_COLUMN_CRITICALITY,
				testCase.getTags().toString());

		value.put(PintailerConstants.IMPORT_CSV_COLUMN_IS_AUTOMATABLE,
				"" + testCase.isAutomatable());
		value.put(PintailerConstants.IMPORT_CSV_COLUMN_REMARKS,
				testCase.getRemarks());
		value.put(PintailerConstants.IMPORT_CSV_COLUMN_FILE_NAME, "");

		value.put(PintailerConstants.IMPORT_CSV_COLUMN_TEST_CASE_NO, "");

		value.put(PintailerConstants.IMPORT_CSV_COLUMN_APPLICABLE,
				"" + testCase.isApplicable());

		value.put(PintailerConstants.IMPORT_CSV_COLUMN_CREATION_DATE,
				"" + testCase.getCreatedDate());

		return value;
	}

	private String writeFile(String format, String filePath, String fileName,
			List<Map<String, String>> data) {
		ReadAndWriteReport writeExcelReport = new ReadAndWriteReport();
		try {
			if (format.equalsIgnoreCase("xls")) {
				return writeExcelReport.writeXLSFile(
						PintailerConstants.IMPORT_TC_CSV_HEADERS, data,
						filePath, "TestCases_" + fileName + "_", false);
			} else if (format.equalsIgnoreCase("xlsx")) {
				return writeExcelReport.writeXLSXFile(
						PintailerConstants.IMPORT_TC_CSV_HEADERS, data,
						filePath, "TestCases_" + fileName + "_", false);
			} else {
				log.error("Unsupported file format [" + format
						+ "] is given for report");
			}
		} catch (IOException e) {
			log.error(
					"Some error occured while creating the test case list for download : "
							+ e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ProgressReportBean getAutomationProgress(int clientProjectId,
			String applicable, String startDate, String endDate)
			throws APIExceptions {
		ProgressReportBean progressReportBean = new ProgressReportBean();
		progressReportBean.setClientProjectId(clientProjectId);
		// getting added test cases list
		List<TestCase> testCaseList = testCaseService.getAllTestCases(
				clientProjectId, 0, null, null, applicable, null, null, null,
				null, 0, 0, startDate, endDate, null);
		List<Integer> addedTestCaseIds = testCaseList.stream()
				.map(e -> e.getTestCaseId()).collect(Collectors.toList());

		// Fetching deleted test cases ids list
		List<Integer> deletedTestCaseIds = testCaseService
				.getDeletedTestCaseIdsForGivenTime(clientProjectId, startDate,
						endDate);

		// Fetching test cases ids list which are added then deleted in the
		// given tie frame
		List<Integer> newlyAddedAndDeletedTestCaseIds = addedTestCaseIds
				.stream().filter(deletedTestCaseIds::contains)
				.collect(Collectors.toList());
		List<TestCase> newlyAddedAndDeletedTestCases = new ArrayList<TestCase>();
		if (!(null == newlyAddedAndDeletedTestCaseIds
				|| newlyAddedAndDeletedTestCaseIds.isEmpty())) {
			newlyAddedAndDeletedTestCases = testCaseService.getAllTestCases(
					clientProjectId, 0, null, null, "all",
					StringUtils.join(newlyAddedAndDeletedTestCaseIds, ","),
					null, null, null, 0, 0, null, null, null);
		}
		Map<Integer, String> newlyAddedAndDeletedTestCaseIdsMap = newlyAddedAndDeletedTestCases
				.stream().collect(Collectors.toMap(TestCase::getTestCaseId,
						TestCase::getModifiedBy));
		progressReportBean.setNewlyAddedAndDeletedTestCaseIdsMap(
				newlyAddedAndDeletedTestCaseIdsMap);

		// Removing added and deleted test cases ids in the given time frame
		// from overall deleted and added test cases list
		addedTestCaseIds.removeAll(newlyAddedAndDeletedTestCaseIds);
		deletedTestCaseIds.removeAll(newlyAddedAndDeletedTestCaseIds);

		List<TestCase> addedTestCases = new ArrayList<TestCase>();
		if (!(null == addedTestCaseIds || addedTestCaseIds.isEmpty())) {
			addedTestCases = testCaseService.getAllTestCases(clientProjectId, 0,
					null, null, "all", StringUtils.join(addedTestCaseIds, ","),
					null, null, null, 0, 0, null, null, null);
		}
		Map<Integer, String> addedTestCaseIdsMap = addedTestCases.stream()
				.collect(Collectors.toMap(TestCase::getTestCaseId,
						TestCase::getModifiedBy));
		progressReportBean.setAddedTestCaseIdsMap(addedTestCaseIdsMap);

		List<TestCase> deletedTestCases = new ArrayList<TestCase>();
		if (!(null == deletedTestCaseIds || deletedTestCaseIds.isEmpty())) {
			deletedTestCases = testCaseService.getAllTestCases(clientProjectId,
					0, null, null, "all",
					StringUtils.join(deletedTestCaseIds, ","), null, null, null,
					0, 0, null, null, null);
		}
		Map<Integer, String> deletedTestCaseIdsMap = deletedTestCases.stream()
				.collect(Collectors.toMap(TestCase::getTestCaseId,
						TestCase::getModifiedBy));
		progressReportBean.setDeletedTestCaseIdsMap(deletedTestCaseIdsMap);

		progressReportBean.setAddedTestCaseIds(addedTestCaseIds);
		progressReportBean.setDeletedTestCaseIds(deletedTestCaseIds);
		progressReportBean.setNewlyAddedAndDeletedTestCaseIds(
				newlyAddedAndDeletedTestCaseIds);

		// Fetching mapping version in the given time frame
		List<TestCaseMapVersion> testCaseMapMaxVersionForSpecificPeriod = testCaseMapService
				.getTestCaseMapMaxVersionForSpecificPeriod(clientProjectId,
						startDate, endDate);
		List<Integer> overallMapped = testCaseMapMaxVersionForSpecificPeriod
				.stream().map(e -> e.getTestCaseId())
				.collect(Collectors.toList());
		progressReportBean
				.setOverallMappedTestCasesInSpecificPeriod(overallMapped);

		// Fetching the list of test cases ids for which the mapping is deleted
		// in the given time frame
		List<Integer> deletedMappedTestCaseIds = testCaseMapMaxVersionForSpecificPeriod
				.stream()
				.filter(e -> e.getSelectedTestStepsIdAndVersion()
						.equals(PintailerConstants.TEST_CASE_STEP_NO_MAPPING))
				.map(e -> e.getTestCaseId()).collect(Collectors.toList());

		List<TestCase> deletedMappedTestCases = new ArrayList<TestCase>();
		if (!(null == deletedMappedTestCaseIds
				|| deletedMappedTestCaseIds.isEmpty())) {
			deletedMappedTestCases = testCaseService.getAllTestCases(
					clientProjectId, 0, null, null, "all",
					StringUtils.join(deletedMappedTestCaseIds, ","), null, null,
					null, 0, 0, null, null, null);
		}
		Map<Integer, String> deletedMappedTestCaseIdsMap = deletedMappedTestCases
				.stream().collect(Collectors.toMap(TestCase::getTestCaseId,
						TestCase::getModifiedBy));
		progressReportBean
				.setDeletedTestCaseMappingIdsMap(deletedMappedTestCaseIdsMap);

		progressReportBean
				.setDeletedTestCaseMappingIds(deletedMappedTestCaseIds);

		// Fetching the list of test cases ids for which the mapping is added in
		// the given time frame
		List<Integer> addedMappedTestCaseIds = testCaseMapMaxVersionForSpecificPeriod
				.stream()
				.filter(e -> !e.getSelectedTestStepsIdAndVersion()
						.equals(PintailerConstants.TEST_CASE_STEP_NO_MAPPING))
				.map(e -> e.getTestCaseId()).collect(Collectors.toList());

		List<TestCase> addedMappedTestCases = new ArrayList<TestCase>();
		if (!(null == addedMappedTestCaseIds
				|| addedMappedTestCaseIds.isEmpty())) {
			addedMappedTestCases = testCaseService.getAllTestCases(
					clientProjectId, 0, null, null, "all",
					StringUtils.join(addedMappedTestCaseIds, ","), null, null,
					null, 0, 0, null, null, null);
		}
		Map<Integer, String> addedMappedTestCaseIdsMap = addedMappedTestCases
				.stream().collect(Collectors.toMap(TestCase::getTestCaseId,
						TestCase::getModifiedBy));
		progressReportBean
				.setAddedTestCaseMappingIdsMap(addedMappedTestCaseIdsMap);

		progressReportBean.setAddedTestCaseMappingIds(addedMappedTestCaseIds);

		return progressReportBean;
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
