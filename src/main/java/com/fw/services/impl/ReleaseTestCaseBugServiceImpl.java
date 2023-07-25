package com.fw.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fw.dao.IReleaseTestCaseBugManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.AddReleaseMapBean;
import com.fw.domain.ReleaseTestCaseBug;
import com.fw.domain.TestCaseClientBean;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IReleaseMapService;
import com.fw.services.IReleaseTestCaseBugService;
import com.fw.utils.ApplicationCommonUtil;

@Service
public class ReleaseTestCaseBugServiceImpl
		implements IReleaseTestCaseBugService {

	private Logger log = Logger.getLogger(ReleaseTestCaseBugServiceImpl.class);

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Autowired
	IReleaseTestCaseBugManager releaseTestCaseBugManager;

	@Autowired
	IReleaseMapService releaseMapService;

	@Override
	@Transactional
	public ReleaseTestCaseBug persistReleaseTestCaseBug(int clientProjectId,
			ReleaseTestCaseBug releaseTestCaseBug) throws APIExceptions {
		updateDataSource();
		if (releaseTestCaseBug != null) {
			// Fetching the given release test cases currently mapped. It could
			// be empty as well if the release is not mapped to any test case
			// or it is newly created.
			List<TestCaseClientBean> releasesMap = releaseMapService
					.getReleasesMap(releaseTestCaseBug.getReleaseId(),
							clientProjectId, 0, null, null, null, 0, 0, null,
							null);
			releasesMap = releasesMap.stream()
					.filter(e -> e.isMappedToRelease())
					.collect(Collectors.toList());
			List<String> testCaseIdAndVersion = new ArrayList<String>();
			if (null == releasesMap || releasesMap.isEmpty()) {
				// No test case is mapped to the release yet
				testCaseIdAndVersion.add(releaseTestCaseBug.getTestCaseId()
						+ "::" + releaseTestCaseBug.getTestCaseVersionId());
			} else {
				// in case the older version of the test case is mapped,
				// updating it to the version for which the bug has been
				// created. After that, even if the later version of that test
				// case will be mapped to the new releases, it will be marked as
				// compulsory to execute in the release as the test case
				// (irrespective of the version) is associated with the
				// production bug .
				boolean isTestCaseAlreadyMappedToRelease = false;
				for (TestCaseClientBean testCaseClientBean : releasesMap) {
					if (testCaseClientBean
							.getTestCaseSequenceId() == releaseTestCaseBug
									.getTestCaseId()) {
						testCaseIdAndVersion.add(releaseTestCaseBug
								.getTestCaseId() + "::"
								+ releaseTestCaseBug.getTestCaseVersionId());
						isTestCaseAlreadyMappedToRelease = true;
					} else {
						testCaseIdAndVersion.add(testCaseClientBean
								.getTestCaseSequenceId() + "::"
								+ testCaseClientBean.getSelectedVersion());
					}
				}
				if (!isTestCaseAlreadyMappedToRelease) {
					testCaseIdAndVersion.add(releaseTestCaseBug.getTestCaseId()
							+ "::" + releaseTestCaseBug.getTestCaseVersionId());
				}
			}

			AddReleaseMapBean addReleaseMapBean = new AddReleaseMapBean();
			addReleaseMapBean.setReleaseId(releaseTestCaseBug.getReleaseId());
			addReleaseMapBean.setTestCaseIds(testCaseIdAndVersion);
			addReleaseMapBean
					.setCreatedBy(applicationCommonUtil.getCurrentUser());
			addReleaseMapBean
					.setModifiedBy(applicationCommonUtil.getCurrentUser());

			log.info("Adding test case bug info");
			ReleaseTestCaseBug persistReleaseTestCaseBug = releaseTestCaseBugManager
					.persistReleaseTestCaseBug(releaseTestCaseBug);
			if (null != persistReleaseTestCaseBug) {
				releaseMapService.deleteReleaseMapByReleaseUniqueId(
						clientProjectId, releaseTestCaseBug.getReleaseId(), 0);
				releaseMapService.persistReleaseMapBatch(clientProjectId,
						addReleaseMapBean);
			}
			return persistReleaseTestCaseBug;
		} else
			return null;
	}

	@Override
	@Transactional
	public int updateReleaseTestCaseBugById(
			ReleaseTestCaseBug releaseTestCaseBug) throws APIExceptions {
		updateDataSource();
		if (releaseTestCaseBug != null) {
			return releaseTestCaseBugManager
					.updateReleaseTestCaseBugById(releaseTestCaseBug);
		}
		return 0;
	}

	@Override
	@Transactional
	public void deleteReleaseTestCaseBugById(long releaseTestCaseBugId)
			throws APIExceptions {
		updateDataSource();
		releaseTestCaseBugManager
				.deleteReleaseTestCaseBugById(releaseTestCaseBugId);
	}

	@Override
	@Transactional
	public ReleaseTestCaseBug getReleaseTestCaseBugById(
			int releaseTestCaseBugId, String applicable, String isDeleted)
			throws APIExceptions {
		updateDataSource();
		return releaseTestCaseBugManager.getReleaseTestCaseBugById(
				releaseTestCaseBugId, applicable, isDeleted);
	}
	
	@Override
	@Transactional
	public List<ReleaseTestCaseBug> getReleaseTestCaseBugByTestCaseId(
			int testCaseId, String applicable, String isDeleted)
			throws APIExceptions {
		updateDataSource();
		return releaseTestCaseBugManager.getReleaseTestCaseBugByTestCaseId(
				testCaseId, applicable, isDeleted);
	}

	@Override
	public List<ReleaseTestCaseBug> getReleaseTestCaseBug(int releaseId,
			int testCaseId, String testCaseVersionId, String bugId,
			String bugType, String applicable, String isDeleted,
			String createDateStart, String createDateEnd,
			String modifiedDateStart, String modifiedDateEnd)
			throws APIExceptions {
		return releaseTestCaseBugManager.getReleaseTestCaseBug(releaseId,
				testCaseId, testCaseVersionId, bugId, bugType, applicable,
				isDeleted, createDateStart, createDateEnd, modifiedDateStart,
				modifiedDateEnd);
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}

}
