package com.fw.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fw.dao.IReleaseMapManager;
import com.fw.domain.AddReleaseMapBean;
import com.fw.domain.Modules;
import com.fw.domain.ReleaseMap;
import com.fw.domain.ReleaseMapVersion;
import com.fw.domain.ReleaseTestCaseBug;
import com.fw.domain.TestCaseClientBean;
import com.fw.domain.TestCaseVersion;
import com.fw.enums.BugTypes;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IModulesService;
import com.fw.services.IReleaseMapService;
import com.fw.services.IReleaseService;
import com.fw.services.IReleaseTestCaseBugService;
import com.fw.services.IReleaseTestCaseMappingService;
import com.fw.services.ITestCaseMapService;
import com.fw.services.ITestCaseService;
import com.fw.utils.ValueValidations;

@Service
public class ReleaseMapServiceImpl implements IReleaseMapService {

	@Autowired
	IReleaseMapManager releaseMapManager;

	@Autowired
	ITestCaseService testCaseService;

	@Autowired
	IReleaseService releaseService;

	@Autowired
	IModulesService modulesService;

	@Autowired
	IReleaseTestCaseMappingService releaseTestCaseMappingService;

	@Autowired
	IReleaseTestCaseBugService releaseTestCaseBugService;

	@Override
	@Transactional
	public ReleaseMap persistReleaseMap(int clientProjectId,
			ReleaseMap releaseMap) throws APIExceptions {
		if (releaseMap != null) {
			ReleaseMap persistReleaseMap = releaseMapManager
					.persistReleaseMap(releaseMap);
			if (null == persistReleaseMap) {
				throw new APIExceptions(
						"Error occured while assigning test cases to release.");
			}
			if (releaseTestCaseMappingService
					.persistReleaseTestCaseMappingInBatch(clientProjectId,
							releaseMap.getReleaseId(), new ArrayList<String>(
									releaseMap.getTestCaseId())) < 0) {
				throw new APIExceptions(
						"Error occured while saving the test case mapping and release version info.");
			}
			return persistReleaseMap;
		} else
			return null;
	}

