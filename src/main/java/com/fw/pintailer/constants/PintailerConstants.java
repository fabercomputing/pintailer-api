/**
 * 
 */
package com.fw.pintailer.constants;

/**
 * @author faber
 *
 */
public final class PintailerConstants {

	// 1 Day
	public static final long ACCESS_TOKEN_VALIDITY_SECONDS = (24 * 60 * 60);

	public static final String SIGNING_KEY = "fwTestManagement#faberwork";
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String LANGUAGE_HEADER_STRING = "fwTestManagement_locale";
	public static final String USER_DETAIL_HEADER_STRING = "user-detail";

	public static final String DEFAULT_DB_NAME = "fw_test_mgmt";
	public static final String DEFAULT_DB_USERNAME = "postgres";
	public static final String DEFAULT_DB_PASSWORD = "postgres";

	public static final String COMMON_ORG = "COMMON";

	public static final String AUTOMATIC_TEST_CASE_FROM_FEATURE_FILE_IDENTIFIER = "TC";
	public static final String READ_FILE_OBJECT_SEPARATOR = "&@@&@&";
	public static final String IMPORT_AUTOMATED_FILE_LINE_COMMENT_SYMBOL = "#";
	public static final String MODULE_HIERARCHY_SEPARATOR = "##";
	public static final String HELP_TOPICS_HIERARCHY_SEPARATOR = "##";
	public static final String GENERIC_SEPARATOR = "@@&@@";

	public static final String USER_DETAILS_SEPARATOR = "#";

	// name of columns in the CSV file used for importing the test cases
	public static final String IMPORT_CSV_COLUMN_SERIAL_NO = "Sr. No.";
	public static final String IMPORT_CSV_COLUMN_TC_ID_REF = "TC ID Ref.";
	public static final String IMPORT_CSV_COLUMN_MODULE_NAME = "Module Name";
	public static final String IMPORT_CSV_COLUMN_FUNCTIONALITY = "Functionality";
	public static final String IMPORT_CSV_COLUMN_SUB_FUNCTIONALITY = "Sub Functionality";
	public static final String IMPORT_CSV_COLUMN_TEST_SUMMARY = "Test Summary";
	public static final String IMPORT_CSV_COLUMN_PRE_CONDITION = "Pre condition";
	public static final String IMPORT_CSV_COLUMN_EXECUTION_STEPS = "Execution Steps";
	public static final String IMPORT_CSV_COLUMN_EXPECTED_RESULT = "Expected Result";
	public static final String IMPORT_CSV_COLUMN_ACTUAL_RESULT = "Actual Result";
	public static final String IMPORT_CSV_COLUMN_TESTER = "Tester";
	public static final String IMPORT_CSV_COLUMN_EXECUTION_DATE = "Execution Date";
	public static final String IMPORT_CSV_COLUMN_TEST_RESULTS = "Test Results";
	public static final String IMPORT_CSV_COLUMN_TEST_DATA = "Test Data";
	public static final String IMPORT_CSV_COLUMN_LINKED_DEFECTS = "Linked Defect";
	public static final String IMPORT_CSV_COLUMN_ENVIRONMENT = "Environment";
	public static final String IMPORT_CSV_COLUMN_CRITICALITY = "Criticality";
	public static final String IMPORT_CSV_COLUMN_IS_AUTOMATABLE = "IsAutomatable";
	public static final String IMPORT_CSV_COLUMN_IS_AUTOMATED = "IsAutomated";
	public static final String IMPORT_CSV_COLUMN_REMARKS = "Remarks";
	public static final String IMPORT_CSV_COLUMN_FILE_NAME = "File Name";
	public static final String IMPORT_CSV_COLUMN_TEST_CASE_NO = "Test Case No.";
	public static final String IMPORT_CSV_COLUMN_APPLICABLE = "Applicable";
	public static final String IMPORT_CSV_COLUMN_CREATION_DATE = "Creation Date";

