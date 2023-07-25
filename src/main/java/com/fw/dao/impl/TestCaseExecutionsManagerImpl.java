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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.fw.dao.ILinkedTicketManager;
import com.fw.dao.ITestCaseExecutionsManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.LinkedTicket;
import com.fw.domain.TestCaseExecutions;
import com.fw.enums.TestResults;
import com.fw.exceptions.APIExceptions;
import com.fw.utils.ApplicationCommonUtil;

@Repository
public class TestCaseExecutionsManagerImpl
		implements ITestCaseExecutionsManager {

	private Logger log = Logger.getLogger(TestCaseExecutionsManagerImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	ILinkedTicketManager linkedTicketManagerImpl;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	public TestCaseExecutions persistTestCaseExecutions(
			TestCaseExecutions logEntity) throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO testcase_execution (testcase_id, test_step_id, "
					+ "test_runBy, execution_date, test_result, environment_id, "
					+ "actual_loe, created_by, modified_by, is_deleted, actual_result, release_id) "
					+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "testcase_execution_id" });
					ps.setLong(1, logEntity.getTestCaseId());
					ps.setLong(2, logEntity.getTestStepId());
					ps.setString(3, logEntity.getTestRunBy());
					ps.setTimestamp(4, new Timestamp(
							logEntity.getExecutionDate().getTime()));
					ps.setString(5, logEntity.getTestResult().toDbString());
					ps.setInt(6, logEntity.getEnvironmentId());
					ps.setInt(7, logEntity.getActualLOE());
					ps.setString(8, currentUser);
					ps.setString(9, currentUser);
					ps.setBoolean(10, logEntity.isDeleted());
					ps.setString(11, logEntity.getActualResult());
					ps.setInt(12, logEntity.getReleaseId());
					return ps;
				}
			}, requestKeyHolder);
			long logEntityId = requestKeyHolder.getKey().longValue();
			logEntity.setTestCaseExecutionsId(logEntityId);
			return logEntity;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public int persistTestCaseExecutionsInBatch(final List<Object[]> args)
			throws APIExceptions {
		updateDataSource();
		String sql = "INSERT INTO testcase_execution (testcase_id, test_step_id, "
				+ "test_runBy, execution_date, test_result, environment_id, "
				+ "actual_loe, created_by, modified_by, is_deleted, actual_result, "
				+ "release_id, step_keyword) "
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			int[] arr = jdbcTemplate.batchUpdate(sql, args);
			return arr.length;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return 0;
		}
	}

	@Override
	public List<TestCaseExecutions> getAllTestCaseExecutions()
			throws APIExceptions {
		updateDataSource();
		return jdbcTemplate.query("SELECT * FROM testCase_execution",
				new RowMapper<TestCaseExecutions>() {
					@Override
					public TestCaseExecutions mapRow(ResultSet rs,
							int rownumber) throws SQLException {
						TestCaseExecutions logEntity = new TestCaseExecutions();
						logEntity.setTestCaseExecutionsId(
								rs.getLong("testcase_execution_id"));
						logEntity.setTestCaseId(rs.getInt("testCase_id"));
						logEntity.setTestStepId(rs.getLong("test_step_id"));
						logEntity.setTestRunBy(rs.getString("test_runBy"));
						logEntity
								.setActualResult(rs.getString("actual_result"));
						logEntity.setExecutionDate(
								rs.getTimestamp("execution_date"));
						logEntity.setTestResult(TestResults
								.fromString(rs.getString("test_result")));
						logEntity.setEnvironmentId(rs.getInt("environment_id"));
						logEntity.setActualLOE(rs.getInt("actual_loe"));
						logEntity.setCreatedBy(rs.getString("created_by"));
						logEntity.setModifiedBy(rs.getString("modified_by"));
						logEntity.setCreatedDate(
								rs.getTimestamp("created_date"));
						logEntity.setModifiedDate(
								rs.getTimestamp("modified_date"));
						logEntity.setDeleted(rs.getBoolean("is_deleted"));
						logEntity.setReleaseId(rs.getInt("release_id"));
						return logEntity;
					}
				});
	}

	@Override
	public TestCaseExecutions getTestCaseExecutionsById(long executionId,
			int testCaseId, int releaseId, int environmentId)
			throws APIExceptions {
		updateDataSource();
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM testCase_execution e WHERE ");
		if (executionId <= 0) {
			List<TestCaseExecutions> latestTestCaseExecutions = getAllLatestTestCaseExecutions(
					releaseId, environmentId, testCaseId, null);
			if (null == latestTestCaseExecutions
					|| latestTestCaseExecutions.isEmpty()) {
				return null;
			} else {
				executionId = latestTestCaseExecutions.get(0)
						.getTestCaseExecutionsId();
			}
		}
		query.append(" e.testcase_execution_id=" + executionId);

		StringBuilder bugList = new StringBuilder();
		boolean firstTime = true;
		List<LinkedTicket> linkedTicketsForExecutionId = linkedTicketManagerImpl
				.getLinkedTicketsForExecutionId(executionId);
		for (LinkedTicket linkedTicket : linkedTicketsForExecutionId) {
			if (firstTime) {
				bugList.append(linkedTicket.getTicketNumber());
				firstTime = false;
			} else {
				bugList.append("," + linkedTicket.getTicketNumber());
			}
		}

		final String bugs = bugList.toString();
		try {
			return jdbcTemplate.queryForObject(query.toString(),
					new RowMapper<TestCaseExecutions>() {
						@Override
						public TestCaseExecutions mapRow(ResultSet rs,
								int rownumber) throws SQLException {
							TestCaseExecutions logEntity = new TestCaseExecutions();
							logEntity.setTestCaseExecutionsId(
									rs.getLong("testcase_execution_id"));
							logEntity.setTestCaseId(rs.getInt("testCase_id"));
							logEntity.setTestStepId(rs.getLong("test_step_id"));
							logEntity.setTestRunBy(rs.getString("test_runBy"));
							logEntity.setActualResult(
									rs.getString("actual_result"));
							logEntity.setExecutionDate(
									rs.getTimestamp("execution_date"));
							logEntity.setTestResult(TestResults
									.fromString(rs.getString("test_result")));
							logEntity.setEnvironmentId(
									rs.getInt("environment_id"));
							logEntity.setActualLOE(rs.getInt("actual_loe"));
							logEntity.setCreatedBy(rs.getString("created_by"));
							logEntity
									.setModifiedBy(rs.getString("modified_by"));
							logEntity.setCreatedDate(
									rs.getTimestamp("created_date"));
							logEntity.setModifiedDate(
									rs.getTimestamp("modified_date"));
							logEntity.setDeleted(rs.getBoolean("is_deleted"));
							logEntity.setReleaseId(rs.getInt("release_id"));
							logEntity.setLinkedBug(bugs);

							return logEntity;
						}
					});
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public List<TestCaseExecutions> getAllLatestTestCaseExecutions(
			int releaseId, int environmentId, int testCaseId,
			String testStepIds) throws APIExceptions {
		updateDataSource();
		String query = "SELECT t1.* FROM testCase_execution as t1 "
				+ "INNER JOIN "
				+ "(SELECT testcase_id, test_step_id, release_id, MAX(created_date) as created_date "
				+ "FROM testCase_execution GROUP BY testCase_id, test_step_id, release_id) as t2 "
				+ "ON " + "t1.testcase_id=t2.testcase_id "
				+ "AND t1.test_step_id=t2.test_step_id "
				+ "AND t1.release_id=t2.release_id "
				+ "AND t1.created_date=t2.created_date "
				+ "WHERE t1.release_id=" + releaseId + " AND t1.environment_id="
				+ environmentId + " AND t1.is_deleted=false "
				+ "AND t1.testcase_id=" + testCaseId;
		if (!(null == testStepIds || testStepIds.equals(""))) {
			query += " AND t1.test_step_id IN (" + testStepIds + ")";
		}
		query += " ORDER BY t1.testcase_id";
		return jdbcTemplate.query(query, new RowMapper<TestCaseExecutions>() {
			@Override
			public TestCaseExecutions mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				TestCaseExecutions logEntity = new TestCaseExecutions();
				logEntity.setTestCaseExecutionsId(
						rs.getLong("testcase_execution_id"));
				logEntity.setTestCaseId(rs.getInt("testcase_id"));
				logEntity.setTestStepId(rs.getLong("test_step_id"));
				logEntity.setTestRunBy(rs.getString("test_runBy"));
				logEntity.setActualResult(rs.getString("actual_result"));
				logEntity.setExecutionDate(rs.getTimestamp("execution_date"));
				logEntity.setTestResult(
						TestResults.fromString(rs.getString("test_result")));
				logEntity.setEnvironmentId(environmentId);
				logEntity.setActualLOE(rs.getInt("actual_loe"));
				logEntity.setCreatedBy(rs.getString("created_by"));
				logEntity.setModifiedBy(rs.getString("modified_by"));
				logEntity.setCreatedDate(rs.getTimestamp("created_date"));
				logEntity.setModifiedDate(rs.getTimestamp("modified_date"));
				logEntity.setDeleted(rs.getBoolean("is_deleted"));
				String stepKeyword = rs.getString("step_keyword");
				if (stepKeyword == null || stepKeyword.equals("null")) {
					stepKeyword = "";
				}
				logEntity.setStepKeyword(stepKeyword);
				logEntity.setReleaseId(releaseId);
				return logEntity;
			}
		});
	}

	@Override
	public Map<Integer, List<TestCaseExecutions>> getAllLatestTestCaseExecutions(
			int releaseId, int environmentId, String testCaseIds)
			throws APIExceptions {
		updateDataSource();
		String query = "SELECT t1.* FROM testCase_execution as t1 "
				+ "INNER JOIN "
				+ "(SELECT testcase_id, test_step_id, release_id, MAX(created_date) as created_date "
				+ "FROM testCase_execution GROUP BY testCase_id, test_step_id, release_id) as t2 "
				+ "ON " + "t1.testcase_id=t2.testcase_id "
				+ "AND t1.test_step_id=t2.test_step_id "
				+ "AND t1.release_id=t2.release_id "
				+ "AND t1.created_date=t2.created_date "
				+ "WHERE t1.release_id=" + releaseId + " AND t1.environment_id="
				+ environmentId + " AND t1.is_deleted=false "
				+ "AND t1.testcase_id IN (" + testCaseIds
				+ ") ORDER BY t1.testcase_id";
		try {
			return jdbcTemplate.query(query,
					new ResultSetExtractor<Map<Integer, List<TestCaseExecutions>>>() {
						@Override
						public Map<Integer, List<TestCaseExecutions>> extractData(
								ResultSet rs) throws SQLException {
							Map<Integer, List<TestCaseExecutions>> result = new LinkedHashMap<Integer, List<TestCaseExecutions>>();
							while (rs.next()) {
								TestCaseExecutions testCaseExecution = new TestCaseExecutions();
								testCaseExecution.setTestCaseExecutionsId(
										rs.getLong("testcase_execution_id"));
								testCaseExecution.setTestCaseId(
										rs.getInt("testcase_id"));
								testCaseExecution.setTestStepId(
										rs.getLong("test_step_id"));
								testCaseExecution.setTestRunBy(
										rs.getString("test_runBy"));
								testCaseExecution.setActualResult(
										rs.getString("actual_result"));
								testCaseExecution.setExecutionDate(
										rs.getTimestamp("execution_date"));
								testCaseExecution
										.setTestResult(TestResults.fromString(
												rs.getString("test_result")));
								testCaseExecution
										.setEnvironmentId(environmentId);
								testCaseExecution
										.setActualLOE(rs.getInt("actual_loe"));
								testCaseExecution.setCreatedBy(
										rs.getString("created_by"));
								testCaseExecution.setModifiedBy(
										rs.getString("modified_by"));
								testCaseExecution.setCreatedDate(
										rs.getTimestamp("created_date"));
								testCaseExecution.setModifiedDate(
										rs.getTimestamp("modified_date"));
								testCaseExecution.setDeleted(
										rs.getBoolean("is_deleted"));
								String stepKeyword = rs
										.getString("step_keyword");
								if (stepKeyword == null
										|| stepKeyword.equals("null")) {
									stepKeyword = "";
								}
								testCaseExecution.setStepKeyword(stepKeyword);
								testCaseExecution.setReleaseId(releaseId);
								if (null == result
										.get(rs.getInt("testcase_id"))) {
									final List<TestCaseExecutions> testCaseExecutions = new ArrayList<TestCaseExecutions>();
									testCaseExecutions.add(testCaseExecution);
									result.put(rs.getInt("testcase_id"),
											testCaseExecutions);
								} else {
									List<TestCaseExecutions> list = result
											.get(rs.getInt("testcase_id"));
									list.add(testCaseExecution);
									result.put(rs.getInt("testcase_id"), list);
								}
							}
							return result;
						}
					});
		} catch (Exception e) {
			return null;
		}
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
