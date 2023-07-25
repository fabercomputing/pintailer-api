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
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.fw.dao.ITestStepManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.TestScenarioStep;
import com.fw.domain.TestStep;
import com.fw.domain.TestStepVersion;
import com.fw.exceptions.APIExceptions;
import com.fw.utils.ApplicationCommonUtil;

@Repository
public class TestStepManagerImpl implements ITestStepManager {

	private Logger log = Logger.getLogger(TestStepManagerImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	public TestStep persistTestStep(TestStep logEntity) throws APIExceptions {
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		updateDataSource();
		try {
			String sql = "INSERT INTO test_step (name, hash_code, created_by, "
					+ "modified_by, client_project_id, applicable) VALUES(?,?,?,?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "test_step_id" });
					ps.setString(1, logEntity.getName());
					ps.setString(2, logEntity.getHashCode());
					ps.setString(3, currentUser);
					ps.setString(4, currentUser);
					ps.setInt(5, logEntity.getClientProjectId());
					ps.setBoolean(6, logEntity.isApplicable());
					return ps;
				}
			}, requestKeyHolder);
			long logEntityId = requestKeyHolder.getKey().longValue();
			logEntity.setTestStepId(logEntityId);
			return logEntity;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions(e.getMessage());
		}

	}

	@Override
	public int updateTestStepById(TestStep logEntity) throws APIExceptions {
		updateDataSource();
		String sql = "UPDATE test_step SET name=?, hash_code=?, modified_by=?, "
				+ "client_project_id=? WHERE test_step_id=?";
		try {
			return jdbcTemplate.update(sql, logEntity.getName(),
					logEntity.getHashCode(),
					applicationCommonUtil.getCurrentUser(),
					logEntity.getClientProjectId(), logEntity.getTestStepId());
		} catch (Exception e) {
			log.error(logEntity.getName() + ":" + logEntity.getHashCode());
			throw new APIExceptions(e);
		}
	}

	@Override
	public void updateTestStepById(TestStep logEntity, boolean applicableFlg)
			throws APIExceptions {
		updateDataSource();
		String sql = "UPDATE test_step SET applicable=?, modified_by=? "
				+ "WHERE test_step_id=?";
		jdbcTemplate.update(sql, applicableFlg,
				applicationCommonUtil.getCurrentUser(),
				logEntity.getTestStepId());
	}

	@Override
	public void deleteTestStepById(long Id) throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM test_step WHERE test_step_id=?";
		try {
			jdbcTemplate.update(sql, Id);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public List<TestStep> getAllTestStepRowMapper(int clientProjectId)
			throws APIExceptions {
		updateDataSource();
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM test_step");

		if (clientProjectId > 0) {
			query.append(" WHERE client_project_id=" + clientProjectId);
		} else {
			if (null != applicationCommonUtil.getAssignedProjectIds()) {
				query.append(" WHERE c.client_project_id IN ("
						+ applicationCommonUtil.getAssignedProjectIds() + ")");
			}
		}

		try {
			return jdbcTemplate.query(query.toString(), new RowMapper<TestStep>() {
				@Override
				public TestStep mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					TestStep testStep = new TestStep();
					testStep.setTestStepId(rs.getLong("test_step_id"));
					testStep.setName(rs.getString("name"));
					testStep.setHashCode(rs.getString("hash_code"));
					testStep.setCreatedBy(rs.getString("created_by"));
					testStep.setModifiedBy(rs.getString("modified_by"));
					testStep.setCreatedDate(rs.getTimestamp("created_date"));
					testStep.setModifiedDate(rs.getTimestamp("modified_date"));
					testStep.setClientProjectId(rs.getInt("client_project_id"));
					testStep.setApplicable(rs.getBoolean("applicable"));
					return testStep;
				}
			});
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public TestStep getTestStepById(long stepId, int clientProjectId)
			throws APIExceptions {
		updateDataSource();
		String qry = "SELECT v.version_id, t.* FROM fw_test_mgmt.test_step t "
				+ "LEFT JOIN fw_test_mgmt.test_step_version v "
				+ "ON t.hash_code=v.hash_code AND t.test_step_id=v.test_step_id "
				+ "AND v.version_id=(SELECT MAX(version_id) FROM "
				+ "fw_test_mgmt.test_step_version WHERE hash_code= t.hash_code) "
				+ "WHERE t.is_deleted=false AND t.test_step_id=?";
		if (clientProjectId > 0) {
			qry += " AND t.client_project_id=" + clientProjectId;
		}
		try {
			return jdbcTemplate.queryForObject(qry, new RowMapper<TestStep>() {
				@Override
				public TestStep mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					return fetchRS(rs);
				}
			}, new Object[] { stepId });
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public List<TestStepVersion> getTestStepsVersionByStepId(long stepId)
			throws APIExceptions {
		updateDataSource();
		try {
			return jdbcTemplate.query(
					"SELECT * FROM test_step_version WHERE test_step_id=? "
							+ "ORDER BY version_id DESC",
					new RowMapper<TestStepVersion>() {
						@Override
						public TestStepVersion mapRow(ResultSet rs,
								int rownumber) throws SQLException {
							TestStepVersion testStepVersion = new TestStepVersion();
							testStepVersion
									.setTestStepId(rs.getLong("test_step_id"));
							testStepVersion.setName(rs.getString("name"));
							testStepVersion
									.setHashCode(rs.getString("hash_code"));
							testStepVersion
									.setCreatedBy(rs.getString("created_by"));
							testStepVersion
									.setModifiedBy(rs.getString("modified_by"));
							testStepVersion.setCreatedDate(
									rs.getTimestamp("created_date"));
							testStepVersion.setModifiedDate(
									rs.getTimestamp("modified_date"));
							testStepVersion.setClientProjectId(
									rs.getInt("client_project_id"));
							testStepVersion.setTestStepVersion(
									"V" + rs.getInt("version_id"));
							return testStepVersion;
						}
					}, stepId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public TestStepVersion getTestStepsVersionByStepIdAndVersionId(long stepId,
			int versionId) throws APIExceptions {
		updateDataSource();
		try {
			return jdbcTemplate.queryForObject(
					"SELECT * FROM test_step_version WHERE test_step_id=? "
							+ " AND version_id=? ORDER BY version_id DESC",
					new RowMapper<TestStepVersion>() {
						@Override
						public TestStepVersion mapRow(ResultSet rs,
								int rownumber) throws SQLException {
							TestStepVersion testStepVersion = new TestStepVersion();
							testStepVersion
									.setTestStepId(rs.getLong("test_step_id"));
							testStepVersion.setName(rs.getString("name"));
							testStepVersion
									.setHashCode(rs.getString("hash_code"));
							testStepVersion
									.setCreatedBy(rs.getString("created_by"));
							testStepVersion
									.setModifiedBy(rs.getString("modified_by"));
							testStepVersion.setCreatedDate(
									rs.getTimestamp("created_date"));
							testStepVersion.setModifiedDate(
									rs.getTimestamp("modified_date"));
							testStepVersion.setClientProjectId(
									rs.getInt("client_project_id"));
							testStepVersion.setTestStepVersion(
									"V" + rs.getInt("version_id"));
							return testStepVersion;
						}
					}, new Object[] { stepId, versionId });
		} catch (EmptyResultDataAccessException e) {
			log.error("Version data is not avaialble for step id [" + stepId
					+ "] with version id [" + versionId + "]");
			return null;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public List<TestStep> getTestStepsByScenarioSteps(
			List<TestScenarioStep> testScenarioSteps) throws APIExceptions {
		updateDataSource();
		String idList = null;
		boolean firstTime = true;
		int sequence = 1;
		for (TestScenarioStep testScenarioStep : testScenarioSteps) {
			if (firstTime) {
				idList = "(" + testScenarioStep.getTestStepId() + "," + sequence
						+ ")";
				firstTime = false;
			} else {
				idList += ",(" + testScenarioStep.getTestStepId() + ","
						+ sequence + ")";
			}
			sequence++;
		}
		String qry = "SELECT c.* FROM test_step c JOIN (VALUES " + idList
				+ ") AS x (test_step_id, ordering) ON "
				+ "c.test_step_id = x.test_step_id AND c.applicable=true "
				+ "AND c.is_deleted=false ORDER BY x.ordering";
		try {
			return jdbcTemplate.query(qry, new RowMapper<TestStep>() {
				@Override
				public TestStep mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					return fetchRS(rs);
				}
			});
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public List<TestStep> getTestStepsByScenarioId(long testScenarioId)
			throws APIExceptions {
		updateDataSource();
//		String qry = "SELECT c.test_step_id, CONCAT(t.step_keyword, ' ', c.name) stepName, "
//				+ "c.hash_code, c.created_by, c.modified_by, c.created_date, "
//				+ "c.modified_date, c.client_project_id "
//				+ "FROM test_step c, test_scenario_step t "
//				+ "WHERE c.test_step_id = t.test_step_id AND t.test_scenarios_id="
//				+ testScenarioId
//				+ " AND c.applicable=true AND c.is_deleted=false ORDER BY t.test_step_sequence";

		String qry = "SELECT v.version_id, c.test_step_id, "
				+ "CONCAT(t.step_keyword, ' ', c.name) stepName, c.hash_code, "
				+ "c.created_by, c.modified_by, c.created_date, c.modified_date, "
				+ "c.client_project_id, t.test_step_sequence "
				+ "FROM fw_test_mgmt.test_step c "
				+ "INNER JOIN fw_test_mgmt.test_scenario_step t "
				+ "ON c.test_step_id = t.test_step_id AND t.test_scenarios_id="
				+ testScenarioId
				+ "AND c.applicable=true AND c.is_deleted=false "
				+ "LEFT JOIN fw_test_mgmt.test_step_version v "
				+ "ON c.hash_code=v.hash_code AND c.test_step_id=v.test_step_id "
				+ "AND v.version_id=(SELECT MAX(version_id) FROM "
				+ "fw_test_mgmt.test_step_version WHERE hash_code= c.hash_code "
				+ "AND test_step_id=c.test_step_id) ORDER BY t.test_step_sequence ASC";
		try {
			return jdbcTemplate.query(qry, new RowMapper<TestStep>() {
				@Override
				public TestStep mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					TestStep testStep = new TestStep();
					testStep.setTestStepId(rs.getLong("test_step_id"));
					testStep.setName(rs.getString("stepName"));
					testStep.setHashCode(rs.getString("hash_code"));
					testStep.setCreatedBy(rs.getString("created_by"));
					testStep.setModifiedBy(rs.getString("modified_by"));
					testStep.setCreatedDate(rs.getTimestamp("created_date"));
					testStep.setModifiedDate(rs.getTimestamp("modified_date"));
					testStep.setClientProjectId(rs.getInt("client_project_id"));
					testStep.setApplicable(rs.getBoolean("applicable"));
					testStep.setStepLatestVersion(
							"V" + (rs.getInt("version_id") == 0 ? 1
									: rs.getInt("version_id")));
//					testStep.setStepSelectedVersion(
//							testStep.getStepLatestVersion());
					return testStep;
				}
			});
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public TestStep getTestStepIdByHashCode(String hashCode)
			throws APIExceptions {
		updateDataSource();
		String sql = "SELECT test_step_id FROM test_step WHERE hash_code=?";
		try {
			return jdbcTemplate.queryForObject(sql, new RowMapper<TestStep>() {
				@Override
				public TestStep mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					TestStep logEntity = new TestStep();
					logEntity.setTestStepId(rs.getLong("test_step_id"));
					logEntity.setHashCode(hashCode);
					return logEntity;
				}
			}, hashCode);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public Map<String, Long> getTestStepHashCode() throws APIExceptions {
		updateDataSource();
		Map<String, Long> result = new LinkedHashMap<String, Long>();
		String sql = "SELECT hash_code,test_step_id FROM test_step WHERE "
				+ "is_deleted=false";
		try {
			return jdbcTemplate.query(sql,
					new ResultSetExtractor<Map<String, Long>>() {
						@Override
						public Map<String, Long> extractData(ResultSet rs)
								throws SQLException, DataAccessException {
							while (rs.next()) {
								result.put(rs.getString("hash_code"),
										rs.getLong("test_step_id"));
							}
							return result;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<TestStep> getTestStepsByIds(String testStepIds)
			throws APIExceptions {
		updateDataSource();
		if (null == testStepIds || testStepIds.equals("")
				|| testStepIds.equalsIgnoreCase("null")) {
			log.info("Test steps ids are not given.");
			return null;
		}

		String qry = "SELECT v.version_id, c.test_step_id, c.name, c.hash_code, "
				+ "c.created_by, c.modified_by, c.created_date, c.modified_date, "
				+ "c.client_project_id, c.applicable FROM test_step c "
				+ "LEFT JOIN fw_test_mgmt.test_step_version v "
				+ "ON c.hash_code=v.hash_code AND c.test_step_id=v.test_step_id "
				+ "AND v.version_id=(SELECT MAX(version_id) FROM "
				+ "fw_test_mgmt.test_step_version WHERE hash_code= c.hash_code "
				+ "AND test_step_id=c.test_step_id) "
				+ "WHERE c.test_step_id IN (" + testStepIds
				+ ") AND c.applicable=true AND c.is_deleted=false";
		try {
			return jdbcTemplate.query(qry, new RowMapper<TestStep>() {
				@Override
				public TestStep mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					return fetchRS(rs);
				}
			});
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	// Added for DB operations to update the client project IDs and hash code
	// for all the existing steps.
	@Override
	public int getTestStepProjectId(long testStepId) throws APIExceptions {
		updateDataSource();
		if (testStepId <= 0) {
			log.info("Test steps id is not given.");
			return 0;
		}
		String qry = "SELECT DISTINCT(client_project_id) FROM test_scenarios "
				+ "WHERE test_scenarios_id IN (SELECT test_scenarios_id FROM "
				+ "test_scenario_step WHERE test_step_id=?)";
		try {
			return jdbcTemplate.queryForObject(qry, new RowMapper<Integer>() {
				@Override
				public Integer mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					return rs.getInt("client_project_id");
				}
			}, testStepId);
		} catch (DataAccessException e) {
			log.error("Test step with id [" + testStepId
					+ "] is not associated to any feature: " + e.getMessage());
			return 0;
		}
	}

	private TestStep fetchRS(ResultSet rs) throws SQLException {
		TestStep testStep = new TestStep();
		testStep.setTestStepId(rs.getLong("test_step_id"));
		testStep.setName(rs.getString("name"));
		testStep.setHashCode(rs.getString("hash_code"));
		testStep.setCreatedBy(rs.getString("created_by"));
		testStep.setModifiedBy(rs.getString("modified_by"));
		testStep.setCreatedDate(rs.getTimestamp("created_date"));
		testStep.setModifiedDate(rs.getTimestamp("modified_date"));
		testStep.setClientProjectId(rs.getInt("client_project_id"));
		testStep.setApplicable(rs.getBoolean("applicable"));
		testStep.setStepLatestVersion("V"
				+ (rs.getInt("version_id") == 0 ? 1 : rs.getInt("version_id")));
//		testStep.setStepSelectedVersion(testStep.getStepLatestVersion());
		return testStep;
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