	// name of additional columns in the CSV file used to import the execution
	// details of the manual/non-executed test cases
	public static final String IMPORT_EXECUTION_CSV_COLUMN_ADD_EXECUTION_DETAILS = "Add Execution Details";
	public static final String IMPORT_EXECUTION_CSV_COLUMN_EXECUTION_DATE = "Execution Date";
	public static final String IMPORT_EXECUTION_CSV_COLUMN_EXECUTION_DATE_FORMAT = "yyyy-MM-dd hh:mm";
	public static final String IMPORT_EXECUTION_CSV_COLUMN_DURATION_IN_SECONDS = "Duration (Sec)";
	public static final String IMPORT_EXECUTION_CSV_COLUMN_TEST_CASE_ID = "Test Case ID";
	public static final String IMPORT_EXECUTION_CSV_COLUMN_TEST_STEP_DEFINITION = "Test Step Definition";

	// name of the columns used to generated the execution report which is
	// shared with client (Gridpoint)
	public static final String DOWNLOAD_EXECUTION_REPORT_COLUMN_SECTION = "Section";
	public static final String DOWNLOAD_EXECUTION_REPORT_COLUMN_TOTAL = "Total";
	public static final String DOWNLOAD_EXECUTION_REPORT_COLUMN_EXECUTED = "Executed";
	public static final String DOWNLOAD_EXECUTION_REPORT_COLUMN_PASSED = "Passed";
	public static final String DOWNLOAD_EXECUTION_REPORT_COLUMN_FAILED = "Failed";
	public static final String DOWNLOAD_EXECUTION_REPORT_COLUMN_PENDING = "Pending";
	public static final String DOWNLOAD_EXECUTION_REPORT_COLUMN_BLOCKED = "Blocked";
	public static final String DOWNLOAD_EXECUTION_REPORT_COLUMN_BLOCKER_BUGS = "Blocker Bugs";
	public static final String DOWNLOAD_EXECUTION_REPORT_COLUMN_STATUS = "Status";
	public static final String DOWNLOAD_EXECUTION_REPORT_COLUMN_BACKLOG = "Backlog";
	public static final String DOWNLOAD_EXECUTION_REPORT_COLUMN_LINKED_BUGS = "Linked Bugs";

	// status of the module based on the status of its test cases. This detail
	// is added in the execution report which is
	// shared with client (Gridpoint)
	public static final String DOWNLOAD_EXECUTION_REPORT_STATUS_COMPLETED = "Completed";
	public static final String DOWNLOAD_EXECUTION_REPORT_STATUS_PENDING = "Pending";
	public static final String DOWNLOAD_EXECUTION_REPORT_STATUS_IN_PROCESS = "In Process";
	public static final String DOWNLOAD_EXECUTION_REPORT_STATUS_NOT_DETERMINED = "Not Determined";

	public static final int MODULE_HIERARCHY_LEVEL_COUNT = 3;

	public static final String[] IMPORT_TC_CSV_HEADERS = {
			IMPORT_CSV_COLUMN_SERIAL_NO, IMPORT_CSV_COLUMN_TC_ID_REF,
			IMPORT_CSV_COLUMN_MODULE_NAME, IMPORT_CSV_COLUMN_FUNCTIONALITY,
			IMPORT_CSV_COLUMN_SUB_FUNCTIONALITY, IMPORT_CSV_COLUMN_TEST_SUMMARY,
			IMPORT_CSV_COLUMN_PRE_CONDITION, IMPORT_CSV_COLUMN_EXECUTION_STEPS,
			IMPORT_CSV_COLUMN_EXPECTED_RESULT, IMPORT_CSV_COLUMN_ACTUAL_RESULT,
			IMPORT_CSV_COLUMN_TESTER, IMPORT_CSV_COLUMN_EXECUTION_DATE,
			IMPORT_CSV_COLUMN_TEST_RESULTS, IMPORT_CSV_COLUMN_TEST_DATA,
			IMPORT_CSV_COLUMN_LINKED_DEFECTS, IMPORT_CSV_COLUMN_ENVIRONMENT,
			IMPORT_CSV_COLUMN_CRITICALITY, IMPORT_CSV_COLUMN_IS_AUTOMATABLE,
			IMPORT_CSV_COLUMN_IS_AUTOMATED, IMPORT_CSV_COLUMN_REMARKS,
			IMPORT_CSV_COLUMN_FILE_NAME, IMPORT_CSV_COLUMN_TEST_CASE_NO,
			IMPORT_CSV_COLUMN_APPLICABLE, IMPORT_CSV_COLUMN_CREATION_DATE };

