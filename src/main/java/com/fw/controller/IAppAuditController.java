package com.fw.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.fw.domain.AppAuditBean;
import com.fw.exceptions.APIExceptions;

public interface IAppAuditController {

	ResponseEntity<?> getSchemaNames() throws APIExceptions;

	ResponseEntity<?> getSchemaTableNames(String schemaName)
			throws APIExceptions;

	ResponseEntity<?> getTableColumnNames(String schemaName, String tableName)
			throws APIExceptions;

	ResponseEntity<List<AppAuditBean>> getAuditDetails(String tableName,
			String actionTime, String actionTimeCondition, String operation,
			String oldColumnName, String oldValue, String newColumnName,
			String newValue) throws APIExceptions;
}
