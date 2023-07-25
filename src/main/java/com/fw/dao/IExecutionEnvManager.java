package com.fw.dao;

import java.util.List;

import com.fw.domain.ExecutionEnv;
import com.fw.exceptions.APIExceptions;

public interface IExecutionEnvManager {

	ExecutionEnv persistExecutionEnv(ExecutionEnv logEntity)
			throws APIExceptions;

	int updateExecutionEnv(ExecutionEnv logEntity) throws APIExceptions;

	List<ExecutionEnv> getAllExecutionEnvs() throws APIExceptions;

	int deleteExecutionEnvById(int Id) throws APIExceptions;
}
