package com.fw.controller.impl;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fw.config.AuthorizeUser;
import com.fw.controller.IAppAuditController;
import com.fw.domain.AppAuditBean;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;
import com.fw.services.IAppAuditService;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class AppAuditControllerImpl implements IAppAuditController {

	private static Logger log = Logger.getLogger(AppAuditControllerImpl.class);

	@Autowired
	IAppAuditService appAuditService;

	@Autowired
	AuthorizeUser authorizeUser;

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/appAudit/getSchemas", method = { GET })
	public ResponseEntity<?> getSchemaNames() throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<String>>(
				appAuditService.getSchemaNames(), HttpStatus.OK);
	}

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/appAudit/getSchemaTableNames", method = {
			GET })
	public ResponseEntity<?> getSchemaTableNames(
			@RequestParam("schemaName") String schemaName)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<String>>(
				appAuditService.getSchemaTableNames(schemaName), HttpStatus.OK);
	}

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/appAudit/getTableColumnNames", method = {
			GET })
	public ResponseEntity<?> getTableColumnNames(
			@RequestParam("schemaName") String schemaName,
			@RequestParam("tableName") String tableName) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<String>>(
				appAuditService.getTableColumnNames(schemaName, tableName),
				HttpStatus.OK);
	}

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/appAudit/getAuditInfo", method = { GET })
	public ResponseEntity<List<AppAuditBean>> getAuditDetails(
			@RequestParam("tableName") String tableName,
			@RequestParam(required = false, value = "operationTime", defaultValue = "") String actionTime,
			@RequestParam("operationTimeCondition") String actionTimeCondition,
			@RequestParam("operation") String operation,
			@RequestParam(required = false, value = "oldColumnName", defaultValue = "") String oldColumnName,
			@RequestParam(required = false, value = "oldValue", defaultValue = "") String oldValue,
			@RequestParam(required = false, value = "newColumnName", defaultValue = "") String newColumnName,
			@RequestParam(required = false, value = "newValue", defaultValue = "") String newValue)
			throws APIExceptions {
		return new ResponseEntity<List<AppAuditBean>>(
				appAuditService.getAuditDetails(
						PintailerConstants.DEFAULT_DB_NAME, tableName,
						actionTime, actionTimeCondition, operation, null,
						oldColumnName, newColumnName, oldValue, newValue),
				HttpStatus.OK);
	}
}
