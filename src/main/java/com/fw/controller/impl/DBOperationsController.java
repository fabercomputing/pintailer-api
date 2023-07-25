package com.fw.controller.impl;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fw.dao.IClientProjectsManager;
import com.fw.dao.IReleaseManager;
import com.fw.dao.IReleaseMapManager;
import com.fw.dao.IReleaseTestCaseMappingManager;
import com.fw.dao.ITestCaseManager;
import com.fw.dao.ITestCaseMapManager;
import com.fw.dao.ITestScenarioStepManager;
import com.fw.dao.ITestScenariosManager;
import com.fw.dao.ITestStepManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.ClientProjects;
import com.fw.domain.Release;
import com.fw.domain.ReleaseMapVersion;
import com.fw.domain.ReleaseTestCaseMapping;
import com.fw.domain.TestCase;
import com.fw.domain.TestCaseMap;
import com.fw.domain.TestCaseMapVersion;
import com.fw.domain.TestScenarioStep;
import com.fw.domain.TestScenarioStepVersion;
import com.fw.domain.TestScenarios;
import com.fw.domain.TestStep;
import com.fw.exceptions.APIExceptions;
import com.fw.services.ITestCaseService;
import com.fw.services.ITestStepService;
import com.fw.utils.GenerateUniqueHash;
import com.fw.utils.TestStepsInfoUtil;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class DBOperationsController {

	private Logger log = Logger.getLogger(DBOperationsController.class);

	@Autowired
	ITestCaseService testCaseService;

	@Autowired
	ITestStepService testStepService;

	@Autowired
	ITestScenarioStepManager testScenarioStepManager;

	@Autowired
	ITestCaseManager testCaseManager;

	@Autowired
	ITestCaseMapManager testCaseMapManager;

	@Autowired
	ITestScenariosManager testScenariosManager;

	@Autowired
	ITestStepManager testStepManager;

	@Autowired
	IClientProjectsManager clientProjectsManager;

	@Autowired
	IReleaseManager releaseManager;

	@Autowired
	IReleaseMapManager releaseMapManager;

	@Autowired
	IReleaseTestCaseMappingManager releaseTestCaseMappingManagerImpl;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@RequestMapping(value = "/private/dbOperations/detachStepKeyword", method = {
			POST })
	public void detachStepKeyword() throws APIExceptions {
		List<TestStep> testStepList = testStepService.getTestStep(0);
		for (TestStep testStep : testStepList) {
			if (TestStepsInfoUtil.isStep(testStep.getName())) {
				String name = testStep.getName();
				String keyword = name.substring(0, name.indexOf(" ")).trim();
				String desc = name.substring(name.indexOf(" ")).trim();
				testStep.setName(desc);
				// String hashCode = TestStepsInfoUtil.hash(desc);
				// if (!hashCode.equals(testStep.getHashCode())) {
				// throw new APIExceptions("Step original hash ["
				// + testStep.getHashCode()
				// + "] does not match with latest hash [" + hashCode
				// + "]");
				// }
				testStepService.updateTestStepById(testStep);
				log.info("Step is updated");

				testScenarioStepManager.updateTestScenarioKeywordByStepStepId(
						testStep.getTestStepId(), keyword);
			}
		}
	}

	@RequestMapping(value = "/private/dbOperations/updateTestCaseHashCode", method = {
			POST })
	public void updateTestCaseHashCode() throws APIExceptions {
		List<TestCase> testCaseList = testCaseManager.getAllTestCases();
		int result = 0;
		for (TestCase testCase : testCaseList) {
			String oldHash = testCase.getHashCode();
			String newHash = GenerateUniqueHash.getTestCaseHash(
					testCase.getModuleId(), testCase.getTestCaseNo(),
					testCase.getTestSummary(), testCase.getPreCondition(),
					testCase.getExecutionSteps(), testCase.getExpectedResult());
			testCase.setHashCode(newHash);

			result += testCaseManager.updateTestCaseById(testCase);
			log.info("hashcode updated from old hash [" + oldHash
					+ "] to new hash [" + newHash + "] for test case id ["
					+ testCase.getTestCaseId() + "]");
		}
		if (result != testCaseList.size()) {
			throw new APIExceptions("Not all test cases are updated");
		}
	}

	@RequestMapping(value = "/private/dbOperations/updateTestScenarioHashCode", method = {
			POST })
	public void updateTestScenarioHashCode() throws APIExceptions {
		List<TestScenarios> testScenariosList = testScenariosManager
				.getAllTestScenarios();
		int result = 0;
		for (TestScenarios testScenarios : testScenariosList) {
			String oldHash = testScenarios.getHashCode();
			String newHash = GenerateUniqueHash.getFeatureScenarioHash(
					testScenarios.getClientProjectId(),
					testScenarios.getFeatureFileName(),
					testScenarios.getName());
			testScenarios.setHashCode(newHash);

			result += testScenariosManager
					.updateTestScenariosById(testScenarios);
			log.info("hashcode updated from old hash [" + oldHash
					+ "] to new hash [" + newHash + "] for test scenario id ["
					+ testScenarios.getTestScenarioId() + "]");
		}
		if (result != testScenariosList.size()) {
			throw new APIExceptions("Not all test cases are updated");
		}
	}

	@RequestMapping(value = "/private/dbOperations/updateTestStepHashCodeAndProjectId", method = {
			POST })
	public void updateTestStepHashCodeAndProjectId() throws APIExceptions {
		List<TestStep> testStepList = testStepService.getTestStep(0);
		int result = 0;
		for (TestStep testStep : testStepList) {
			String oldHash = testStep.getHashCode();
			if (testStep.getTestStepId() == 0) {
				continue;
			}
			int client_project_id = testStepManager
					.getTestStepProjectId(testStep.getTestStepId());
			if (client_project_id == 0) {
				log.info("Given test step with id [" + testStep.getTestStepId()
						+ "] is not associated to any feature.");
				continue;
			}
			String newHash = GenerateUniqueHash
					.getTestStepHash(client_project_id, testStep.getName());
			testStep.setHashCode(newHash);
			testStep.setClientProjectId(client_project_id);

			result += testStepService.updateTestStepById(testStep);
			log.info("hashcode updated from old hash [" + oldHash
					+ "] to new hash [" + newHash
					+ "] and client project id is [" + client_project_id
					+ "] for test step id [" + testStep.getTestStepId() + "]");
		}
		if (result != testStepList.size()) {
			throw new APIExceptions("Not all test cases are updated");
		}
	}

	@RequestMapping(value = "/private/dbOperations/deleteOrgData", method = {
			POST })
	public String deleteOrgData(@RequestParam("defaultDB") String defaultDB,
			@RequestParam("OrgName") String orgName) throws APIExceptions {
		ClientDatabaseContextHolder.set(defaultDB.toUpperCase());
		String queryArr[] = new String[] {
				"DELETE FROM fw_test_mgmt.linked_ticket WHERE linked_ticket.testexecution_id IN (SELECT testcase_execution_id FROM fw_test_mgmt.testcase_execution WHERE environment_id IN (SELECT execution_env_id FROM fw_test_mgmt.execution_environment WHERE client_organization='"
						+ orgName + "'))",
				"DELETE FROM fw_test_mgmt.testcase_execution WHERE environment_id IN (SELECT execution_env_id FROM fw_test_mgmt.execution_environment WHERE client_organization='"
						+ orgName + "')",
				"DELETE FROM fw_test_mgmt.execution_environment WHERE client_organization='"
						+ orgName + "'",

				"DELETE FROM fw_test_mgmt.release_testcase WHERE release_testcase.testcase_id IN (SELECT testcase.testcase_id FROM fw_test_mgmt.testcase WHERE testcase.module_id IN (SELECT modules.module_id FROM fw_test_mgmt.modules WHERE client_project_id IN (SELECT client_project_id FROM fw_test_mgmt.client_projects WHERE client_organization='"
						+ orgName + "')))",
				"DELETE FROM fw_test_mgmt.testcase_map WHERE testcase_map.testcase_id IN (SELECT testcase.testcase_id FROM fw_test_mgmt.testcase WHERE testcase.module_id IN (SELECT modules.module_id FROM fw_test_mgmt.modules WHERE client_project_id IN (SELECT client_project_id FROM fw_test_mgmt.client_projects WHERE client_organization='"
						+ orgName + "')))",
				"DELETE FROM fw_test_mgmt.testcase WHERE testcase.module_id IN (SELECT modules.module_id FROM fw_test_mgmt.modules WHERE client_project_id IN (SELECT client_project_id FROM fw_test_mgmt.client_projects WHERE client_organization='"
						+ orgName + "'))",
				"DELETE FROM fw_test_mgmt.modules WHERE client_project_id IN (SELECT client_project_id FROM fw_test_mgmt.client_projects WHERE client_organization='"
						+ orgName + "')",

				"DELETE FROM fw_test_mgmt.\"release\" WHERE client_project_id IN (SELECT client_project_id FROM fw_test_mgmt.client_projects WHERE client_organization='"
						+ orgName + "')",
				"DELETE FROM fw_test_mgmt.test_scenario_step WHERE test_step_id IN (SELECT test_step_id FROM fw_test_mgmt.test_step WHERE client_project_id IN (SELECT client_project_id FROM fw_test_mgmt.client_projects WHERE client_organization='"
						+ orgName + "'))",

				"DELETE FROM fw_test_mgmt.test_scenarios WHERE client_project_id IN (SELECT client_project_id FROM fw_test_mgmt.client_projects WHERE client_organization='"
						+ orgName + "')",

				"DELETE FROM fw_test_mgmt.test_step WHERE client_project_id IN (SELECT client_project_id FROM fw_test_mgmt.client_projects WHERE client_organization='"
						+ orgName + "')",

				"DELETE FROM fw_test_mgmt.client_projects WHERE client_organization='"
						+ orgName + "'",
				"DELETE FROM fw_test_mgmt.testcase_map WHERE testcase_map.test_step_id IN (SELECT test_step_id FROM fw_test_mgmt.test_step WHERE test_step.client_project_id=0 AND test_step.test_step_id >0)",
				"DELETE FROM fw_test_mgmt.test_step WHERE test_step.client_project_id=0 AND test_step.test_step_id >0" };

		try {
			for (String sql : queryArr) {
				jdbcTemplate.update(sql);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			return e.getMessage();
		}
		return "success";
	}

	@RequestMapping(value = "/private/dbOperations/createTestScenarioStepMappingInitialVersion", method = {
			POST })
	public String createTestScenarioStepMappingInitialVersion(
			String organizationName) throws APIExceptions {
		try {
			List<ClientProjects> allClientProjectsForOrg = clientProjectsManager
					.getAllClientProjectsForOrg(organizationName,
							organizationName.toUpperCase());
			for (ClientProjects clientProjects : allClientProjectsForOrg) {
				List<TestScenarioStep> allTestScenarioStep = testScenarioStepManager
						.getAllTestScenarioStep(
								clientProjects.getClientProjectId());
				String prefix = "";
				int existingScenarioId = 0;
				boolean firstTime = true;

				TestScenarios testScenarios = new TestScenarios();
				TestScenarioStepVersion testScenarioStepVersion;
				StringBuilder testStepIdVersionSequenceKeyword = new StringBuilder();
				for (TestScenarioStep testScenarioStep : allTestScenarioStep) {
					if (existingScenarioId != 0
							&& existingScenarioId != testScenarioStep
									.getTestScenarioId()) {

						testScenarioStepVersion = new TestScenarioStepVersion();
						testScenarioStepVersion
								.setTestScenariosId(existingScenarioId);
						testScenarioStepVersion.setTestScenariosHashcode(
								testScenarios.getHashCode());
						testScenarioStepVersion
								.setTestScenariosVersionId(Integer.parseInt(
										testScenarios.getScenarioLatestVersion()
												.replace("V", "")));
						testScenarioStepVersion.setHardDeleted(false);
						testScenarioStepVersion
								.setTestStepIdVersionSequenceKeyword(
										testStepIdVersionSequenceKeyword
												.toString());

						testScenarioStepManager.persistTestScenarioStepVersion(
								testScenarioStepVersion);
						testStepIdVersionSequenceKeyword.delete(0,
								testStepIdVersionSequenceKeyword.length());
						prefix = "";
						existingScenarioId = testScenarioStep
								.getTestScenarioId();
						firstTime = true;
					} else {
						existingScenarioId = testScenarioStep
								.getTestScenarioId();
					}
					if (firstTime) {
						testScenarios = testScenariosManager
								.getTestScenariosById(
										testScenarioStep.getTestScenarioId(),
										null);
						firstTime = false;
					}
					TestStep testStepById = testStepManager.getTestStepById(
							testScenarioStep.getTestStepId(),
							clientProjects.getClientProjectId());
					testStepIdVersionSequenceKeyword.append(prefix)
							.append(testScenarioStep.getTestStepId())
							.append("-")
							.append(testStepById.getStepLatestVersion())
							.append("-")
							.append(testScenarioStep.getTestStepSequence())
							.append("-")
							.append(testScenarioStep.getStepKeyword());
					prefix = ",";
				}

				// for last scenario
				if (existingScenarioId != 0) {
					testScenarioStepVersion = new TestScenarioStepVersion();
					testScenarioStepVersion
							.setTestScenariosId(existingScenarioId);
					testScenarioStepVersion.setTestScenariosHashcode(
							testScenarios.getHashCode());
					testScenarioStepVersion.setTestScenariosVersionId(Integer
							.parseInt(testScenarios.getScenarioLatestVersion()
									.replace("V", "")));
					testScenarioStepVersion.setHardDeleted(false);
					testScenarioStepVersion.setTestStepIdVersionSequenceKeyword(
							testStepIdVersionSequenceKeyword.toString());

					testScenarioStepManager.persistTestScenarioStepVersion(
							testScenarioStepVersion);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Error : " + e.getMessage();
		}
		return "Success";
	}

	@RequestMapping(value = "/private/dbOperations/createTestCaseMappingInitialVersion", method = {
			POST })
	public String createTestCaseMappingInitialVersion(String organizationName)
			throws APIExceptions {
		try {
			List<ClientProjects> allClientProjectsForOrg = clientProjectsManager
					.getAllClientProjectsForOrg(organizationName,
							organizationName.toUpperCase());
			for (ClientProjects clientProjects : allClientProjectsForOrg) {
				List<TestCaseMap> testCaseMappings = testCaseMapManager
						.getTestCaseMappings(
								clientProjects.getClientProjectId());
//				List<TestScenarioStepVersion> allScenarioStepMappingVersion = testScenarioStepManager
//						.getAllScenarioStepMappingVersion(
//								clientProjects.getClientProjectId());

				TestCaseMapVersion testCaseMapVersion = null;
				String prefix = "";
				int existingTestCaseId = 0;
				boolean firstTime = true;
				StringBuilder selectedTestStepsIdAndVersion = new StringBuilder();
				TestCase testCase = new TestCase();
				TestScenarios testScenarios = new TestScenarios();
				TestScenarioStepVersion testScenarioStepVersion = new TestScenarioStepVersion();
				for (TestCaseMap testCaseMap : testCaseMappings) {

					if (existingTestCaseId != 0
							&& existingTestCaseId != testCaseMap
									.getTestCaseId()) {

						testCaseMapVersion = new TestCaseMapVersion();
						testCaseMapVersion
								.setTestCaseId(testCase.getTestCaseId());
						testCaseMapVersion
								.setTestCaseVersionId(Integer.parseInt(testCase
										.getLatestVersion().replace("V", "")));
						testCaseMapVersion.setHardDeleted(false);
						testCaseMapVersion.setSelectedTestStepsIdAndVersion(
								selectedTestStepsIdAndVersion.toString());
						testCaseMapVersion.setTestScenarioStepVersionId(
								testScenarioStepVersion
										.getTestScenarioStepVersionId());

						testCaseMapManager
								.persistTestCaseMapVersion(testCaseMapVersion);

						selectedTestStepsIdAndVersion.delete(0,
								selectedTestStepsIdAndVersion.length());
						prefix = "";
						existingTestCaseId = testCaseMap.getTestCaseId();
						firstTime = true;
					} else {
						existingTestCaseId = testCaseMap.getTestCaseId();
					}
					if (firstTime) {
						testCase = testCaseManager.getTestCaseById(
								testCaseMap.getTestCaseId(), null);
						firstTime = false;
						testScenarios = testScenariosManager
								.getTestScenariosById(
										testCaseMap.getTestScenarioId(), null);
						List<TestScenarioStepVersion> scenarioStepMappingVersion = testScenarioStepManager
								.getScenarioStepMappingVersion(
										testScenarios.getTestScenarioId(),
										Integer.parseInt(testScenarios
												.getScenarioLatestVersion()
												.replace("V", "")));
						if (null == scenarioStepMappingVersion
								|| scenarioStepMappingVersion.isEmpty()) {
							testScenarioStepVersion = new TestScenarioStepVersion();
							testScenarioStepVersion
									.setTestScenarioStepVersionId(1);

						} else {
							testScenarioStepVersion = scenarioStepMappingVersion
									.get(0);
						}
					}
					TestStep testStepById = testStepManager.getTestStepById(
							testCaseMap.getTestStepId(),
							clientProjects.getClientProjectId());
					selectedTestStepsIdAndVersion.append(prefix)
							.append(testCaseMap.getTestStepId()).append("::")
							.append(testStepById.getStepLatestVersion());
					prefix = ",";
				}

				// for last scenario
				if (existingTestCaseId != 0) {
					testCaseMapVersion = new TestCaseMapVersion();
					testCaseMapVersion.setTestCaseId(testCase.getTestCaseId());
					testCaseMapVersion.setTestCaseVersionId(Integer.parseInt(
							testCase.getLatestVersion().replace("V", "")));
					testCaseMapVersion.setHardDeleted(false);
					testCaseMapVersion.setSelectedTestStepsIdAndVersion(
							selectedTestStepsIdAndVersion.toString());
					testCaseMapVersion.setTestScenarioStepVersionId(
							testScenarioStepVersion
									.getTestScenarioStepVersionId());

					testCaseMapManager
							.persistTestCaseMapVersion(testCaseMapVersion);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Error : " + e.getMessage();
		}
		return "Success";
	}

	@RequestMapping(value = "/private/dbOperations/createReleaseTestCaseMappingInitialVersion", method = {
			POST })
	public String createReleaseTestCaseMappingInitialVersion(
			String organizationName) throws APIExceptions {
		try {
			List<ClientProjects> allClientProjectsForOrg = clientProjectsManager
					.getAllClientProjectsForOrg(organizationName,
							organizationName.toUpperCase());
			for (ClientProjects clientProjects : allClientProjectsForOrg) {
				int clientProjectId = clientProjects.getClientProjectId();
				List<Release> allReleases = releaseManager
						.getAllReleases(clientProjectId, "all");
				for (Release release : allReleases) {
					int releaseId = release.getReleaseId();
					List<ReleaseMapVersion> releasesMapVersion = releaseMapManager
							.getReleasesMapVersion(releaseId);
					Map<Integer, String> releasesMapSelectedVersion = new LinkedHashMap<Integer, String>();
					if (null == releasesMapVersion
							|| releasesMapVersion.isEmpty()) {
						releasesMapSelectedVersion = releaseMapManager
								.getReleasesMapSelectedVersion(releaseId,
										clientProjectId);
					} else {
						ReleaseMapVersion releaseMapVersion = releasesMapVersion
								.get(0);
						String testCaseIds = releaseMapVersion.getTestCaseIds();
						String[] split = testCaseIds.split(",");
						for (String s : split) {
							releasesMapSelectedVersion.put(
									Integer.parseInt(s.trim().split("::")[0]),
									s.trim().split("::")[1].replace("V", ""));
						}
					}

					Set<Integer> keySet = releasesMapSelectedVersion.keySet();
					for (Integer testCaseId : keySet) {
						int testCaseVersionId = Integer
								.parseInt(releasesMapSelectedVersion
										.get(testCaseId).replace("V", ""));
						List<TestCaseMapVersion> testCaseMapVersions = testCaseMapManager
								.getTestCaseMapVersion(testCaseId,
										testCaseVersionId);
						for (TestCaseMapVersion tesCaseMapVersion : testCaseMapVersions) {
							ReleaseTestCaseMapping releaseTestCaseMapping = new ReleaseTestCaseMapping();
							releaseTestCaseMapping
									.setClientProjectId(clientProjectId);
							releaseTestCaseMapping
									.setDeleted(release.isClosed());
							releaseTestCaseMapping.setReleaseId(releaseId);
							releaseTestCaseMapping.setTestCaseId(testCaseId);
							releaseTestCaseMapping
									.setTestCaseVersionId(testCaseVersionId);
							releaseTestCaseMapping
									.setTestCaseMapVersionId(tesCaseMapVersion
											.getTestCaseMapVersionId());

							releaseTestCaseMappingManagerImpl
									.persistReleaseTestCaseMapping(
											releaseTestCaseMapping);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Error : " + e.getMessage();
		}
		return "Success";
	}
}