	public static final String[] IMPORT_TC_CSV_HEADERS_NEW = {
			IMPORT_CSV_COLUMN_SERIAL_NO, IMPORT_CSV_COLUMN_TC_ID_REF,
			IMPORT_CSV_COLUMN_MODULE_NAME, IMPORT_CSV_COLUMN_FUNCTIONALITY,
			IMPORT_CSV_COLUMN_SUB_FUNCTIONALITY, IMPORT_CSV_COLUMN_TEST_SUMMARY,
			IMPORT_CSV_COLUMN_PRE_CONDITION, IMPORT_CSV_COLUMN_EXECUTION_STEPS,
			IMPORT_CSV_COLUMN_EXPECTED_RESULT, IMPORT_CSV_COLUMN_ACTUAL_RESULT,
			IMPORT_CSV_COLUMN_TESTER, IMPORT_CSV_COLUMN_EXECUTION_DATE,
			IMPORT_CSV_COLUMN_TEST_RESULTS, IMPORT_CSV_COLUMN_TEST_DATA,
			IMPORT_CSV_COLUMN_LINKED_DEFECTS, IMPORT_CSV_COLUMN_ENVIRONMENT,
			IMPORT_CSV_COLUMN_CRITICALITY, IMPORT_CSV_COLUMN_IS_AUTOMATABLE,
			IMPORT_CSV_COLUMN_IS_AUTOMATED, IMPORT_CSV_COLUMN_REMARKS,
			IMPORT_CSV_COLUMN_APPLICABLE, IMPORT_CSV_COLUMN_CREATION_DATE };

	public static final String[] IMPORT_EXECUTION_CSV_HEADERS = {
			IMPORT_CSV_COLUMN_SERIAL_NO, IMPORT_CSV_COLUMN_TC_ID_REF,
			IMPORT_CSV_COLUMN_MODULE_NAME, IMPORT_CSV_COLUMN_FUNCTIONALITY,
			IMPORT_CSV_COLUMN_SUB_FUNCTIONALITY, IMPORT_CSV_COLUMN_TEST_SUMMARY,
			IMPORT_CSV_COLUMN_PRE_CONDITION, IMPORT_CSV_COLUMN_EXECUTION_STEPS,
			IMPORT_CSV_COLUMN_EXPECTED_RESULT,
			IMPORT_EXECUTION_CSV_COLUMN_ADD_EXECUTION_DETAILS,
			IMPORT_EXECUTION_CSV_COLUMN_TEST_CASE_ID,
			IMPORT_EXECUTION_CSV_COLUMN_EXECUTION_DATE,
			IMPORT_CSV_COLUMN_TEST_RESULTS, IMPORT_CSV_COLUMN_REMARKS,
			IMPORT_CSV_COLUMN_LINKED_DEFECTS,
			IMPORT_EXECUTION_CSV_COLUMN_DURATION_IN_SECONDS,
			IMPORT_EXECUTION_CSV_COLUMN_TEST_STEP_DEFINITION };

	public static final String ENCODING_UTF_8 = "UTF-8";
	public static final String SUPPORT_EMAIL = "rishi.singhal@faberwork.com";

	public static final String GCAPTCHA = "6LdpLY4UAAAAAO5CP2pSANxR3V3DXTl_SkVG_f1Q";

	public static final String LOCALE_EN = "en-US";
	public static final String LOCALE_HI = "hi-IN";

	public static final String DEFAULT_TEST_CASE_TAG = "P3";
	public static final String DEFAULT_TEST_SCENARIO_TAG = "@P3";

	// used at FeatureManagementServiceImpl to update the test step sequence as
	// delete is removed from the code to maintain proper version
	public static final String STEP_SEQUENCE_UPDATE_CONDITION_LESSER = "lesser";
	public static final String STEP_SEQUENCE_UPDATE_CONDITION_EQUAL = "equal";
	public static final String STEP_SEQUENCE_UPDATE_CONDITION_GREATER = "greater";

	public static final String TEST_CASE_STEP_NO_MAPPING = "no_map";

	public static final String EMAIL_TYPE_CLIENT_STATUS = "client_status";
}
