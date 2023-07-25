package com.fw.services.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fw.dao.IAppAuditManager;
import com.fw.domain.AppAuditBean;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IAppAuditService;

@Service
public class AppAuditServiceImpl implements IAppAuditService {

	private Logger log = Logger.getLogger(AppAuditServiceImpl.class);

	@Autowired
	IAppAuditManager appAuditManager;

	@Override
	public List<String> getSchemaNames() throws APIExceptions {
		return appAuditManager.getSchemaNames();
	}

	@Override
	public List<String> getSchemaTableNames(String schemaName)
			throws APIExceptions {
		return appAuditManager.getSchemaTableNames(schemaName);
	}

	@Override
	public List<String> getTableColumnNames(String schemaName, String tableName)
			throws APIExceptions {
		return appAuditManager.getTableColumnNames(schemaName, tableName);
	}

	@Override
	public List<AppAuditBean> getAuditDetails(String schema_name,
			String table_name, String actionTime, String actionTimeCondition,
			String operation, String username, String oldColumnName,
			String newColumnName, String oldValue, String newValue)
			throws APIExceptions {
		log.info("Fetching logs");
		return appAuditManager.getAuditDetails(schema_name, table_name,
				actionTime, actionTimeCondition, operation, username,
				oldColumnName, newColumnName, oldValue, newValue);
	}
}
