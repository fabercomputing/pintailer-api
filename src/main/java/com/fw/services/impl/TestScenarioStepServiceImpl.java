package com.fw.services.impl;

import java.util.ArrayList;
/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.util.List;
import java.util.ListIterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fw.dao.ITestScenarioStepManager;
import com.fw.dao.ITestScenariosManager;
import com.fw.dao.ITestStepManager;
import com.fw.domain.TestScenarioStep;
import com.fw.domain.TestScenarioStepVersion;
import com.fw.domain.TestScenarios;
import com.fw.domain.TestStep;
import com.fw.domain.TestStepVersion;
import com.fw.exceptions.APIExceptions;
import com.fw.services.ITestScenarioStepService;
import com.fw.services.ITestScenariosService;
import com.fw.services.ITestStepService;
import com.fw.utils.ValueValidations;

@Service
public class TestScenarioStepServiceImpl implements ITestScenarioStepService {

	@Autowired
	ITestScenarioStepManager testScenarioStepManager;

	@Autowired
	ITestScenariosService testScenariosService;

	@Autowired
	ITestScenariosManager testScenariosManager;

	@Autowired
	ITestStepService testStepService;

	@Autowired
	ITestStepManager testStepManager;

	@Override
	@Transactional
	public TestScenarioStep addTestScenarioStep(TestScenarioStep logEntity)
			throws APIExceptions {
		if (logEntity != null) {
			return testScenarioStepManager.persistTestScenarioStep(logEntity);
		} else
			return null;
	}

	@Override
	@Transactional
	public void updateTestScenarioStepById(int clientProjectId,
			TestScenarioStep logEntity) throws APIExceptions {
		if (logEntity != null) {
			TestScenarioStep clientProjects = testScenarioStepManager
					.getTestScenarioStepById(clientProjectId,
							logEntity.getTestScenarioStepId());
			logEntity.setCreatedBy(clientProjects.getCreatedBy());
			testScenarioStepManager.updateTestScenarioStepById(logEntity);
		}

	}

//	@Override
//	@Transactional
//	public ResponseEntity<Void> deleteTestScenarioStepById(long id)
//			throws APIExceptions {
//		testScenarioStepManager.deleteTestScenarioStepById(id);
//		return new ResponseEntity<Void>(HttpStatus.OK);
//	}

	@Override
	public List<TestScenarioStep> getTestScenarioStep(int clientProjectId)
			throws APIExceptions {
		return testScenarioStepManager.getAllTestScenarioStep(clientProjectId);
	}

	@Override
	public TestScenarioStep getTestScenarioStepById(int clientProjectId,
			long id) throws APIExceptions {
		return testScenarioStepManager.getTestScenarioStepById(clientProjectId,
				id);
	}

	@Override
	public List<TestScenarioStepVersion> getScenarioStepMappingVersion(
			int clientProjectId, String featureFileName, String scenarioName,
			int testScenariosVersionId) throws APIExceptions {
		if (!ValueValidations.isValueValid(scenarioName)) {
			List<TestScenarios> testScenariosList = testScenariosService
					.getTestScenariosByFeatureName(clientProjectId,
							featureFileName);
			List<TestScenarioStepVersion> result = new ArrayList<TestScenarioStepVersion>();
			for (TestScenarios testScenarios : testScenariosList) {
				result.addAll(testScenarioStepManager
						.getScenarioStepMappingVersion(clientProjectId,
								featureFileName, testScenarios.getName(),
								testScenariosVersionId));
			}
			return result;
		} else {
			return testScenarioStepManager.getScenarioStepMappingVersion(
					clientProjectId, featureFileName, scenarioName,
					testScenariosVersionId);
		}
	}

