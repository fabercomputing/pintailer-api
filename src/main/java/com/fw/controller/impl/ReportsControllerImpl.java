package com.fw.controller.impl;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fw.bean.AutomationReportBean;
import com.fw.bean.ExecutionReportBean;
import com.fw.bean.ProgressReportBean;
import com.fw.config.AuthorizeUser;
import com.fw.controller.IReportsController;
import com.fw.domain.DashboardGraph;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IReportsService;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class ReportsControllerImpl implements IReportsController {

	private static Logger log = Logger.getLogger(ReportsControllerImpl.class);

	@Autowired
	IReportsService reportsService;

	@Autowired
	AuthorizeUser authorizeUser;

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/reports/getDashboardReport", method = {
			GET })
	public ResponseEntity<?> getDashboardReport() throws APIExceptions {
		List<DashboardGraph> dashboardGraphReport = reportsService
				.getDashboardGraphReport();
		if (null == dashboardGraphReport) {
			return new ResponseEntity<String>(
					"User is not assigned to any project. Atleast one project "
							+ "is required to continue.",
					HttpStatus.NOT_FOUND);
		}
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<DashboardGraph>>(
				reportsService.getDashboardGraphReport(), HttpStatus.OK);
	}

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/reports/getAutomationReport", method = {
			GET })
	public ResponseEntity<?> getAutomationReport(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("tagValue") String tagValue,
			@RequestParam("moduleId") long moduleId,
			@RequestParam("applicable") String applicable,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<AutomationReportBean>(
				reportsService.getAutomationReport(clientProjectId, tagValue,
						moduleId, applicable, startDate, endDate),
				HttpStatus.OK);
	}

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/reports/getExecutionReport", method = {
			GET })
	public ResponseEntity<?> getExecutionReport(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("moduleId") int moduleId,
			@RequestParam("releaseId") int releaseId,
			@RequestParam("environmentId") int environmentId)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<ExecutionReportBean>(
				reportsService.getExecutionReport(clientProjectId, moduleId,
						releaseId, environmentId, "true"),
				HttpStatus.OK);
	}

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/reports/downloadReport", method = { GET })
	public ResponseEntity<String> downloadReport(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("releaseId") int releaseId,
			@RequestParam("environmentId") int environmentId,
			@RequestParam("reportFileFormat") String reportFileFormat,
			@RequestParam("reportFilePath") String reportFilePath)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		try {
			return new ResponseEntity<String>(
					reportsService.downloadClientReport(clientProjectId,
							releaseId, environmentId, reportFileFormat,
							reportFilePath, "true"),
					HttpStatus.OK);
		} catch (APIExceptions e) {
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.NO_CONTENT);
		}
	}

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/reports/downloadManualExecutionTemplate", method = {
			GET })
	public ResponseEntity<String> downloadManualExecutionTemplate(
			@RequestParam("templateFormat") String templateFormat,
			@RequestParam("fileName") String fileName,
			@RequestParam("filePath") String filePath,
			@RequestParam("testCaseIds") String testCaseIds)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<String>(
				reportsService.downloadManualExecutionTemplate(templateFormat,
						fileName, filePath, testCaseIds),
				HttpStatus.OK);
	}

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/reports/downloadTestCases", method = {
			GET })
	public ResponseEntity<String> downloadTestCases(
			@RequestParam("format") String format,
			@RequestParam("fileName") String fileName,
			@RequestParam("filePath") String filePath,
			@RequestParam("testCaseIds") String testCaseIds)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<String>(reportsService.downloadTestCases(
				format, fileName, filePath, testCaseIds), HttpStatus.OK);
	}

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/reports/getTestCasesOfSpecificActivityStatus", method = {
			GET })
	public ResponseEntity<?> getTestCasesOfSpecificActivityStatus(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("moduleId") int moduleId,
			@RequestParam("isApplicable") String isApplicable,
			@RequestParam("isDeleted") String isDeleted,
			@RequestParam("format") String format,
			@RequestParam("filePath") String filePath,
			@RequestParam("fileName") String fileName) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<String>(reportsService
				.getTestCasesOfSpecificActivityStatus(clientProjectId, moduleId,
						isApplicable, isDeleted, format, filePath, fileName),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/reports/getProgressReport", method = {
			GET })
	public ResponseEntity<?> getAutomationProgress(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("applicable") String applicable,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity<ProgressReportBean>(
				reportsService.getAutomationProgress(clientProjectId,
						applicable, startDate, endDate),
				HttpStatus.OK);
	}

}
