package com.fw.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fw.dao.IReleaseTestCaseMappingManager;
import com.fw.domain.ReleaseTestCaseMapping;
import com.fw.domain.TestCaseMap;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IReleaseTestCaseMappingService;
import com.fw.services.ITestCaseMapService;
import com.fw.utils.ApplicationCommonUtil;
import com.fw.utils.ValueValidations;

@Service
public class ReleaseTestCaseMappingServiceImpl
		implements IReleaseTestCaseMappingService {

	@Autowired
	IReleaseTestCaseMappingManager releaseTestCaseMappingManager;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Autowired
	ITestCaseMapService testCaseMapService;

	@Override
	public ReleaseTestCaseMapping persistReleaseTestCaseMapping(
			ReleaseTestCaseMapping releaseTestCaseMapping)
			throws APIExceptions {
		return releaseTestCaseMappingManager
				.persistReleaseTestCaseMapping(releaseTestCaseMapping);
	}

	@Override
	public int persistReleaseTestCaseMappingInBatch(int clientProjectId,
			int releaseId, List<String> testCaseIds) throws APIExceptions {
//		String[] testCaseList = testCaseIds.split(",");
		List<Object[]> args = new ArrayList<Object[]>();
		try {
			for (int i = 0; i < testCaseIds.size(); i++) {
				String[] testCaseInfo = testCaseIds.get(i).split("::");
				if (!ValueValidations.isValueValid(testCaseInfo[0])
						|| testCaseInfo[0].equals("0")
						|| ValueValidations.isValueNull(testCaseInfo[1])) {
					throw new APIExceptions(
							"Invalid test case id is given for release mapping");
				}
				List<TestCaseMap> testCaseMaps = testCaseMapService
						.getTestStepByTestCaseId(clientProjectId, releaseId,
								Integer.parseInt(testCaseInfo[0]),
								testCaseInfo[1], true); // Temp
//				String testCaseMapIds = testCaseMaps.stream()
//						.map(e -> new String("" + e.getTestCaseMapId()))
//						.collect(Collectors.joining(", "));

//				boolean firstTime = true;
//				for (TestCaseMap testCaseMap : testCaseMaps) {
//					if (firstTime) {
//						testCaseMapIds = "" + testCaseMap.getTestCaseMapId();
//						firstTime = false;
//					} else {
//						testCaseMapIds += testCaseMap.getTestCaseMapId();
//					}
//				}
				if (!(null == testCaseMaps || testCaseMaps.isEmpty())) {
//					for (String testCaseMapId : testCaseMapIds.split(",")) {
					args.add(new Object[] { clientProjectId, releaseId,
							Integer.parseInt(testCaseInfo[0]),
							Integer.parseInt(testCaseInfo[1].replace("V", "")),
							testCaseMaps.get(0).getTestCaseMapId(), false,
							applicationCommonUtil.getCurrentUser(),
							applicationCommonUtil.getCurrentUser() });
//					}
				}
			}
			if(args.size() > 0){
				releaseTestCaseMappingManager
						.persistReleaseTestCaseMappingInBatch(args);
				return args.size();
			}
		} catch (Exception e) {
			throw new APIExceptions(
					"Error : Issue occured while mapping release with test cases "
							+ "mapping info for release [" + releaseId + "]");
		}
		return 0;
	}

	@Override
	public int deleteReleaseMap(int clientProjectId, int releaseId,
			String testCaseMapIds) throws APIExceptions {
		return releaseTestCaseMappingManager.deleteReleaseMap(clientProjectId,
				releaseId, testCaseMapIds);
	}

	@Override
	public List<ReleaseTestCaseMapping> getReleaseTestCaseMapping(
			int clientProjectId, int releaseId) throws APIExceptions {
		return releaseTestCaseMappingManager
				.getReleaseTestCaseMapping(clientProjectId, releaseId);
	}

	@Override
	public List<ReleaseTestCaseMapping> getReleaseTestCaseMapping(
			int clientProjectId, int releaseId, int testCaseId,
			int testCaseVersionId) throws APIExceptions {
		return releaseTestCaseMappingManager.getReleaseTestCaseMapping(
				clientProjectId, releaseId, testCaseId, testCaseVersionId);
	}

}
