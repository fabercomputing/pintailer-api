package com.fw.services;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.fw.domain.TestScenarios;
import com.fw.domain.TestStep;
import com.fw.domain.TestStepVersion;
import com.fw.exceptions.APIExceptions;

public interface ITestStepService {

	TestStep addTestStep(TestStep testStep) throws APIExceptions;

	List<TestStep> getTestStep(int clientProjectId) throws APIExceptions;

	int updateTestStepById(TestStep testStep) throws APIExceptions;

	TestStep getTestStepById(long testStepId, int clientProjectId)
			throws APIExceptions;

	ResponseEntity<Void> deleteTestStepById(long testStepId)
			throws APIExceptions;

	void importTestStepFeature(MultipartFile uploadfile, int clientProjectId)
			throws APIExceptions, Exception;

	void importTestStepFromJava(MultipartFile uploadfile, int clientProjectId)
			throws APIExceptions, Exception;

	void updateTestStepById(TestStep testStep, boolean applicableFlg)
			throws APIExceptions;

	Map<Long, String> getTestStepsNameByIds(String testStepIds)
			throws APIExceptions;

	List<TestStepVersion> getTestStepsVersionByStepId(long testStepId)
			throws APIExceptions;

	Map<Integer, TestScenarios> getTestStepByScenarios(
			TestScenarios testScenarios) throws APIExceptions;
}
