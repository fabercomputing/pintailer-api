package com.fw.services;

import java.util.List;

import com.fw.domain.ExecutionEnv;
import com.fw.exceptions.APIExceptions;

public interface IExecutionEnvService {

	ExecutionEnv addExecutionEnv(ExecutionEnv unit) throws APIExceptions;

	int updateExecutionEnv(ExecutionEnv logEntity) throws APIExceptions;

	List<ExecutionEnv> getAllExecutionEnvs() throws APIExceptions;

	int deleteExecutionEnvById(int Id) throws APIExceptions;
}
