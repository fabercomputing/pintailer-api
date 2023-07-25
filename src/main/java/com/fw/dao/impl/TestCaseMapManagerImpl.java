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

import com.fw.dao.ITestCaseMapManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.TestCaseMap;
import com.fw.domain.TestCaseMapVersion;
import com.fw.exceptions.APIExceptions;
import com.fw.utils.ApplicationCommonUtil;
import com.fw.utils.ValueValidations;

@Repository
public class TestCaseMapManagerImpl implements ITestCaseMapManager {

	private Logger log = Logger.getLogger(TestCaseMapManagerImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	public int persistTestCaseMap(int testCaseId, String testStepIds,
			int testScenarioId) throws APIExceptions {
		updateDataSource();
		String[] testStepsIds = testStepIds.split(",");
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO testcase_map (testcase_id, test_step_id, "
					+ "test_scenarios_id, created_by, modified_by) "
					+ " VALUES(?,?,?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();
			int update = 0;
			for (String stepId : testStepsIds) {
				update += jdbcTemplate.update(new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(
							Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement(sql,
								new String[] { "testcase_map_id" });
						ps.setInt(1, testCaseId);
						ps.setLong(2, Long.parseLong(stepId));
						ps.setInt(3, testScenarioId);
						ps.setString(4, currentUser);
						ps.setString(5, currentUser);
						return ps;
					}
				}, requestKeyHolder);
			}
			return update;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions(
					"Error occured while mapping the test case with automated test steps.");
		}
	}