	@Override
	@Transactional
	public Integer persistReleaseMapBatch(int clientProjectId,
			AddReleaseMapBean addReleaseMapBean) throws APIExceptions {
		if (addReleaseMapBean != null
				&& addReleaseMapBean.getTestCaseIds().size() > 0) {
			int batchCount = 0;
			List<Object[]> args = new ArrayList<Object[]>();
//			boolean firstTime = true;
//			String testCaseIds = null;
			for (int i = 0; i < addReleaseMapBean.getTestCaseIds()
					.size(); i++) {
				batchCount++;
				String[] testCaseInfo = addReleaseMapBean.getTestCaseIds()
						.get(i).split("::");
				if (!ValueValidations.isValueValid(testCaseInfo[0])
						|| testCaseInfo[0].equals("0")
						|| ValueValidations.isValueNull(testCaseInfo[1])) {
					throw new APIExceptions(
							"Invalid test case id is given for release mapping");
				}
				args.add(new Object[] { addReleaseMapBean.getReleaseId(),
						Integer.parseInt(testCaseInfo[0]),
						Integer.parseInt(testCaseInfo[1].replace("V", "")),
						addReleaseMapBean.getCreatedBy(),
						addReleaseMapBean.getModifiedBy() });
				if (batchCount == 50) {
					releaseMapManager.persistReleaseMapInBatch(args);
					args.clear();
					batchCount = 0;
				}
			}
			/*
			 * Storing the release and test case mapping info i.e. the test
			 * cases and there specific versions which are mapped to the release
			 * is stored here
			 */
			releaseMapManager.persistReleaseMapInBatch(args);

			if (releaseTestCaseMappingService
					.persistReleaseTestCaseMappingInBatch(clientProjectId,
							addReleaseMapBean.getReleaseId(),
							addReleaseMapBean.getTestCaseIds()) < 0) {
				throw new APIExceptions(
						"Error occured while saving the test case mapping and release version info.");
			}
			addVersionInfo(addReleaseMapBean);

			return batchCount;
		} else
			throw new APIExceptions(
					"Error : Issue occured while mappin test cases with the given release ["
							+ addReleaseMapBean.getReleaseId() + "]");
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

	@Override
	@Transactional
	public ReleaseMap updateReleaseMap(ReleaseMap logEntity)
			throws APIExceptions {
		if (logEntity != null) {
			if (releaseMapManager.updateReleaseMap(logEntity) > 0) {
				return logEntity;
			} else {
				return null;
			}
		} else {
			return null;
		}

	}

	/**
	 * Release map code works as in fetching all the test cases as per the
	 * filter but without passing the release id. This is done after the release
	 * id is mapped to the project. Previously release id was associated to the
	 * organization, so there was separate API to fetch the test cases and then
	 * this API was suppose to return the test cases to be selected which are
	 * mapped to the given release on release mapping page. Later, this is
	 * updated as in to fetch the test cases and then added the info in the
	 * selected test cases i.e. populating specific fields of the test cases to
	 * check them on the release mapping page.
	 */
	@Override
	public List<TestCaseClientBean> getReleasesMap(int releaseId,
			int clientProjectId, long moduleId, String searchTxt,
			String sortByColumn, String ascOrDesc, int limit, int pageNumber,
			String startDate, String endDate) throws APIExceptions {
		List<Long> allChildModules = modulesService
				.getAllChildModules(moduleId);
		String moduleIds = allChildModules.stream().map(Object::toString)
				.distinct().collect(Collectors.joining(", "));
		List<TestCaseClientBean> testCasesList = testCaseService
				.getTestCaseList(clientProjectId, 0, moduleIds, null, "true",
						null, searchTxt, sortByColumn, ascOrDesc, limit,
						pageNumber, startDate, endDate, "false", false);

		// Update if the test case has a production bug or not. If yes, the test
		// case cannot be removed from the release and will be default selected
		testCasesList = updateTestCaseCompulsoryExecutionFlag(releaseId,
				testCasesList);
		return updateVersionInfo(clientProjectId, releaseId, testCasesList);

//		return multipleTestCasesVersionMap;
	}

	/**
	 * method is for add version info of the test case which is mapped to the
	 * release
	 */
	public List<TestCaseClientBean> updateVersionInfo(int clientProjectId,
			int releaseId, List<TestCaseClientBean> testCasesList)
			throws APIExceptions {
		// Map<TestCaseId, TestCaseVersion>
		final Map<Integer, String> testCaseIdAndVersionList = releaseMapManager
				.getReleasesMapSelectedVersion(releaseId, clientProjectId);
		ListIterator<TestCaseClientBean> itr = testCasesList.listIterator();
		while (itr.hasNext()) {
			TestCaseClientBean testCaseClientBean = itr.next();
			/*
			 * Perform further operation if the test cases is mapped to given
			 * release
			 */
			if (ValueValidations.isValueValid(testCaseIdAndVersionList
					.get(testCaseClientBean.getTestCaseSequenceId()))) {
				testCaseClientBean.setSelectedVersion(testCaseIdAndVersionList
						.get(testCaseClientBean.getTestCaseSequenceId()));
				if (!testCaseClientBean.getSelectedVersion()
						.equals(testCaseClientBean.getLatestVersion())) {
					TestCaseVersion testCaseVersion = testCaseService
							.getTestCaseVersion(
									testCaseClientBean.getTestCaseSequenceId(),
									Integer.parseInt(testCaseClientBean
											.getSelectedVersion()
											.replace("V", "")));
					if (null != testCaseVersion) {
						testCaseClientBean = mapTestCaseVersionWithClientBean(
								testCaseVersion, null, testCaseClientBean);
					}
				}
				testCaseClientBean.setMappedToRelease(true);
			} else {
				testCaseClientBean.setSelectedVersion(
						testCaseClientBean.getLatestVersion());
				testCaseClientBean.setMappedToRelease(false);
			}
		}
		return testCasesList;
	}

	/**
	 * Add test case bugs info. If any one of the bug is Production bug than set
	 * the production bug flag as true so that it can be made compulsory on the
	 * release map page
	 */
	@Override
	public List<TestCaseClientBean> updateTestCaseCompulsoryExecutionFlag(
			int releaseId, List<TestCaseClientBean> testCasesList)
			throws APIExceptions {
		ListIterator<TestCaseClientBean> itr = testCasesList.listIterator();
		while (itr.hasNext()) {
			TestCaseClientBean testCaseClientBean = itr.next();
			List<ReleaseTestCaseBug> releaseTestCaseBug = releaseTestCaseBugService
					.getReleaseTestCaseBug(releaseId,
							testCaseClientBean.getTestCaseSequenceId(), null,
							null, null, "true", "false", null, null, null,
							null);
			if (!(null == releaseTestCaseBug || releaseTestCaseBug.isEmpty())) {
				List<String> testCaseBugsList = releaseTestCaseBug.stream()
						.map(e -> e.getBugId() + "::" + e.getBugType())
						.collect(Collectors.toList());
				for (String bug : testCaseBugsList) {
					if (bug.split("::")[1].toUpperCase()
							.equals(BugTypes.PRODUCTION.name())) {
						testCaseClientBean.setProductionBug(true);
						break;
					}
				}
				testCaseClientBean
						.setBugsAndTypes(String.join(",", testCaseBugsList));
			}
		}

		return testCasesList;
	}

	private TestCaseClientBean mapTestCaseVersionWithClientBean(
			TestCaseVersion testCaseVersion, String moduleHierarchy,
			TestCaseClientBean testCaseClientBean) throws APIExceptions {
		testCaseClientBean
				.setTestCaseSequenceId(testCaseVersion.getTestCaseId());
		testCaseClientBean.setTestCaseNo(testCaseVersion.getTestCaseNo());
		testCaseClientBean.setTestData(testCaseVersion.getTestData());
		testCaseClientBean.setModuleId(testCaseVersion.getModuleId());
		testCaseClientBean.setTestSummary(testCaseVersion.getTestSummary());
		testCaseClientBean.setPreCondition(testCaseVersion.getPreCondition());
		testCaseClientBean.setTags(testCaseVersion.getTags());
		testCaseClientBean
				.setExecutionSteps(testCaseVersion.getExecutionSteps());
		testCaseClientBean
				.setExpectedResult(testCaseVersion.getExpectedResult());
		testCaseClientBean.setAutomatable(testCaseVersion.isAutomatable());
		testCaseClientBean.setRemarks(testCaseVersion.getRemarks());
		testCaseClientBean.setFileName(testCaseVersion.getFileName());
		testCaseClientBean.setAutomatedTestCaseNoFromFile(
				testCaseVersion.getAutomatedTestCaseNoFromFile());
		testCaseClientBean.setManualReason(testCaseVersion.getManualReason());
		testCaseClientBean.setApplicable(testCaseVersion.isApplicable());
		testCaseClientBean.setCreatedBy(testCaseVersion.getCreatedBy());
		testCaseClientBean.setModifiedBy(testCaseVersion.getModifiedBy());
		testCaseClientBean.setCreatedDate(testCaseVersion.getCreatedDate());
		testCaseClientBean.setDeleted(testCaseVersion.isDeleted());

		if (!(null == moduleHierarchy || moduleHierarchy.trim().equals("")
				|| moduleHierarchy.trim().equals("null"))) {
			testCaseClientBean.setModulesNameHierarchy(moduleHierarchy);
		} else {
			testCaseClientBean.setModulesNameHierarchy(testCaseService
					.getFinalModuleHierarchy(testCaseVersion.getModuleId()));
		}

		return testCaseClientBean;
	}

	@Override
	@Transactional
	public int deleteReleaseMap(int releaseMapId) throws APIExceptions {
		return releaseMapManager.deleteReleaseMap(releaseMapId);
	}

	@Autowired
	ITestCaseMapService testCaseMapService;

	@Override
	@Transactional
	public int deleteReleaseMapByReleaseUniqueId(int clientProjectId,
			int releaseId, long moduleId) throws APIExceptions {
//		List<TestCase> testCasesList = new ArrayList<TestCase>();
		if (null != releaseService
				.getReleaseByProjectAndRelease(clientProjectId, releaseId)) {
			String moduleList = "";
			if (moduleId > 0) {
				List<Long> allChildModules = modulesService
						.getAllChildModules(moduleId);
				/* for release and testcase mapping version info */
//				for (long childModuleId : allChildModules) {
//					testCasesList.addAll(
//							testCaseService.getTestCaseBeanListByModuleId(
//									childModuleId, "all", "false"));
//				}
				/*******/
				moduleList = StringUtils.join(allChildModules, ",");
			} else {
				List<Modules> modulesByProjectId = modulesService
						.getModulesByProjectId(clientProjectId);
				String separator = "";
				for (Modules modules : modulesByProjectId) {
					List<Long> allChildModules = modulesService
							.getAllChildModules(modules.getModuleId());
					moduleList += separator
							+ StringUtils.join(allChildModules, ",");
					separator = ",";
				}
			}
			/*
			 * Deleting the release and test case mapping info to store the
			 * latest mapping info for the given release id
			 */

			List<TestCaseClientBean> testCaseList = testCaseService
					.getTestCaseList(clientProjectId, releaseId, moduleList,
							null, "all", null, null, null, null, 0, 0, null,
							null, "false", false);
//			boolean firstTime = true;
//			String testCaseMapIds = null;
//			for (TestCase testCase : testCasesList) {
//				List<TestCaseMap> testCaseMapList = testCaseMapService
//						.getTestStepByTestCaseId(clientProjectId, releaseId,
//								testCase.getTestCaseId(), "V1");
//				for (TestCaseMap testCaseMap : testCaseMapList) {
//					if (firstTime) {
//						testCaseMapIds = "" + testCaseMap.getTestCaseMapId();
//						firstTime = false;
//					} else {
//						testCaseMapIds += testCaseMap.getTestCaseMapId();
//					}
//				}
//			}
			String testCaseIds = testCaseList.stream()
					.map(e -> "" + e.getTestCaseSequenceId())
					.collect(Collectors.joining(","));
			if (ValueValidations.isValueValid(testCaseIds)
					&& releaseTestCaseMappingService.deleteReleaseMap(
							clientProjectId, releaseId, testCaseIds) < 0) {
				throw new APIExceptions(
						"Error occured while removing release and test case "
								+ "mapping info. System will behave unexpectedly.");
			}
			/*******/
			return releaseMapManager
					.deleteReleaseMapByReleaseUniqueIdAndTestCaseId(
							clientProjectId, releaseId, moduleList);
		} else {
			throw new APIExceptions(
					"One of the relase or project given is invalid. Please try again later.");
		}
	}

	@Override
	public List<ReleaseMapVersion> getReleasesMapVersion(int releaseId)
			throws APIExceptions {
		return releaseMapManager.getReleasesMapVersion(releaseId);
	}
}