	@Override
	public List<TestScenarioStepVersion> getScenarioStepMappingVersionByScenarioId(
			int clientProjectId, int testScenarioId) throws APIExceptions {
		List<TestScenarioStepVersion> scenarioStepMappingVersion = testScenarioStepManager
				.getScenarioStepMappingVersion(testScenarioId, 0);
		ListIterator<TestScenarioStepVersion> itr = scenarioStepMappingVersion
				.listIterator();
		while (itr.hasNext()) {
			TestScenarioStepVersion testScenarioStepVersion = itr.next();
			testScenarioStepVersion.setTestScenarios(testScenariosManager
					.getSpecificTestScenariosVersion(clientProjectId,
							testScenarioStepVersion.getTestScenariosHashcode(),
							testScenarioStepVersion
									.getTestScenariosVersionId()));
//			testScenarioStepVersion
//					.setTestScenariosStepVersion(testScenarioStepVersion
//							.getTestScenarios().getScenarioSelectedVersion());
			testScenarioStepVersion.setTestSteps(
					getStepsInfo(clientProjectId, testScenarioStepVersion));
		}
		return scenarioStepMappingVersion;
	}

	private List<TestStep> getStepsInfo(int clientProjectId,
			TestScenarioStepVersion testScenarioStepVersion)
			throws APIExceptions {

		String[] stepInfosList = testScenarioStepVersion
				.getTestStepIdVersionSequenceKeyword().split(",");
		List<TestStep> testStepList = new ArrayList<TestStep>();
		for (String stepInfos : stepInfosList) {
			String[] stepInfo = stepInfos.split("-");
			List<TestStepVersion> testStepsVersionsList = testStepService
					.getTestStepsVersionByStepId(Long.parseLong(stepInfo[0]));
			TestStep testStep = null;
			if (null == testStepsVersionsList
					|| testStepsVersionsList.isEmpty()) {
				testStep = testStepService.getTestStepById(
						Long.parseLong(stepInfo[0]), clientProjectId);
				testStep.setStepSelectedVersion(
						testStep.getStepLatestVersion());
				testStep.setName(ValueValidations.isValueValid(stepInfo[3])
						? (stepInfo[3] + " ")
						: "" + testStep.getName());
			} else {
				testStep = testStepService.getTestStepById(
						Long.parseLong(stepInfo[0]), clientProjectId);

				TestStepVersion testStepsVersionByStepIdAndVersionId = testStepManager
						.getTestStepsVersionByStepIdAndVersionId(
								Long.parseLong(stepInfo[0]),
								Integer.parseInt(stepInfo[1].replace("V", "")));
				testStep.setClientProjectId(testStepsVersionByStepIdAndVersionId
						.getClientProjectId());
				testStep.setCreatedBy(
						testStepsVersionByStepIdAndVersionId.getCreatedBy());
				testStep.setDeleted(
						testStepsVersionByStepIdAndVersionId.isDeleted());
				testStep.setHashCode(
						testStepsVersionByStepIdAndVersionId.getHashCode());
				testStep.setModifiedBy(
						testStepsVersionByStepIdAndVersionId.getModifiedBy());
				testStep.setName(ValueValidations.isValueValid(stepInfo[3])
						? (stepInfo[3] + " ")
						: "" + testStepsVersionByStepIdAndVersionId.getName());
				testStep.setStepSelectedVersion(stepInfo[1]);
			}
			testStepList.add(testStep);
		}
		return testStepList;
	}

	@Override
	public TestScenarioStepVersion getScenarioStepMappingById(
			int clientProjectId, int testScenarioStepVersionId)
			throws APIExceptions {
		TestScenarioStepVersion testScenarioStepVersion = testScenarioStepManager
				.getScenarioStepMappingVersion(testScenarioStepVersionId);

		TestScenarios testScenarios = testScenariosManager
				.getSpecificTestScenariosVersion(clientProjectId,
						testScenarioStepVersion.getTestScenariosId(),
						testScenarioStepVersion.getTestScenariosVersionId());
		if (null == testScenarios) {
			testScenarios = testScenariosManager.getTestScenariosById(
					testScenarioStepVersion.getTestScenariosId(), "false");
		}

		testScenarioStepVersion.setTestScenarios(testScenarios);
		testScenarioStepVersion.setTestSteps(
				getStepsInfo(clientProjectId, testScenarioStepVersion));
		return testScenarioStepVersion;
	}
}
