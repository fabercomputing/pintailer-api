package com.fw.services;

import java.util.List;

import com.fw.bean.AutomationReportBean;
import com.fw.bean.ExecutionReportBean;
import com.fw.bean.ProgressReportBean;
import com.fw.domain.DashboardGraph;
import com.fw.exceptions.APIExceptions;

public interface IReportsService {

//	AutomationReportBean getAutomationReport(int clientProjectI,
//			String tagValue, long moduleId, String applicable)
//			throws APIExceptions;

	ExecutionReportBean getExecutionReport(int clientProjectId, int moduleId,
			int releaseId, int environmentId, String applicable)
			throws APIExceptions;

	String downloadClientReport(int clientProjectId, int releaseId,
			int environmentId, String format, String path, String applicable)
			throws APIExceptions;

	String downloadManualExecutionTemplate(String format, String fileName,
			String filePath, String testCaseIds) throws APIExceptions;

	String downloadTestCases(String format, String fileName, String filePath,
			String testCaseIds) throws APIExceptions;

	List<DashboardGraph> getDashboardGraphReport() throws APIExceptions;

	String getTestCasesOfSpecificActivityStatus(int clientProjectId,
			int moduleId, String isApplicable, String isDeleted,
			String fileFormat, String filePath, String fileName)
			throws APIExceptions;

	AutomationReportBean getAutomationReport(int clientProjectId,
			String tagValue, long moduleId, String applicable, String startDate,
			String endDate) throws APIExceptions;

	ProgressReportBean getAutomationProgress(int clientProjectId,
			String applicable, String startDate, String endDate)
			throws APIExceptions;
}
