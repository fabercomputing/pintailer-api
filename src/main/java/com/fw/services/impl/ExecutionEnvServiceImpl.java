package com.fw.services.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fw.dao.IExecutionEnvManager;
import com.fw.domain.ExecutionEnv;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IExecutionEnvService;

@Service
public class ExecutionEnvServiceImpl implements IExecutionEnvService {

	private Logger log = Logger.getLogger(ExecutionEnvServiceImpl.class);

	@Autowired
	IExecutionEnvManager executionEnvManager;

	@Override
	@Transactional
	public ExecutionEnv addExecutionEnv(ExecutionEnv logEntity)
			throws APIExceptions {
		if (!(null == logEntity || null == logEntity.getExecutionEnvName() || logEntity
				.getExecutionEnvName().trim().equals(""))) {
			return executionEnvManager.persistExecutionEnv(logEntity);
		} else {
			log.error("Invalid execution environment is given");
			throw new APIExceptions("Invalid execution environment is given");
		}
	}

	@Override
	@Transactional
	public int updateExecutionEnv(ExecutionEnv logEntity) throws APIExceptions {
		if (!(null == logEntity || null == logEntity.getExecutionEnvName() || logEntity
				.getExecutionEnvName().trim().equals(""))) {
			return executionEnvManager.updateExecutionEnv(logEntity);
		}
		log.error("Invalid execution environment is given for update");
		return 0;
	}

	@Override
	public List<ExecutionEnv> getAllExecutionEnvs() throws APIExceptions {
		return executionEnvManager.getAllExecutionEnvs();
	}

	@Override
	@Transactional
	public int deleteExecutionEnvById(int id) throws APIExceptions {
		return executionEnvManager.deleteExecutionEnvById(id);
	}
}