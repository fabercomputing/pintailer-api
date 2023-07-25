package com.fw.services;

import java.util.List;

import com.fw.domain.AppAuditBean;
import com.fw.exceptions.APIExceptions;

public interface IAppAuditService {

	List<String> getSchemaNames() throws APIExceptions;

	List<String> getSchemaTableNames(String schemaName) throws APIExceptions;

	List<String> getTableColumnNames(String schemaName, String tableName)
			throws APIExceptions;

	List<AppAuditBean> getAuditDetails(String schema_name, String table_name,
			String actionTime, String actionTimeCondition, String operation,
			String username, String oldColumnName, String newColumnName,
			String oldValue, String newValue) throws APIExceptions;
}
