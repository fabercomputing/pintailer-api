package com.fw.controller;

import org.springframework.http.ResponseEntity;

import com.fw.domain.ExecutionEnv;
import com.fw.exceptions.APIExceptions;

public interface IExecutionEnvController {

	ResponseEntity<?> addExecutionEnv(ExecutionEnv unit) throws APIExceptions;

	ResponseEntity<?> updateExecutionEnv(ExecutionEnv unit)
			throws APIExceptions;

	ResponseEntity<?> getAllExecutionEnv() throws APIExceptions;

	ResponseEntity<?> deleteExecutionEnv(int envId) throws APIExceptions;
}
