package com.fw.dao.impl;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.fw.dao.ITestCaseManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.TestCase;
import com.fw.domain.TestCaseVersion;
import com.fw.exceptions.APIExceptions;
import com.fw.utils.ApplicationCommonUtil;
import com.fw.utils.LocalUtils;
import com.fw.utils.ValueValidations;

@Repository
public class TestCaseManagerImpl implements ITestCaseManager {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	private Logger log = Logger.getLogger(TestCaseManagerImpl.class);

	@Override
	public TestCase persistTestCase(TestCase testCase) throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO testCase (testcase_no, test_data, module_id, "
					+ "test_summary, precondition, tags, execution_steps, "
					+ "expected_result, isautomatable, remarks, file_name, "
					+ "automated_textcase_no_from_file, manual_reason,"
					+ "applicable, created_by,  modified_by, hashcode) "
					+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			final String currentUser = applicationCommonUtil.getCurrentUser();

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "testcase_id" });
					ps.setString(1, testCase.getTestCaseNo());
					ps.setString(2, testCase.getTestData());
					ps.setLong(3, testCase.getModuleId());
					ps.setString(4, testCase.getTestSummary());
					ps.setString(5, testCase.getPreCondition());

					String tags = testCase.getTags() == null ? null
							: String.join(",", testCase.getTags());
					ps.setString(6, tags);
					ps.setString(7, testCase.getExecutionSteps());
					ps.setString(8, testCase.getExpectedResult());
					ps.setBoolean(9, testCase.isAutomatable());
					ps.setString(10, testCase.getRemarks());
					ps.setString(11, testCase.getFileName());
					ps.setString(12, testCase.getAutomatedTestCaseNoFromFile());
					ps.setString(13, testCase.getManualReason());
					ps.setBoolean(14, testCase.isApplicable());
					ps.setString(15, currentUser);
					ps.setString(16, currentUser);
					ps.setString(17, testCase.getHashCode());
					return ps;
				}
			}, requestKeyHolder);
			int logEntityId = requestKeyHolder.getKey().intValue();
			testCase.setTestCaseId(logEntityId);
			return testCase;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions(LocalUtils
					.getStringLocale("fw_test_mgmt_locale", "TestCaseImport"));
		}
	}

	@Override
	public int updateTestCaseById(TestCase testCase) throws APIExceptions {
		updateDataSource();
		String tags = testCase.getTags() == null ? null
				: String.join(",", testCase.getTags());
		String sql = "UPDATE testcase SET testcase_no=?, test_data=?, module_id=?, "
				+ "test_summary=?, precondition=?, tags=?, execution_steps=?, "
				+ "expected_result=?, isautomatable=?, remarks=?, file_name=?, "
				+ "automated_textcase_no_from_file=?, manual_reason=?, applicable=?, "
				+ "modified_by=?, hashcode=?, is_deleted=? WHERE testcase_id=?";
		try {
			return jdbcTemplate.update(sql, testCase.getTestCaseNo(),
					testCase.getTestData(), testCase.getModuleId(),
					testCase.getTestSummary(), testCase.getPreCondition(), tags,
					testCase.getExecutionSteps(), testCase.getExpectedResult(),
					testCase.isAutomatable(), testCase.getRemarks(),
					testCase.getFileName(),
					testCase.getAutomatedTestCaseNoFromFile(),
					testCase.getManualReason(), testCase.isApplicable(),
					applicationCommonUtil.getCurrentUser(),
					testCase.getHashCode(), testCase.isDeleted(),
					testCase.getTestCaseId());
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if (message.toLowerCase()
					.contains("violates not-null constraint")) {
				message = "One or more required data for update in not provided.";
			}
			throw new APIExceptions(
					"Error : Update test case cannot be done : " + message);
		}
	}

	@Override
	public void deleteTestCaseById(int testCaseId) throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM testCase WHERE testCase_id=?";
		try {
			jdbcTemplate.update(sql, testCaseId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
		}

	}

	@SuppressWarnings("serial")
	private static final Map<String, String> columns = new HashMap<String, String>() {
		{
			put("Test Case No.", "testcase_id");
			put("Test Case Ref No.", "testcase_no");
			put("Test Data", "test_data");
			put("Module", "module_id");
			put("Test Case Summary", "test_summary");
			put("Pre Condition", "precondition");
			put("Tags", "tags");
			put("Execution Steps", "execution_steps");
			put("Expected Result", "expected_result");
			put("Automatable", "isautomatable");
			put("Remarks", "remarks");
			put("File Name", "file_name");
			put("Automated Test Case No.", "automated_textcase_no_from_file");
			put("Manual Reason", "manual_reason");
			put("Applicable", "applicable");
			put("Created By", "created_by");
			put("Modified By", "modified_by");
			put("Created Date", "created_date");
			put("Modified Date", "modified_date");
			put("Deleted", "is_deleted");
		}
	};

	@Override
	public List<TestCase> getAllTestCases(int clientProjectId, int releaseId,
			String moduleIds, String tagValue, String applicable,
			String testCaseIds, String searchTxt, String sortByColumn,
			String ascOrDesc, int limit, int pageNumber, String startDate,
			String endDate, String isDeleted) throws APIExceptions {
		updateDataSource();
		String qry = getTestCasesQuery(clientProjectId, releaseId, moduleIds,
				tagValue, applicable, testCaseIds, searchTxt, sortByColumn,
				ascOrDesc, limit, pageNumber, startDate, endDate, isDeleted);
		if (!ValueValidations.isValueValid(qry)) {
			return new ArrayList<TestCase>();
		}
		try {
			return jdbcTemplate.query(qry, new RowMapper<TestCase>() {
				@Override
				public TestCase mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					return readRS(rs);
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new APIExceptions("Error : Some error occured while fetching "
					+ "the test case information for given filters : "
					+ e.getMessage());
		}
	}

	private String getTestCasesQuery(int clientProjectId, int releaseId,
			String moduleIds, String tagValue, String applicable,
			String testCaseIds, String searchTxt, String sortByColumn,
			String ascOrDesc, int limit, int pageNumber, String startDate,
			String endDate, String isDeleted) throws APIExceptions {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT v.version_id, t1.* FROM testCase AS t1 INNER JOIN modules AS m1 ON t1.module_id=m1.module_id INNER JOIN "
						+ "client_projects AS p1 ON m1.client_project_id = p1.client_project_id ");
		if (releaseId > 0) {
			sql.append(
					" INNER JOIN release_testcase rt ON t1.testcase_id=rt.testcase_id");
		}
		sql.append(
				" LEFT JOIN testcase_version v ON t1.hashcode=v.hashcode AND v.version_id=(SELECT MAX(version_id) FROM fw_test_mgmt.testcase_version WHERE hashcode= t1.hashcode) ");
		sql.append(" WHERE ");
		// adding filter condition for test case page
		if (ValueValidations.isValueValid(searchTxt)) {
			sql.append(" (CAST(t1.testcase_id AS text) LIKE '%" + searchTxt
					+ "%' OR t1.testcase_no ILIKE '%" + searchTxt
					+ "%' OR t1.test_data ILIKE '%" + searchTxt
					+ "%' OR m1.name ILIKE '%" + searchTxt
					+ "%' OR t1.test_summary ILIKE '%" + searchTxt
					+ "%' OR t1.precondition ILIKE '%" + searchTxt
					+ "%' OR t1.tags ILIKE '%" + searchTxt
					+ "%' OR t1.execution_steps ILIKE '%" + searchTxt
					+ "%' OR t1.expected_result ILIKE '%" + searchTxt
					+ "%' OR t1.remarks ILIKE '%" + searchTxt
					+ "%' OR t1.file_name ILIKE '%" + searchTxt
					+ "%' OR t1.automated_textcase_no_from_file ILIKE '%"
					+ searchTxt + "%' OR t1.manual_reason ILIKE '%" + searchTxt
					+ "%' OR t1.created_by ILIKE '%" + searchTxt
					+ "%' OR t1.modified_by ILIKE '%" + searchTxt
					+ "%' OR CAST(t1.created_date AS text) LIKE '%" + searchTxt
					+ "%' OR CAST(t1.modified_date AS text) LIKE '%" + searchTxt
					+ "%') AND ");
		}

		if (clientProjectId > 0) {
			sql.append(" p1.client_project_id=" + clientProjectId);
		} else {
			if (ValueValidations.isValueValid(
					applicationCommonUtil.getAssignedProjectIds())) {
				sql.append(" p1.client_project_id IN ("
						+ applicationCommonUtil.getAssignedProjectIds() + ")");
			} else {
				return null;
			}
		}

		if (releaseId > 0) {
			sql.append(" AND rt.release_id=" + releaseId);
		}

		if (ValueValidations.isValueValid(moduleIds)
				&& !moduleIds.equals("0")) {
			sql.append(" AND t1.module_id IN (" + moduleIds + ")");
		}

		if (ValueValidations.isValueValid(tagValue)) {
			sql.append(" AND t1.tags ILIKE '%" + tagValue + "%'");
		}

		if (ValueValidations.isValueValid(applicable)
				&& !applicable.trim().equalsIgnoreCase("all")) {
			sql.append(" AND t1.applicable=" + Boolean.valueOf(applicable));
		}

		if (ValueValidations.isValueValid(isDeleted)
				&& !isDeleted.trim().equalsIgnoreCase("all")) {
			sql.append(" AND t1.is_deleted=" + Boolean.valueOf(isDeleted));
		}

		if (ValueValidations.isValueValid(testCaseIds)
				&& !testCaseIds.equals("0")) {
			sql.append(" AND t1.testcase_id IN (" + testCaseIds + ")");
		}

		if (ValueValidations.isValueValid(startDate)) {
			sql.append(" AND t1.created_date >='" + startDate + "'");
		}

		if (ValueValidations.isValueValid(endDate)) {
			sql.append(" AND t1.created_date <='" + endDate + "'");
		}

//		sql.append(" AND t1.is_deleted=false");

		// Added for server side sorting and pagination
		if (ValueValidations.isValueValid(sortByColumn)
				&& ValueValidations.isValueValid(ascOrDesc)) {
			sql.append(" ORDER BY t1." + columns.get(sortByColumn) + " "
					+ ascOrDesc);
		} else {
			sql.append(" ORDER BY t1.created_date DESC, t1.testcase_id ASC");
		}

		if (limit > 0 && pageNumber >= 0) {
			sql.append(" LIMIT " + limit + " OFFSET " + pageNumber * limit);
		}

		return sql.toString();
	}

	// Added to get the test cases version info. The separate method is created
	// to avoid extra test case info fetching from result set.
	@Override
	public List<Integer> getAllTestCasesIds(int clientProjectId, int releaseId,
			String moduleIds, String tagValue, String applicable,
			String testCaseIds, String searchTxt, String sortByColumn,
			String ascOrDesc, int limit, int pageNumber, String startDate,
			String endDate, String isDeleted) throws APIExceptions {
		updateDataSource();
		String qry = getTestCasesQuery(clientProjectId, releaseId, moduleIds,
				tagValue, applicable, testCaseIds, searchTxt, sortByColumn,
				ascOrDesc, limit, pageNumber, startDate, endDate, isDeleted);
		if (ValueValidations.isValueValid(qry)) {
			return new ArrayList<Integer>();
		}
		try {
			return jdbcTemplate.query(qry, new RowMapper<Integer>() {
				@Override
				public Integer mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					return rs.getInt("testcase_id");
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new APIExceptions("Error : Some error occured while fetching "
					+ "the test case information for given filters : "
					+ e.getMessage());
		}
	}

	// This is added for server side pagination
	@Override
	public int getTestCasesCount(int clientProjectId, String applicable,
			String searchTxt) throws APIExceptions {
		updateDataSource();
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT count(testcase_id) FROM testCase AS t1, modules AS m1, "
						+ "client_projects AS p1 WHERE ");

		// adding filter condition for test case page
		if (!(null == searchTxt || searchTxt.trim().equals("")
				|| searchTxt.trim().toLowerCase().equals("null"))) {
			sql.append(" (CAST(t1.testcase_id AS text) LIKE '%" + searchTxt
					+ "%' OR t1.testcase_no ILIKE '%" + searchTxt
					+ "%' OR t1.test_data ILIKE '%" + searchTxt
					+ "%' OR m1.name ILIKE '%" + searchTxt
					+ "%' OR t1.test_summary ILIKE '%" + searchTxt
					+ "%' OR t1.precondition ILIKE '%" + searchTxt
					+ "%' OR t1.tags ILIKE '%" + searchTxt
					+ "%' OR t1.execution_steps ILIKE '%" + searchTxt
					+ "%' OR t1.expected_result ILIKE '%" + searchTxt
					+ "%' OR t1.remarks ILIKE '%" + searchTxt
					+ "%' OR t1.file_name ILIKE '%" + searchTxt
					+ "%' OR t1.automated_textcase_no_from_file ILIKE '%"
					+ searchTxt + "%' OR t1.manual_reason ILIKE '%" + searchTxt
					+ "%' OR t1.created_by ILIKE '%" + searchTxt
					+ "%' OR t1.modified_by ILIKE '%" + searchTxt
					+ "%' OR CAST(t1.created_date AS text) LIKE '%" + searchTxt
					+ "%' OR CAST(t1.modified_date AS text) LIKE '%" + searchTxt
					+ "%') AND ");
		}

		sql.append(" t1.module_id=m1.module_id "
				+ "AND m1.client_project_id = p1.client_project_id");

		if (clientProjectId > 0) {
			sql.append(" AND p1.client_project_id=" + clientProjectId);
		} else {
			if (ValueValidations.isValueValid(
					applicationCommonUtil.getAssignedProjectIds())) {
				sql.append(" AND p1.client_project_id IN ("
						+ applicationCommonUtil.getAssignedProjectIds() + ")");
			} else {
				return 0;
			}
		}

		if (!(null == applicable || applicable.trim().equals("")
				|| applicable.trim().toLowerCase().equals("all"))) {
			sql.append(" AND t1.applicable=" + Boolean.valueOf(applicable));
		}

		sql.append(" AND t1.is_deleted=false");

		try {
			return jdbcTemplate.queryForObject(sql.toString(),
					new RowMapper<Integer>() {
						@Override
						public Integer mapRow(ResultSet rs, int rownumber)
								throws SQLException {
							return rs.getInt(1);
						}
					});
		} catch (EmptyResultDataAccessException e) {
			log.info(e.getMessage());
			return 0;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return 0;
		}
	}

	@Override
	public TestCase getTestCaseById(int testCaseId, String isDeleted)
			throws APIExceptions {
		try {
			updateDataSource();
			String query = "SELECT * FROM testCase t LEFT JOIN testcase_version v "
					+ "ON t.testcase_id=v.testcase_id AND v.version_id="
					+ "(SELECT MAX(version_id) FROM testcase_version "
					+ "WHERE testcase_id=t.testcase_id) WHERE t.testCase_id=? ";
			if (!(!ValueValidations.isValueValid(isDeleted)
					|| isDeleted.equalsIgnoreCase("all"))) {
				query += "AND t.is_deleted=" + Boolean.valueOf(isDeleted);
			}
			return jdbcTemplate.queryForObject(query,
					new RowMapper<TestCase>() {
						@Override
						public TestCase mapRow(ResultSet rs, int rownumber)
								throws SQLException {
							return readRS(rs);
						}
					}, testCaseId);
		} catch (EmptyResultDataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions("Error : Invalid test case id ["
					+ testCaseId + "] is given as not test case is available");
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public List<TestCase> getTestCaseByModuleId(long moduleId)
			throws APIExceptions {
		updateDataSource();
		String sql = "SELECT * FROM testCase t LEFT JOIN testcase_version v "
				+ "ON t.testcase_id=v.testcase_id AND v.version_id="
				+ "(SELECT MAX(version_id) FROM testcase_version "
				+ "WHERE testcase_id=t.testcase_id) WHERE t.module_id=? "
				+ "AND t.is_deleted=false";
		try {
			return jdbcTemplate.query(sql, new RowMapper<TestCase>() {
				@Override
				public TestCase mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					return readRS(rs);
				}
			}, moduleId);
		} catch (EmptyResultDataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions("Error : Invalid module id [" + moduleId
					+ "] is given as not test case is available");
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public TestCase getTestCaseByHash(String hash, boolean isAllRecordsRequired,
			boolean isDeleted) throws APIExceptions {
		updateDataSource();
		try {
			String query = "SELECT * FROM testCase t LEFT JOIN testcase_version v "
					+ "ON t.testcase_id=v.testcase_id AND v.version_id=(SELECT "
					+ "MAX(version_id) FROM testcase_version WHERE "
					+ "testcase_id=t.testcase_id) WHERE t.hashcode=?";
			if (!isAllRecordsRequired) {
				query += " AND t.is_deleted=" + isDeleted;
			}
			return jdbcTemplate.queryForObject(query,
					new RowMapper<TestCase>() {
						@Override
						public TestCase mapRow(ResultSet rs, int rownumber)
								throws SQLException {
							return readRS(rs);
						}
					}, hash);
		} catch (EmptyResultDataAccessException e) {
			log.info("No test case is available with given hash");
			return null;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	// Added for DB operations on all rows i.e. update hash code
	@Override
	public List<TestCase> getAllTestCases() throws APIExceptions {
		updateDataSource();
		try {
			String query = "SELECT * FROM testCase";
			return jdbcTemplate.query(query, new RowMapper<TestCase>() {
				@Override
				public TestCase mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					return readRS(rs);
				}
			});
		} catch (EmptyResultDataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions(
					"Error : No test case is available with given hash");
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	private TestCase readRS(ResultSet rs) throws SQLException {
		TestCase testCase = new TestCase();
		testCase.setTestCaseId(rs.getInt("testCase_id"));
		testCase.setTestCaseNo(rs.getString("testCase_no"));
		testCase.setTestData(rs.getString("test_data"));
		testCase.setModuleId(rs.getLong("module_id"));
		testCase.setTestSummary(rs.getString("test_summary"));
		testCase.setPreCondition(rs.getString("preCondition"));

		String tags = rs.getString("tags");
		List<String> tagList = Arrays.asList(tags);
		testCase.setTags(tagList);

		testCase.setExecutionSteps(rs.getString("execution_steps"));
		testCase.setExpectedResult(rs.getString("expected_result"));

		testCase.setAutomatable(rs.getBoolean("isAutomatable"));

		testCase.setRemarks(rs.getString("remarks"));
		testCase.setFileName(rs.getString("file_name"));
		testCase.setAutomatedTestCaseNoFromFile(
				rs.getString("automated_textCase_no_from_file"));
		testCase.setManualReason(rs.getString("manual_reason") == null ? ""
				: rs.getString("manual_reason"));
		testCase.setApplicable(rs.getBoolean("applicable"));
		testCase.setCreatedBy(rs.getString("created_by"));
		testCase.setModifiedBy(rs.getString("modified_by"));
		testCase.setCreatedDate(rs.getTimestamp("created_date"));
		testCase.setModifiedDate(rs.getTimestamp("modified_date"));
		testCase.setDeleted(rs.getBoolean("is_deleted"));
		testCase.setHashCode(rs.getString("hashcode"));

		testCase.setLatestVersion("V"
				+ (rs.getInt("version_id") == 0 ? 1 : rs.getInt("version_id")));

		return testCase;
	}

	@Override
	public Map<String, Integer> getTestCaseHashCode(boolean isAllRequired,
			boolean isDeleted, boolean applicable) throws APIExceptions {
		updateDataSource();
		String sql = "SELECT hashcode,testcase_id FROM testCase";
		if (!isAllRequired) {
			sql += " WHERE is_deleted=" + String.valueOf(isDeleted);
			if (!isDeleted) {
				sql += " AND applicable=" + String.valueOf(applicable);
			}
		}

		return jdbcTemplate.query(sql,
				new ResultSetExtractor<Map<String, Integer>>() {
					public Map<String, Integer> extractData(ResultSet rs)
							throws SQLException {
						Map<String, Integer> map = new LinkedHashMap<String, Integer>();
						while (rs.next()) {
							map.put(rs.getString("hashcode"),
									rs.getInt("testcase_id"));
						}
						return map;
					};
				});
	}

	@Override
	public List<String> getProjectTestCaseTagsDetails(int clientProjectId)
			throws APIExceptions {
		updateDataSource();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT(t1.tags) FROM testCase t1,modules m1, "
				+ "client_projects p1 WHERE t1.applicable = true "
				+ "AND t1.is_deleted=false " + "AND t1.module_id=m1.module_id "
				+ "AND m1.client_project_id = p1.client_project_id");

		if (clientProjectId > 0) {
			sql.append(" AND p1.client_project_id=" + clientProjectId);
		} else {
			if (null != applicationCommonUtil.getAssignedProjectIds()) {
				sql.append(" AND p1.client_project_id IN ("
						+ applicationCommonUtil.getAssignedProjectIds() + ")");
			}
		}

		return jdbcTemplate.query(sql.toString(), new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				String tags = null;
				tags = rs.getString("tags");
				return tags;
			}
		});
	}

	@Override
	public List<TestCaseVersion> getTestCaseVersion(int testCaseId)
			throws APIExceptions {
		updateDataSource();
		String sql = "SELECT * FROM testcase_version WHERE testcase_id=? "
				+ "ORDER BY version_id DESC";
		try {
			return jdbcTemplate.query(sql, new RowMapper<TestCaseVersion>() {
				@Override
				public TestCaseVersion mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					TestCaseVersion testCaseVersion = new TestCaseVersion();
					testCaseVersion.setTestCaseVersionId(
							rs.getInt("testcase_version_id"));
					testCaseVersion.setTestCaseId(rs.getInt("testCase_id"));
					testCaseVersion.setTestCaseNo(rs.getString("testCase_no"));
					testCaseVersion.setTestData(rs.getString("test_data"));
					testCaseVersion.setModuleId(rs.getLong("module_id"));
					testCaseVersion
							.setTestSummary(rs.getString("test_summary"));
					testCaseVersion
							.setPreCondition(rs.getString("preCondition"));

					String tags = rs.getString("tags");
					List<String> tagList = Arrays.asList(tags);
					testCaseVersion.setTags(tagList);

					testCaseVersion
							.setExecutionSteps(rs.getString("execution_steps"));
					testCaseVersion
							.setExpectedResult(rs.getString("expected_result"));

					testCaseVersion
							.setAutomatable(rs.getBoolean("isAutomatable"));

					testCaseVersion.setRemarks(rs.getString("remarks"));
					testCaseVersion.setFileName(rs.getString("file_name"));
					testCaseVersion.setAutomatedTestCaseNoFromFile(
							rs.getString("automated_textCase_no_from_file"));
					testCaseVersion
							.setManualReason(rs.getString("manual_reason"));
					testCaseVersion.setApplicable(rs.getBoolean("applicable"));
					testCaseVersion.setCreatedBy(rs.getString("created_by"));
					testCaseVersion.setModifiedBy(rs.getString("modified_by"));
					testCaseVersion
							.setCreatedDate(rs.getTimestamp("created_date"));
					testCaseVersion
							.setModifiedDate(rs.getTimestamp("modified_date"));
					testCaseVersion.setDeleted(rs.getBoolean("is_deleted"));
					testCaseVersion.setHashCode(rs.getString("hashcode"));
					testCaseVersion
							.setTestCaseVersion("V" + rs.getInt("version_id"));
					return testCaseVersion;
				}
			}, testCaseId);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new APIExceptions("Error : Some error occured while fetching "
					+ "the test case information for given filters : "
					+ e.getMessage());
		}
	}

	@Override
	public TestCaseVersion getTestCaseVersion(int testCaseId, int versionId)
			throws APIExceptions {
		updateDataSource();
		String sql = "SELECT * FROM testcase_version WHERE testcase_id=? "
				+ "AND version_id=? ORDER BY version_id DESC";
		try {
			return jdbcTemplate.queryForObject(sql,
					new RowMapper<TestCaseVersion>() {
						@Override
						public TestCaseVersion mapRow(ResultSet rs,
								int rownumber) throws SQLException {
							TestCaseVersion testCaseVersion = new TestCaseVersion();
							testCaseVersion.setTestCaseVersionId(
									rs.getInt("testcase_version_id"));
							testCaseVersion
									.setTestCaseId(rs.getInt("testCase_id"));
							testCaseVersion
									.setTestCaseNo(rs.getString("testCase_no"));
							testCaseVersion
									.setTestData(rs.getString("test_data"));
							testCaseVersion
									.setModuleId(rs.getLong("module_id"));
							testCaseVersion.setTestSummary(
									rs.getString("test_summary"));
							testCaseVersion.setPreCondition(
									rs.getString("preCondition"));

							String tags = rs.getString("tags");
							List<String> tagList = Arrays.asList(tags);
							testCaseVersion.setTags(tagList);

							testCaseVersion.setExecutionSteps(
									rs.getString("execution_steps"));
							testCaseVersion.setExpectedResult(
									rs.getString("expected_result"));

							testCaseVersion.setAutomatable(
									rs.getBoolean("isAutomatable"));

							testCaseVersion.setRemarks(rs.getString("remarks"));
							testCaseVersion
									.setFileName(rs.getString("file_name"));
							testCaseVersion.setAutomatedTestCaseNoFromFile(
									rs.getString(
											"automated_textCase_no_from_file"));
							testCaseVersion.setManualReason(
									rs.getString("manual_reason"));
							testCaseVersion
									.setApplicable(rs.getBoolean("applicable"));
							testCaseVersion
									.setCreatedBy(rs.getString("created_by"));
							testCaseVersion
									.setModifiedBy(rs.getString("modified_by"));
							testCaseVersion.setCreatedDate(
									rs.getTimestamp("created_date"));
							testCaseVersion.setModifiedDate(
									rs.getTimestamp("modified_date"));
							testCaseVersion
									.setDeleted(rs.getBoolean("is_deleted"));
							testCaseVersion
									.setHashCode(rs.getString("hashcode"));
							testCaseVersion.setTestCaseVersion(
									"V" + rs.getInt("version_id"));
							return testCaseVersion;
						}
					}, new Object[] { testCaseId, versionId });
		} catch (EmptyResultDataAccessException ex) {
			log.info("Data not available : " + ex.getMessage());
			return null;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new APIExceptions("Error : Some error occured while fetching "
					+ "the test case information for given filters : "
					+ e.getMessage());
		}
	}

	@Override
	public Map<Integer, List<TestCaseVersion>> getMultipleTestCasesVersionMap(
			String testCaseIds) throws APIExceptions {
		updateDataSource();
		String sql = "SELECT * FROM testcase_version WHERE testcase_id IN ("
				+ testCaseIds + ") " + "ORDER BY version_id DESC";
		try {
			return jdbcTemplate.query(sql,
					new ResultSetExtractor<Map<Integer, List<TestCaseVersion>>>() {
						@Override
						public Map<Integer, List<TestCaseVersion>> extractData(
								ResultSet rs) throws SQLException {
							Map<Integer, List<TestCaseVersion>> result = new LinkedHashMap<Integer, List<TestCaseVersion>>();
							while (rs.next()) {
								TestCaseVersion testCaseVersion = new TestCaseVersion();
								testCaseVersion.setTestCaseVersionId(
										rs.getInt("testcase_version_id"));
								testCaseVersion.setTestCaseId(
										rs.getInt("testCase_id"));
								testCaseVersion.setTestCaseNo(
										rs.getString("testCase_no"));
								testCaseVersion
										.setTestData(rs.getString("test_data"));
								testCaseVersion
										.setModuleId(rs.getLong("module_id"));
								testCaseVersion.setTestSummary(
										rs.getString("test_summary"));
								testCaseVersion.setPreCondition(
										rs.getString("preCondition"));

								String tags = rs.getString("tags");
								List<String> tagList = Arrays.asList(tags);
								testCaseVersion.setTags(tagList);

								testCaseVersion.setExecutionSteps(
										rs.getString("execution_steps"));
								testCaseVersion.setExpectedResult(
										rs.getString("expected_result"));

								testCaseVersion.setAutomatable(
										rs.getBoolean("isAutomatable"));

								testCaseVersion
										.setRemarks(rs.getString("remarks"));
								testCaseVersion
										.setFileName(rs.getString("file_name"));
								testCaseVersion.setAutomatedTestCaseNoFromFile(
										rs.getString(
												"automated_textCase_no_from_file"));
								testCaseVersion.setManualReason(
										rs.getString("manual_reason"));
								testCaseVersion.setApplicable(
										rs.getBoolean("applicable"));
								testCaseVersion.setCreatedBy(
										rs.getString("created_by"));
								testCaseVersion.setModifiedBy(
										rs.getString("modified_by"));
								testCaseVersion.setCreatedDate(
										rs.getTimestamp("created_date"));
								testCaseVersion.setModifiedDate(
										rs.getTimestamp("modified_date"));
								testCaseVersion.setDeleted(
										rs.getBoolean("is_deleted"));
								testCaseVersion
										.setHashCode(rs.getString("hashcode"));
								testCaseVersion.setTestCaseVersion(
										"V" + rs.getInt("version_id"));
								List<TestCaseVersion> list = null;
								if (null == result
										.get(rs.getInt("testCase_id"))) {
									list = new ArrayList<TestCaseVersion>();
								} else {
									list = result.get(rs.getInt("testCase_id"));
								}
								list.add(testCaseVersion);
								result.put(rs.getInt("testCase_id"), list);
							}
							return result;
						}
					});
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new APIExceptions("Error : Some error occured while fetching "
					+ "the test case information for given filters : "
					+ e.getMessage());
		}
	}

	@Override
	public List<Integer> getDeletedTestCaseIdsForGivenTime(int clientProjectId,
			String startDate, String endDate) throws APIExceptions {
		updateDataSource();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT testcase_id FROM testcase t, modules m "
				+ "WHERE t.modified_date>='" + startDate
				+ "' AND t.modified_date<='" + endDate
				+ "' AND t.is_deleted=true AND t.module_id=m.module_id "
				+ "AND m.client_project_id=" + clientProjectId);

		return jdbcTemplate.query(sql.toString(), new RowMapper<Integer>() {
			@Override
			public Integer mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				return rs.getInt("testcase_id");
			}
		});
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