	@Override
	public TestCaseMapVersion persistTestCaseMapVersion(
			TestCaseMapVersion testCaseMapVersion) throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO testcase_map_version (testcase_id, "
					+ "testcase_version_id, test_scenario_step_version_id, "
					+ "selected_test_step_ids, created_by, modified_by) "
					+ " VALUES(?,?,?,?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "testcase_map_version_id" });
					ps.setInt(1, testCaseMapVersion.getTestCaseId());
					ps.setInt(2, testCaseMapVersion.getTestCaseVersionId());
					ps.setInt(3,
							testCaseMapVersion.getTestScenarioStepVersionId());
					ps.setString(4, testCaseMapVersion
							.getSelectedTestStepsIdAndVersion());
					ps.setString(5, currentUser);
					ps.setString(6, currentUser);
					return ps;
				}
			}, requestKeyHolder);
			int logEntityId = requestKeyHolder.getKey().intValue();
			testCaseMapVersion.setTestCaseMapVersionId(logEntityId);
			return testCaseMapVersion;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions(
					"Error occured while storing the version info of test case "
							+ "and automated test steps mapping.");
		}
	}

	@Override
	public void updateTestCaseMapById(TestCaseMap logEntity)
			throws APIExceptions {
		updateDataSource();
		String sql = "UPDATE testcase_map  SET testcase_id=?, test_step_id=?, "
				+ " test_scenarios_id=?, modified_by=? "
				+ "WHERE testcase_map_id=?";
		jdbcTemplate.update(sql, logEntity.getTestCaseId(),
				logEntity.getTestStepId(), logEntity.getTestScenarioId(),
				applicationCommonUtil.getCurrentUser(),
				logEntity.getTestCaseMapId());
	}

	@Override
	public void deleteTestCaseMapById(long testCaseMapId) throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM testcase_map WHERE testcase_map_id=?";
		try {
			jdbcTemplate.update(sql, testCaseMapId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public void deleteTestCaseMapByTestCaseIdAndScenarioId(int testCaseId,
			int testScenarioId) throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM testcase_map WHERE testcase_id=? ";
//				+ "AND test_scenarios_id=?";
		try {
//			jdbcTemplate.update(sql, testCaseId, testScenarioId);
			jdbcTemplate.update(sql, testCaseId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions(
					"Error occured while updating test case mapping");
		}
	}

	@Override
	public void deleteTestCaseMapByScenarioId(int testScenarioId,
			long testStepId, int testCaseId) throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM testcase_map WHERE test_scenarios_id="
				+ testScenarioId;

		if (testStepId > 0) {
			sql += " AND test_step_id=" + testStepId;
		}
		if (testCaseId > 0) {
			sql += " AND testcase_id=" + testCaseId;
		}

		try {
			jdbcTemplate.update(sql);
		} catch (DataAccessException e) {
			String message = "Error : occured while deleting data for test steps mapped "
					+ "with test cases for scenario ID [" + testScenarioId
					+ "]";
			if (testStepId >= 0) {
				message += " AND test step id [" + testStepId + "]";
			}
			if (testCaseId >= 0) {
				message += " AND test case id [" + testCaseId + "]";
			}
			message += " : " + e.getMessage();
			log.error(message);
			throw new APIExceptions(message);
		}
	}

	@Override
	public TestCaseMap getTestCaseMapById(long testCaseMapId)
			throws APIExceptions {
		updateDataSource();
		try {
			return jdbcTemplate.queryForObject(
					"SELECT * FROM test_step WHERE test_step_id=?",
					new RowMapper<TestCaseMap>() {
						@Override
						public TestCaseMap mapRow(ResultSet rs, int rownumber)
								throws SQLException {
							TestCaseMap logEntity = new TestCaseMap();
							logEntity.setTestCaseMapId(
									rs.getLong("testcase_map_id"));
							logEntity.setTestCaseId(rs.getInt("testcase_id"));
							logEntity.setTestStepId(rs.getLong("test_step_id"));
							logEntity.setTestScenarioId(
									rs.getInt("test_scenarios_id"));
							logEntity.setCreatedBy(rs.getString("created_by"));
							logEntity
									.setModifiedBy(rs.getString("modified_by"));
							logEntity.setCreatedDate(
									rs.getTimestamp("created_date"));
							logEntity.setModifiedDate(
									rs.getTimestamp("modified_date"));
							return logEntity;
						}
					}, testCaseMapId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public List<TestCaseMap> getTestCaseMapByTestCaseId(int testCaseId)
			throws APIExceptions {
		updateDataSource();
		try {
			String sql = "SELECT testcase_id, test_scenarios_id, test_step_id "
					+ "FROM testcase_map WHERE testcase_id=?";
			return jdbcTemplate.query(sql, new RowMapper<TestCaseMap>() {
				@Override
				public TestCaseMap mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					TestCaseMap logEntity = new TestCaseMap();
					logEntity.setTestCaseId(rs.getInt("testcase_id"));
					logEntity.setTestScenarioId(rs.getInt("test_scenarios_id"));
					logEntity.setTestStepId(rs.getLong("test_step_id"));
					return logEntity;
				}
			}, testCaseId);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Map<Integer, List<TestCaseMap>> getTestCaseMapByTestCaseIds(
			String testCaseIds) throws APIExceptions {
		updateDataSource();
		try {
			String sql = "SELECT testcase_id, test_scenarios_id, test_step_id "
					+ "FROM testcase_map WHERE testcase_id IN (" + testCaseIds
					+ ")";
			return jdbcTemplate.query(sql,
					new ResultSetExtractor<Map<Integer, List<TestCaseMap>>>() {
						@Override
						public Map<Integer, List<TestCaseMap>> extractData(
								ResultSet rs) throws SQLException {
							Map<Integer, List<TestCaseMap>> result = new LinkedHashMap<Integer, List<TestCaseMap>>();
							while (rs.next()) {
								TestCaseMap testCaseMap = new TestCaseMap();
								testCaseMap.setTestCaseId(
										rs.getInt("testcase_id"));
								testCaseMap.setTestScenarioId(
										rs.getInt("test_scenarios_id"));
								testCaseMap.setTestStepId(
										rs.getLong("test_step_id"));

								if (null == result
										.get(rs.getInt("testcase_id"))) {
									final List<TestCaseMap> testCaseMaps = new ArrayList<TestCaseMap>();
									testCaseMaps.add(testCaseMap);
									result.put(rs.getInt("testcase_id"),
											testCaseMaps);
								} else {
									List<TestCaseMap> list = result
											.get(rs.getInt("testcase_id"));
									list.add(testCaseMap);
									result.put(rs.getInt("testcase_id"), list);
								}
							}
							return result;
						}
					});
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public List<TestCaseMap> getTestCaseMapByTestCaseIdAndTestStepId(
			int testCaseId, long testStepId, int clientProjectId)
			throws APIExceptions {
		updateDataSource();
		String sql = "SELECT tm.testcase_id as testcase_id, tm.test_scenarios_id as "
				+ "test_scenarios_id, tm.test_step_id as test_step_id "
				+ "FROM testcase_map tm, testcase t, modules m "
				+ "WHERE t.applicable=true AND tm.testcase_id=? AND tm.test_step_id=? "
				+ "AND tm.testcase_id=t.testcase_id AND t.module_id=m.module_id "
				+ "AND m.client_project_id=?";
		return jdbcTemplate.query(sql, new RowMapper<TestCaseMap>() {
			@Override
			public TestCaseMap mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				TestCaseMap logEntity = new TestCaseMap();
				logEntity.setTestCaseId(rs.getInt("testcase_id"));
				logEntity.setTestScenarioId(rs.getInt("test_scenarios_id"));
				logEntity.setTestStepId(rs.getLong("test_step_id"));
				return logEntity;
			}
		}, new Object[] { testCaseId, testStepId, clientProjectId });
	}

	@Override
	public TestCaseMap getMapByCaseIdStepIdAndScenarioId(int testCaseId,
			long testStepId, int testScenarioId, int clientProjectId)
			throws APIExceptions {
		updateDataSource();
		String sql = "SELECT tm.testcase_id FROM testcase_map tm, testcase t, modules m "
				+ "WHERE t.applicable=true AND tm.testcase_id=? AND tm.test_step_id=? "
				+ "AND tm.test_scenarios_id=? AND tm.testcase_id=t.testcase_id "
				+ "AND t.module_id=m.module_id AND m.client_project_id=?";
		try {
			return jdbcTemplate.queryForObject(sql,
					new RowMapper<TestCaseMap>() {
						@Override
						public TestCaseMap mapRow(ResultSet rs, int rownumber)
								throws SQLException {
							TestCaseMap logEntity = new TestCaseMap();
							logEntity.setTestCaseId(rs.getInt("testcase_id"));
							logEntity.setTestScenarioId(testScenarioId);
							logEntity.setTestStepId(testStepId);
							return logEntity;
						}
					}, new Object[] { testCaseId, testStepId, testScenarioId,
							clientProjectId });
		} catch (DataAccessException e) {
			log.error("Data not avaiable : " + e.getMessage());
			return null;
		}
	}

	@Override
	public List<TestCaseMap> getTestCaseMappings(int clientProjectId)
			throws APIExceptions {
		updateDataSource();
		String sql = "SELECT tm.testcase_id,tm.test_step_id,tm.test_scenarios_id "
				+ "FROM testcase_map tm, testcase t, modules m WHERE t.applicable=true AND "
				+ "tm.testcase_id=t.testcase_id AND t.module_id=m.module_id";
		if (clientProjectId > 0) {
			sql += " AND m.client_project_id=" + clientProjectId;
		}
		// sql += " AND is_deleted=false";
		sql += " ORDER BY tm.testcase_id";
		return jdbcTemplate.query(sql, new RowMapper<TestCaseMap>() {
			@Override
			public TestCaseMap mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				TestCaseMap logEntity = new TestCaseMap();
				logEntity.setTestCaseId(rs.getInt("testcase_id"));
				logEntity.setTestStepId(rs.getLong("test_step_id"));
				logEntity.setTestScenarioId(rs.getInt("test_scenarios_id"));
				return logEntity;
			}
		});
	}

	@Override
	public List<TestCaseMap> getInfo(final int clientProjectId,
			final String stepHashCode, final int testScenarioId,
			final int testStepSequence) throws APIExceptions {
		updateDataSource();
		String query = "SELECT tm.testcase_id,tm.test_step_id,tm.test_scenarios_id "
				+ "FROM testcase_map tm, testcase t, modules m ";

		if (testStepSequence > 0) {
			query += ", test_scenario_step ts ";
		}

		query += "WHERE t.applicable=true AND t.is_deleted=false AND ";

		if (!(null == stepHashCode || stepHashCode.trim().equals(""))) {
			query += "tm.test_step_id=(SELECT test_step_id FROM test_step "
					+ "WHERE hash_code='" + stepHashCode + "') AND ";
		}

		query += "tm.test_scenarios_id=" + testScenarioId
				+ " AND tm.testcase_id=t.testcase_id AND t.module_id=m.module_id "
				+ "AND m.client_project_id=" + clientProjectId;

		if (testStepSequence > 0) {
			query += " AND tm.test_scenarios_id=ts.test_scenarios_id AND "
					+ "tm.test_step_id=ts.test_step_id AND ts.test_step_sequence="
					+ testStepSequence;
		}

		return jdbcTemplate.query(query, new RowMapper<TestCaseMap>() {
			@Override
			public TestCaseMap mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				TestCaseMap logEntity = new TestCaseMap();
				logEntity.setTestCaseId(rs.getInt("testcase_id"));
				logEntity.setTestScenarioId(testScenarioId);
				logEntity.setTestStepId(rs.getLong("test_step_id"));
				return logEntity;
			}
		});
	}

	@Override
	public List<TestCaseMapVersion> getTestCaseMapVersion(int testCaseId,
			int testCaseVersionId) throws APIExceptions {
		updateDataSource();
		try {
			return jdbcTemplate.query(
					"SELECT * FROM testcase_map_version WHERE testcase_id="
							+ testCaseId + " AND testcase_version_id="
							+ testCaseVersionId + " ORDER BY version_id DESC",
					new RowMapper<TestCaseMapVersion>() {
						@Override
						public TestCaseMapVersion mapRow(ResultSet rs,
								int rownumber) throws SQLException {
							TestCaseMapVersion testCaseMapVersion = new TestCaseMapVersion();
							testCaseMapVersion.setTestCaseMapVersionId(
									rs.getInt("testcase_map_version_id"));
							testCaseMapVersion
									.setTestCaseId(rs.getInt("testcase_id"));
							testCaseMapVersion.setTestCaseVersionId(
									rs.getInt("testcase_version_id"));
							testCaseMapVersion.setSelectedTestStepsIdAndVersion(
									rs.getString("selected_test_step_ids"));
							testCaseMapVersion.setTestScenarioStepVersionId(
									rs.getInt("test_scenario_step_version_id"));
							testCaseMapVersion
									.setCreatedBy(rs.getString("created_by"));
							testCaseMapVersion
									.setModifiedBy(rs.getString("modified_by"));
							testCaseMapVersion.setCreatedDate(
									rs.getTimestamp("created_date"));
							testCaseMapVersion.setModifiedDate(
									rs.getTimestamp("modified_date"));
							testCaseMapVersion.setTestCaseMapVersion(
									"V" + rs.getInt("version_id"));
							testCaseMapVersion.setHardDeleted(
									rs.getBoolean("is_hard_deleted"));
							return testCaseMapVersion;
						}
					});
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public TestCaseMapVersion getTestCaseMapVersion(int testCaseMapVersionId)
			throws APIExceptions {
		updateDataSource();
		try {
			return jdbcTemplate.queryForObject(
					"SELECT * FROM testcase_map_version WHERE testcase_map_version_id=?",
					new RowMapper<TestCaseMapVersion>() {
						@Override
						public TestCaseMapVersion mapRow(ResultSet rs,
								int rownumber) throws SQLException {
							TestCaseMapVersion testCaseMapVersion = new TestCaseMapVersion();
							testCaseMapVersion.setTestCaseMapVersionId(
									rs.getInt("testcase_map_version_id"));
							testCaseMapVersion
									.setTestCaseId(rs.getInt("testcase_id"));
							testCaseMapVersion.setTestCaseVersionId(
									rs.getInt("testcase_version_id"));
							testCaseMapVersion.setSelectedTestStepsIdAndVersion(
									rs.getString("selected_test_step_ids"));
							testCaseMapVersion.setTestScenarioStepVersionId(
									rs.getInt("test_scenario_step_version_id"));
							testCaseMapVersion
									.setCreatedBy(rs.getString("created_by"));
							testCaseMapVersion
									.setModifiedBy(rs.getString("modified_by"));
							testCaseMapVersion.setCreatedDate(
									rs.getTimestamp("created_date"));
							testCaseMapVersion.setModifiedDate(
									rs.getTimestamp("modified_date"));
							testCaseMapVersion.setTestCaseMapVersion(
									"V" + rs.getInt("version_id"));
							testCaseMapVersion.setHardDeleted(
									rs.getBoolean("is_hard_deleted"));
							return testCaseMapVersion;
						}
					}, testCaseMapVersionId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public List<TestCaseMapVersion> getTestCaseMapMaxVersionForSpecificPeriod(
			int clientProjectId, String startDate, String endDate)
			throws APIExceptions {
		updateDataSource();
		StringBuilder query = new StringBuilder();
		query.append(
				"SELECT DISTINCT(t1.testcase_id), t1.version_id, t1.selected_test_step_ids FROM "
						+ "testcase_map_version t1, modules m, testcase t WHERE "
						+ "t1.version_id = (SELECT MAX(t2.version_id) FROM "
						+ "testcase_map_version t2 WHERE t2.testcase_id = t1.testcase_id "
						+ "GROUP BY t2.testcase_id) AND t1.testcase_id=t.testcase_id "
						+ "AND t.module_id=m.module_id AND m.client_project_id="
						+ clientProjectId);
		if (ValueValidations.isValueValid(startDate)) {
			query.append(" AND t1.created_date>='" + startDate + "'");
		}

		if (ValueValidations.isValueValid(endDate)) {
			query.append(" AND t1.created_date<='" + endDate + "'");
		}
		try {
			return jdbcTemplate.query(query.toString(),
					new RowMapper<TestCaseMapVersion>() {
						@Override
						public TestCaseMapVersion mapRow(ResultSet rs,
								int rownumber) throws SQLException {
							TestCaseMapVersion testCaseMapVersion = new TestCaseMapVersion();
							testCaseMapVersion
									.setTestCaseId(rs.getInt("testcase_id"));
							testCaseMapVersion.setTestCaseMapVersion(
									"V" + rs.getInt("version_id"));
							testCaseMapVersion.setSelectedTestStepsIdAndVersion(
									rs.getString("selected_test_step_ids"));
							return testCaseMapVersion;
						}
					});
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
