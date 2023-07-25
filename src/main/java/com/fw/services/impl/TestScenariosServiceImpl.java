package com.fw.services.impl;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fw.bean.FeatureFileNameBean;
import com.fw.dao.ITestCaseMapManager;
import com.fw.dao.ITestScenarioStepManager;
import com.fw.dao.ITestScenariosManager;
import com.fw.domain.TestScenarioStepVersion;
import com.fw.domain.TestScenarios;
import com.fw.domain.TestScenariosVersion;
import com.fw.exceptions.APIExceptions;
import com.fw.services.ITestScenariosService;
import com.fw.services.ITestStepService;
import com.fw.utils.ApplicationCommonUtil;

@Service
public class TestScenariosServiceImpl implements ITestScenariosService {

	private Logger log = Logger.getLogger(TestScenariosServiceImpl.class);

	@Autowired
	ITestScenariosManager testScenariosManager;

	@Autowired
	ITestCaseMapManager testCaseMapManager;

	@Autowired
	ITestScenarioStepManager testScenarioStepManager;

	@Autowired
	ITestStepService testStepService;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	@Transactional
	public TestScenarios addTestScenarios(TestScenarios logEntity)
			throws APIExceptions {
		if (logEntity != null) {
			return testScenariosManager.persistTestScenarios(logEntity);
		} else
			return null;
	}

	@Override
	@Transactional
	public void updateTestScenariosById(TestScenarios logEntity)
			throws APIExceptions {
		if (logEntity != null) {
			TestScenarios testScenarios = testScenariosManager
					.getTestScenariosById(logEntity.getTestScenarioId(),
							"false");
			logEntity.setCreatedBy(testScenarios.getCreatedBy());
			testScenariosManager.updateTestScenariosById(logEntity);
		}
	}

//	@Override
//	@Transactional
//	public ResponseEntity<Void> deleteTestScenariosById(int testScenarioId)
//			throws APIExceptions {
//		testScenariosManager.deleteTestScenariosById(testScenarioId);
//		return new ResponseEntity<Void>(HttpStatus.OK);
//	}

	@Override
	public TestScenarios getTestScenariosById(int testScenarioId,
			String isDeleted) throws APIExceptions {
		return testScenariosManager.getTestScenariosById(testScenarioId,
				isDeleted);
	}

	@Override
	public List<TestScenarios> getTestScenariosByFeatureName(
			int clientProjectId, String featureName) throws APIExceptions {
		List<TestScenarios> testScenariosList = testScenariosManager
				.getTestScenariosByFeatureName(clientProjectId, featureName);
		ListIterator<TestScenarios> listIterator = testScenariosList
				.listIterator();
		while (listIterator.hasNext()) {
			TestScenarios testScenarios = listIterator.next();
			List<TestScenariosVersion> testScenariosVersionByScenarioId = testScenariosManager
					.getTestScenariosVersionByScenarioId(clientProjectId,
							testScenarios.getTestScenarioId());
			testScenarios.setExistingVersions(testScenariosVersionByScenarioId
					.stream().map(e -> e.getVersionId())
					.collect(Collectors.joining(",")));
		}
		return testScenariosList;
	}

	@Override
	public List<FeatureFileNameBean> getFeatureFileList(int clientProjectId)
			throws APIExceptions {
		List<String> featureList = testScenariosManager
				.getFeatureFileList(clientProjectId);
		FeatureFileNameBean getAllFeatureBean;
		List<FeatureFileNameBean> allFeatureList = new ArrayList<>();
		final ObjectMapper mapper = new ObjectMapper();
		for (String value : featureList) {
			Map<String, String> featueMap = new LinkedHashMap<>();
			featueMap.put("featureFileName", value);
			getAllFeatureBean = mapper.convertValue(featueMap,
					FeatureFileNameBean.class);
			allFeatureList.add(getAllFeatureBean);
		}
		return allFeatureList;
	}

	@Override
	public List<TestScenariosVersion> getTestScenariosVersionByHashCode(
			int clientProjectId, String featureFileName, String scenarioName)
			throws APIExceptions {
		return testScenariosManager.getTestScenariosVersionByHashCode(
				clientProjectId, featureFileName, scenarioName);
	}

	@Override
	public List<TestScenariosVersion> getTestScenariosVersionByScenarioId(
			int clientProjectId, int testScenariosId) throws APIExceptions {
		return testScenariosManager.getTestScenariosVersionByScenarioId(
				clientProjectId, testScenariosId);
	}

	@Override
	public TestScenarios getTestScenariosByScenarioIdAndVersionId(
			int clientProjectId, int testScenariosId,
			String testScenariosVersionId) throws APIExceptions {
		return testScenariosManager.getSpecificTestScenariosVersion(
				clientProjectId, testScenariosId,
				Integer.parseInt(testScenariosVersionId.replace("V", "")));
	}

	@Override
	public void saveScenarioStepVersionInfo(int testScenariosId,
			String scenarioHashCode, String scenarioSelectedVersion,
			String stepInfo) throws APIExceptions {
		TestScenarioStepVersion testScenarioStepVersion = new TestScenarioStepVersion();
		testScenarioStepVersion.setTestScenariosId(testScenariosId);
		testScenarioStepVersion.setTestScenariosHashcode(scenarioHashCode);
		testScenarioStepVersion.setTestScenariosVersionId(Integer
				.parseInt(scenarioSelectedVersion.replaceAll("V", "").trim()));
		testScenarioStepVersion.setTestStepIdVersionSequenceKeyword(stepInfo);
		testScenarioStepVersion
				.setCreatedBy(applicationCommonUtil.getCurrentUser());
		testScenarioStepVersion
				.setModifiedBy(applicationCommonUtil.getCurrentUser());
		testScenarioStepVersion.setHardDeleted(false);

		// Adding version info only if there is any difference in any of the
		// entity i.e. scenario hash code, scenario version id and step info
		List<TestScenarioStepVersion> versionData = testScenarioStepManager
				.getScenarioStepMappingVersion(scenarioHashCode,
						testScenarioStepVersion.getTestScenariosVersionId());
		if (null == versionData || versionData.isEmpty()) {
			testScenarioStepManager
					.persistTestScenarioStepVersion(testScenarioStepVersion);
		} else {
			if (versionData.get(0).getTestScenariosHashcode()
					.equals(scenarioHashCode)
					&& versionData.get(0)
							.getTestScenariosVersionId() == testScenarioStepVersion
									.getTestScenariosVersionId()
					&& versionData.get(0).getTestStepIdVersionSequenceKeyword()
							.equals(stepInfo)) {
				log.info(
						"version info already available for the given step and scenario mapping");
			} else {
				testScenarioStepManager.persistTestScenarioStepVersion(
						testScenarioStepVersion);
			}
		}
	}
}
