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
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.fw.dao.ITestScenarioStepManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.FeatureVersion;
import com.fw.domain.TestScenarioStep;
import com.fw.domain.TestScenarioStepVersion;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;
import com.fw.utils.ApplicationCommonUtil;
import com.fw.utils.GenerateUniqueHash;

@Repository
public class TestScenarioStepManagerImpl implements ITestScenarioStepManager {

	private Logger log = Logger.getLogger(TestScenarioStepManagerImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	public TestScenarioStep persistTestScenarioStep(
			TestScenarioStep testScenarioStep) throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO test_scenario_step (test_scenarios_id, "
					+ "test_step_id, test_step_sequence, step_keyword, "
					+ "scenario_outline_key_value, created_by, modified_by) "
					+ "VALUES(?,?,?,?,?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "test_scenario_step_id" });
					ps.setLong(1, testScenarioStep.getTestScenarioId());
					ps.setLong(2, testScenarioStep.getTestStepId());
					ps.setInt(3, testScenarioStep.getTestStepSequence());
					ps.setString(4, testScenarioStep.getStepKeyword());
					ps.setString(5,
							testScenarioStep.getScenarioOutlineKeyValue());
					ps.setString(6, currentUser);
					ps.setString(7, currentUser);
					return ps;
				}
			}, requestKeyHolder);
			int logEntityId = requestKeyHolder.getKey().intValue();
			testScenarioStep.setTestScenarioStepId(logEntityId);
			return testScenarioStep;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public void updateTestScenarioStepById(TestScenarioStep testScenarioStep)
			throws APIExceptions {
		updateDataSource();
		try {
			String sql = "UPDATE test_scenario_step SET test_scenarios_id=?, "
					+ "test_step_id=?, test_step_sequence=?, step_keyword=?, "
					+ "scenario_outline_key_value=?, modified_by=? "
					+ "WHERE test_scenario_step_id=?";
			jdbcTemplate.update(sql, testScenarioStep.getTestScenarioId(),
					testScenarioStep.getTestStepId(),
					testScenarioStep.getTestStepSequence(),
					testScenarioStep.getStepKeyword(),
					testScenarioStep.getScenarioOutlineKeyValue(),
					applicationCommonUtil.getCurrentUser(),
					testScenarioStep.getTestScenarioStepId());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	// Added for shifting the keyword from test step table to scenario test step
	// table
	@Override
	public void updateTestScenarioKeywordByStepStepId(long stepId,
			String keyword) throws APIExceptions {
		updateDataSource();
		String sql = "UPDATE test_scenario_step SET step_keyword='" + keyword
				+ "' WHERE test_step_id=" + stepId;
		jdbcTemplate.update(sql);
	}

	@Override
	public void deleteTestScenarioStepById(long testScenarioStepId)
			throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM test_scenario_step WHERE "
				+ "test_scenario_step_id=?";
		try {
			jdbcTemplate.update(sql, testScenarioStepId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
		}

	}

	@Override
	public void deleteTestScenarioStepByScenarioId(int scenarioId)
			throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM test_scenario_step WHERE "
				+ "test_scenarios_id=?";
		try {
			jdbcTemplate.update(sql, scenarioId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions(
					"Error : occured while deleting data for test steps mapped "
							+ "to test scenarios with sequence for scenario ID ["
							+ scenarioId + "] : " + e.getMessage());
		}

	}

	@Override
	public List<TestScenarioStep> getAllTestScenarioStep(int clientProjectId)
			throws APIExceptions {
		updateDataSource();
		String query = "SELECT t.test_scenario_step_id, t.test_scenarios_id, "
				+ "t.test_step_id, t.test_step_sequence, t.step_keyword, "
				+ "t.scenario_outline_key_value FROM test_scenario_step t, "
				+ "test_scenarios ts WHERE t.test_scenarios_id=ts.test_scenarios_id "
				+ "AND ts.client_project_id=" + clientProjectId
				+ " ORDER BY t.test_scenarios_id,t.test_step_sequence";
		return jdbcTemplate.query(query, new RowMapper<TestScenarioStep>() {
			@Override
			public TestScenarioStep mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				TestScenarioStep testScenarioStep = new TestScenarioStep();
				testScenarioStep.setTestScenarioStepId(
						rs.getLong("test_scenario_step_id"));
				testScenarioStep
						.setTestScenarioId(rs.getInt("test_scenarios_id"));
				testScenarioStep.setTestStepId(rs.getLong("test_step_id"));
				testScenarioStep
						.setTestStepSequence(rs.getInt("test_step_sequence"));
				testScenarioStep.setStepKeyword(rs.getString("step_keyword"));
				testScenarioStep.setScenarioOutlineKeyValue(
						rs.getString("scenario_outline_key_value"));
				return testScenarioStep;
			}
		});
	}

	@Override
	public TestScenarioStep getTestScenarioStepById(int clientProjectId,
			long testStepId) throws APIExceptions {
		updateDataSource();
		String query = "SELECT t.test_scenario_step_id, t.test_scenarios_id, "
				+ "t.test_step_sequence, t.step_keyword, t.scenario_outline_key_value "
				+ "FROM test_scenario_step t, test_scenarios ts, "
				+ "client_projects c WHERE t.test_step_id=" + testStepId
				+ " AND t.test_scenarios_id=ts.test_scenarios_id "
				+ "AND ts.client_project_id=c.client_project_id "
				+ "AND c.client_organization='"
				+ applicationCommonUtil.getDefaultOrgInOriginalCase() + "'"
				+ " AND c.client_project_id=" + clientProjectId;
		try {
			return jdbcTemplate.queryForObject(query,
					new RowMapper<TestScenarioStep>() {
						@Override
						public TestScenarioStep mapRow(ResultSet rs,
								int rownumber) throws SQLException {
							TestScenarioStep testScenarioStep = new TestScenarioStep();
							testScenarioStep.setTestScenarioStepId(
									rs.getLong("test_scenario_step_id"));
							testScenarioStep.setTestScenarioId(
									rs.getInt("test_scenarios_id"));
							testScenarioStep.setTestStepId(testStepId);
							testScenarioStep.setTestStepSequence(
									rs.getInt("test_step_sequence"));
							testScenarioStep.setStepKeyword(
									rs.getString("step_keyword"));
							testScenarioStep.setScenarioOutlineKeyValue(
									rs.getString("scenario_outline_key_value"));
							return testScenarioStep;
						}
					});
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public List<TestScenarioStep> getTestStepIdByScenarioId(int clientProjectId,
			int scenarioId) throws APIExceptions {
		updateDataSource();
		String query = "SELECT t.test_step_id,t.test_step_sequence,t.step_keyword, "
				+ "t.scenario_outline_key_value FROM test_scenario_step t, "
				+ "test_scenarios ts WHERE t.test_scenarios_id=" + scenarioId
				+ " AND t.test_scenarios_id=ts.test_scenarios_id "
				+ "AND ts.client_project_id=" + clientProjectId
				+ " GROUP BY t.test_scenarios_id, t.test_step_id, t.test_step_sequence, "
				+ "t.step_keyword,t.scenario_outline_key_value "
				+ "ORDER BY t.test_scenarios_id, t.test_step_sequence ASC";

		return jdbcTemplate.query(query, new RowMapper<TestScenarioStep>() {
			@Override
			public TestScenarioStep mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				TestScenarioStep testScenarioStep = new TestScenarioStep();
				testScenarioStep.setTestStepId(rs.getLong("test_step_id"));
				testScenarioStep.setTestScenarioId(scenarioId);
				testScenarioStep
						.setTestStepSequence(rs.getInt("test_step_sequence"));
				testScenarioStep.setStepKeyword(rs.getString("step_keyword"));
				testScenarioStep.setScenarioOutlineKeyValue(
						rs.getString("scenario_outline_key_value"));
				return testScenarioStep;
			}
		});
	}

	@Override
	public TestScenarioStep isDataExist(int clientProjectId, int scenarioId,
			long stepId) throws APIExceptions {
		updateDataSource();
		String query = "SELECT t.test_step_id, t.test_step_sequence, "
				+ "t.step_keyword, t.scenario_outline_key_value "
				+ "FROM test_scenario_step t, test_scenarios ts "
				+ "WHERE t.test_scenarios_id=" + scenarioId
				+ " AND t.test_step_id=" + stepId
				+ " AND t.test_scenarios_id=ts.test_scenarios_id "
				+ "AND ts.client_project_id=" + clientProjectId
				+ " GROUP BY t.test_scenarios_id, t.test_step_id, t.test_step_sequence, "
				+ "t.step_keyword, t.scenario_outline_key_value "
				+ "ORDER BY t.test_scenarios_id, t.test_step_sequence ASC";
		try {
			return jdbcTemplate.queryForObject(query,
					new RowMapper<TestScenarioStep>() {
						@Override
						public TestScenarioStep mapRow(ResultSet rs,
								int rownumber) throws SQLException {
							TestScenarioStep testScenarioStep = new TestScenarioStep();
							testScenarioStep
									.setTestStepId(rs.getLong("test_step_id"));
							testScenarioStep.setTestScenarioId(scenarioId);
							testScenarioStep.setTestStepSequence(
									rs.getInt("test_step_sequence"));
							testScenarioStep.setStepKeyword(
									rs.getString("step_keyword"));
							testScenarioStep.setScenarioOutlineKeyValue(
									rs.getString("scenario_outline_key_value"));
							return testScenarioStep;
						}
					});
		} catch (Exception e) {
//			log.info(e.getMessage());
			return null;
		}
	}

	@Override
	public TestScenarioStep getDataIfExist(int clientProjectId, int scenarioId,
			long stepId, int sequence) throws APIExceptions {
		updateDataSource();
		String query = "SELECT t.test_scenario_step_id, t.test_step_id, t.test_step_sequence, "
				+ "t.step_keyword, t.scenario_outline_key_value "
				+ "FROM test_scenario_step t, test_scenarios ts "
				+ " WHERE t.test_scenarios_id=" + scenarioId
				+ " AND t.test_step_id=" + stepId + " AND t.test_step_sequence="
				+ sequence + " AND t.test_scenarios_id=ts.test_scenarios_id "
				+ "AND ts.client_project_id= " + clientProjectId
				+ " ORDER BY t.test_scenarios_id, t.test_step_sequence ASC";
		try {
			return jdbcTemplate.queryForObject(query,
					new RowMapper<TestScenarioStep>() {
						@Override
						public TestScenarioStep mapRow(ResultSet rs,
								int rownumber) throws SQLException {
							TestScenarioStep testScenarioStep = new TestScenarioStep();
							testScenarioStep.setTestScenarioStepId(
									rs.getLong("test_scenario_step_id"));
							testScenarioStep
									.setTestStepId(rs.getLong("test_step_id"));
							testScenarioStep.setTestScenarioId(scenarioId);
							testScenarioStep.setTestStepSequence(
									rs.getInt("test_step_sequence"));
							testScenarioStep.setStepKeyword(
									rs.getString("step_keyword"));
							testScenarioStep.setScenarioOutlineKeyValue(
									rs.getString("scenario_outline_key_value"));
							return testScenarioStep;
						}
					});
		} catch (Exception e) {
			log.info(e.getMessage());
			return null;
		}
	}

	@Override
	public List<TestScenarioStep> getDataIfExist(int clientProjectId,
			int scenarioId, long stepId, int sequence, String condition)
			throws APIExceptions {
		updateDataSource();
		String query = "SELECT t.test_scenario_step_id, t.test_step_id, t.test_step_sequence, "
				+ "t.step_keyword, t.scenario_outline_key_value, t.created_by,"
				+ "t.modified_by FROM test_scenario_step t, test_scenarios ts, "
				+ "client_projects c WHERE t.test_scenarios_id=" + scenarioId
				+ " AND t.test_step_id=" + stepId;
		if (condition.equals(
				PintailerConstants.STEP_SEQUENCE_UPDATE_CONDITION_LESSER)) {
			query += " AND t.test_step_sequence<" + sequence;
		} else if (condition.equals(
				PintailerConstants.STEP_SEQUENCE_UPDATE_CONDITION_GREATER)) {
			query += " AND t.test_step_sequence>" + sequence;
		} else if (condition.equals(
				PintailerConstants.STEP_SEQUENCE_UPDATE_CONDITION_EQUAL)) {
			query += " AND t.test_step_sequence=" + sequence;
		}

		query += " AND t.test_scenarios_id=ts.test_scenarios_id "
				+ "AND ts.client_project_id= " + clientProjectId
				+ " ORDER BY t.test_scenarios_id, t.test_step_sequence ASC";
		try {
			return jdbcTemplate.query(query, new RowMapper<TestScenarioStep>() {
				@Override
				public TestScenarioStep mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					TestScenarioStep testScenarioStep = new TestScenarioStep();
					testScenarioStep.setTestScenarioStepId(
							rs.getLong("test_scenario_step_id"));
					testScenarioStep.setTestStepId(rs.getLong("test_step_id"));
					testScenarioStep.setTestScenarioId(scenarioId);
					testScenarioStep.setTestStepSequence(
							rs.getInt("test_step_sequence"));
					testScenarioStep
							.setStepKeyword(rs.getString("step_keyword"));
					testScenarioStep.setScenarioOutlineKeyValue(
							rs.getString("scenario_outline_key_value"));
//					testScenarioStep.setCreatedBy(rs.getString("created_by"));
//					testScenarioStep.setModifiedBy(rs.getString("modified_by"));
					return testScenarioStep;
				}
			});
		} catch (Exception e) {
			log.info(e.getMessage());
			return null;
		}
	}

	@Override
	public List<TestScenarioStep> getDataIfExist(int clientProjectId,
			int scenarioId, long stepId) throws APIExceptions {
		updateDataSource();
		String query = "SELECT t.test_scenario_step_id,t.test_step_id, "
				+ "t.test_step_sequence, t.step_keyword, "
				+ "t.scenario_outline_key_value FROM test_scenario_step t, "
				+ "test_scenarios ts WHERE t.test_scenarios_id=" + scenarioId
				+ " AND t.test_step_id=" + stepId
				+ " AND t.test_scenarios_id=ts.test_scenarios_id "
				+ "AND ts.client_project_id=" + clientProjectId
				+ " GROUP BY t.test_scenario_step_id, t.test_scenarios_id, "
				+ "t.test_step_id, t.test_step_sequence, "
				+ "t.step_keyword, t.scenario_outline_key_value "
				+ "ORDER BY t.test_step_sequence ASC";
		try {
			return jdbcTemplate.query(query, new RowMapper<TestScenarioStep>() {
				@Override
				public TestScenarioStep mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					TestScenarioStep testScenarioStep = new TestScenarioStep();
					testScenarioStep.setTestScenarioStepId(
							rs.getLong("test_scenario_step_id"));
					testScenarioStep.setTestStepId(rs.getLong("test_step_id"));
					testScenarioStep.setTestScenarioId(scenarioId);
					testScenarioStep.setTestStepSequence(
							rs.getInt("test_step_sequence"));
					testScenarioStep
							.setStepKeyword(rs.getString("step_keyword"));
					testScenarioStep.setScenarioOutlineKeyValue(
							rs.getString("scenario_outline_key_value"));
					return testScenarioStep;
				}
			});
		} catch (Exception e) {
			log.info(e.getMessage());
			return null;
		}
	}

	@Override
	public List<TestScenarioStepVersion> getScenarioStepMappingVersion(
			int clientProjectId, String featureFileName, String scenarioName,
			int testScenariosVersionId) throws APIExceptions {
		String hashCode = GenerateUniqueHash.getFeatureScenarioHash(
				clientProjectId, featureFileName, scenarioName);

		return getScenarioStepMappingVersion(hashCode, testScenariosVersionId);
	}

	@Override
	public List<TestScenarioStepVersion> getScenarioStepMappingVersion(
			String scenarioHashCode, int testScenariosVersionId)
			throws APIExceptions {
		updateDataSource();

		String query = "SELECT * FROM test_scenario_step_version WHERE "
				+ "test_scenarios_hashcode=?";

		if (testScenariosVersionId > 0) {
			query += " AND test_scenarios_version_id=" + testScenariosVersionId;
		}

		query += " ORDER BY version_id DESC";

		return jdbcTemplate.query(query,
				new RowMapper<TestScenarioStepVersion>() {
					@Override
					public TestScenarioStepVersion mapRow(ResultSet rs,
							int rownumber) throws SQLException {
						TestScenarioStepVersion testScenarioStepVersion = new TestScenarioStepVersion();
						testScenarioStepVersion.setTestScenarioStepVersionId(
								rs.getInt("test_scenario_step_version_id"));
						testScenarioStepVersion
								.setTestScenariosHashcode(scenarioHashCode);
						testScenarioStepVersion.setTestScenariosVersionId(
								rs.getInt("test_scenarios_version_id"));
						testScenarioStepVersion
								.setTestStepIdVersionSequenceKeyword(
										rs.getString(
												"test_step_id_version_sequence_keyword"));
						testScenarioStepVersion
								.setCreatedBy(rs.getString("created_by"));
						testScenarioStepVersion
								.setModifiedBy(rs.getString("modified_by"));
						testScenarioStepVersion.setCreatedDate(
								rs.getTimestamp("created_date"));
						testScenarioStepVersion.setModifiedDate(
								rs.getTimestamp("modified_date"));
						testScenarioStepVersion.setTestScenariosStepVersion(
								"V" + (rs.getInt("version_id") == 0 ? 1
										: rs.getInt("version_id")));
						testScenarioStepVersion.setHardDeleted(
								rs.getBoolean("is_hard_deleted"));
						return testScenarioStepVersion;
					}
				}, new Object[] { scenarioHashCode });
	}

	// For DB Operations
	@Override
	public List<TestScenarioStepVersion> getAllScenarioStepMappingVersion(
			int clientProjectId) throws APIExceptions {
		updateDataSource();

		String query = "SELECT t.* FROM test_scenario_step_version t, "
				+ "test_scenarios ts WHERE t.test_scenarios_id=ts.test_scenarios_id "
				+ "AND ts.client_project_id=? ORDER BY t.test_scenarios_id DESC";

		return jdbcTemplate.query(query,
				new RowMapper<TestScenarioStepVersion>() {
					@Override
					public TestScenarioStepVersion mapRow(ResultSet rs,
							int rownumber) throws SQLException {
						TestScenarioStepVersion testScenarioStepVersion = new TestScenarioStepVersion();
						testScenarioStepVersion.setTestScenarioStepVersionId(
								rs.getInt("test_scenario_step_version_id"));
						testScenarioStepVersion.setTestScenariosId(
								rs.getInt("test_scenarios_id"));
						testScenarioStepVersion.setTestScenariosHashcode(
								rs.getString("test_scenarios_hashcode"));
						testScenarioStepVersion.setTestScenariosVersionId(
								rs.getInt("test_scenarios_version_id"));
						testScenarioStepVersion
								.setTestStepIdVersionSequenceKeyword(
										rs.getString(
												"test_step_id_version_sequence_keyword"));
						testScenarioStepVersion
								.setCreatedBy(rs.getString("created_by"));
						testScenarioStepVersion
								.setModifiedBy(rs.getString("modified_by"));
						testScenarioStepVersion.setCreatedDate(
								rs.getTimestamp("created_date"));
						testScenarioStepVersion.setModifiedDate(
								rs.getTimestamp("modified_date"));
						testScenarioStepVersion.setTestScenariosStepVersion(
								"V" + (rs.getInt("version_id") == 0 ? 1
										: rs.getInt("version_id")));
						testScenarioStepVersion.setHardDeleted(
								rs.getBoolean("is_hard_deleted"));
						return testScenarioStepVersion;
					}
				});
	}

	@Override
	public TestScenarioStepVersion getScenarioStepMappingVersion(
			String scenarioHashCode, int testScenariosVersionId,
			int testStepScenarioVersionId) throws APIExceptions {
		updateDataSource();

		String query = "SELECT * FROM test_scenario_step_version WHERE "
				+ "test_scenarios_hashcode=? AND version_id=?";

		if (testScenariosVersionId > 0) {
			query += " AND test_scenarios_version_id=" + testScenariosVersionId;
		}

		query += " ORDER BY version_id DESC";
		try {
			return jdbcTemplate.queryForObject(query,
					new RowMapper<TestScenarioStepVersion>() {
						@Override
						public TestScenarioStepVersion mapRow(ResultSet rs,
								int rownumber) throws SQLException {
							TestScenarioStepVersion testScenarioStepVersion = new TestScenarioStepVersion();
							testScenarioStepVersion
									.setTestScenarioStepVersionId(rs.getInt(
											"test_scenario_step_version_id"));
							testScenarioStepVersion
									.setTestScenariosHashcode(scenarioHashCode);
							testScenarioStepVersion.setTestScenariosVersionId(
									rs.getInt("test_scenarios_version_id"));
							testScenarioStepVersion
									.setTestStepIdVersionSequenceKeyword(
											rs.getString(
													"test_step_id_version_sequence_keyword"));
							testScenarioStepVersion
									.setCreatedBy(rs.getString("created_by"));
							testScenarioStepVersion
									.setModifiedBy(rs.getString("modified_by"));
							testScenarioStepVersion.setCreatedDate(
									rs.getTimestamp("created_date"));
							testScenarioStepVersion.setModifiedDate(
									rs.getTimestamp("modified_date"));
							testScenarioStepVersion.setTestScenariosStepVersion(
									"V" + (rs.getInt("version_id") == 0 ? 1
											: rs.getInt("version_id")));
							testScenarioStepVersion.setHardDeleted(
									rs.getBoolean("is_hard_deleted"));
							return testScenarioStepVersion;
						}
					}, new Object[] { scenarioHashCode,
							testStepScenarioVersionId });
		} catch (EmptyResultDataAccessException ex) {
			log.info("Version data is not found for scenario with hash code ["
					+ scenarioHashCode + "], scenario version ["
					+ testScenariosVersionId
					+ "] and test scenario step mapping version ["
					+ testStepScenarioVersionId + "]");
			return null;
		} catch (Exception e) {
			log.info(e.getMessage());
			return null;
		}
	}

	@Override
	public TestScenarioStepVersion getScenarioStepMappingVersion(
			int testScenarioStepVersionId) throws APIExceptions {
		updateDataSource();

		String query = "SELECT * FROM test_scenario_step_version WHERE "
				+ "test_scenario_step_version_id=?";
		try {
			return jdbcTemplate.queryForObject(query,
					new RowMapper<TestScenarioStepVersion>() {
						@Override
						public TestScenarioStepVersion mapRow(ResultSet rs,
								int rownumber) throws SQLException {
							TestScenarioStepVersion testScenarioStepVersion = new TestScenarioStepVersion();
							testScenarioStepVersion
									.setTestScenarioStepVersionId(rs.getInt(
											"test_scenario_step_version_id"));
							testScenarioStepVersion.setTestScenariosId(
									rs.getInt("test_scenarios_id"));
							testScenarioStepVersion.setTestScenariosHashcode(
									rs.getString("test_scenarios_hashcode"));
							testScenarioStepVersion.setTestScenariosVersionId(
									rs.getInt("test_scenarios_version_id"));
							testScenarioStepVersion
									.setTestStepIdVersionSequenceKeyword(
											rs.getString(
													"test_step_id_version_sequence_keyword"));
							testScenarioStepVersion
									.setCreatedBy(rs.getString("created_by"));
							testScenarioStepVersion
									.setModifiedBy(rs.getString("modified_by"));
							testScenarioStepVersion.setCreatedDate(
									rs.getTimestamp("created_date"));
							testScenarioStepVersion.setModifiedDate(
									rs.getTimestamp("modified_date"));
							testScenarioStepVersion.setTestScenariosStepVersion(
									"V" + (rs.getInt("version_id") == 0 ? 1
											: rs.getInt("version_id")));
							testScenarioStepVersion.setHardDeleted(
									rs.getBoolean("is_hard_deleted"));
							return testScenarioStepVersion;
						}
					}, testScenarioStepVersionId);
		} catch (EmptyResultDataAccessException ex) {
			log.info(
					"Version data is not found for test scenario step mapping version");
			return null;
		} catch (Exception e) {
			log.info(e.getMessage());
			return null;
		}
	}

	@Override
	public List<TestScenarioStepVersion> getScenarioStepMappingVersion(
			int testScenariosId, int testScenariosVersionId)
			throws APIExceptions {
		updateDataSource();

		String query = "SELECT * FROM test_scenario_step_version WHERE "
				+ "test_scenarios_id=? ";
		if (testScenariosVersionId > 0) {
			query += "AND test_scenarios_version_id= " + testScenariosVersionId;
		}
		query += "ORDER BY version_id DESC";
		try {
			return jdbcTemplate.query(query,
					new RowMapper<TestScenarioStepVersion>() {
						@Override
						public TestScenarioStepVersion mapRow(ResultSet rs,
								int rownumber) throws SQLException {
							TestScenarioStepVersion testScenarioStepVersion = new TestScenarioStepVersion();
							testScenarioStepVersion
									.setTestScenarioStepVersionId(rs.getInt(
											"test_scenario_step_version_id"));
							testScenarioStepVersion.setTestScenariosId(
									rs.getInt("test_scenarios_id"));
							testScenarioStepVersion.setTestScenariosHashcode(
									rs.getString("test_scenarios_hashcode"));
							testScenarioStepVersion.setTestScenariosVersionId(
									rs.getInt("test_scenarios_version_id"));
							testScenarioStepVersion
									.setTestStepIdVersionSequenceKeyword(
											rs.getString(
													"test_step_id_version_sequence_keyword"));
							testScenarioStepVersion
									.setCreatedBy(rs.getString("created_by"));
							testScenarioStepVersion
									.setModifiedBy(rs.getString("modified_by"));
							testScenarioStepVersion.setCreatedDate(
									rs.getTimestamp("created_date"));
							testScenarioStepVersion.setModifiedDate(
									rs.getTimestamp("modified_date"));
							testScenarioStepVersion.setTestScenariosStepVersion(
									"V" + (rs.getInt("version_id") == 0 ? 1
											: rs.getInt("version_id")));
							testScenarioStepVersion.setHardDeleted(
									rs.getBoolean("is_hard_deleted"));
							return testScenarioStepVersion;
						}
					}, testScenariosId);
		} catch (EmptyResultDataAccessException ex) {
			log.info("Version data is not found for scenario id ["
					+ testScenariosId + "], scenario version ["
					+ testScenariosVersionId + "]");
			return null;
		} catch (Exception e) {
			log.info(e.getMessage());
			return null;
		}
	}

	@Override
	public TestScenarioStepVersion persistTestScenarioStepVersion(
			TestScenarioStepVersion testScenarioStepVersion)
			throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO test_scenario_step_version (test_scenarios_id, "
					+ "test_scenarios_hashcode, test_scenarios_version_id, "
					+ "test_step_id_version_sequence_keyword, "
					+ "created_by, modified_by, is_hard_deleted) "
					+ "VALUES(?,?,?,?,?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "test_scenario_step_version_id" });
					ps.setInt(1, testScenarioStepVersion.getTestScenariosId());
					ps.setString(2,
							testScenarioStepVersion.getTestScenariosHashcode());
					ps.setInt(3, testScenarioStepVersion
							.getTestScenariosVersionId());
					ps.setString(4, testScenarioStepVersion
							.getTestStepIdVersionSequenceKeyword());
					ps.setString(5, currentUser);
					ps.setString(6, currentUser);
//					ps.setString(6, testScenarioStepVersion
//							.getTestScenariosStepVersion());
					ps.setBoolean(7, false);
					return ps;
				}
			}, requestKeyHolder);
			int logEntityId = requestKeyHolder.getKey().intValue();
			testScenarioStepVersion.setTestScenarioStepVersionId(logEntityId);
			return testScenarioStepVersion;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public TestScenarioStepVersion isVersionDataExist(String scenarioHashCode,
			int testScenariosVersionId, String testStepInfo)
			throws APIExceptions {
		updateDataSource();

		String query = "SELECT * FROM test_scenario_step_version WHERE "
				+ "test_scenarios_hashcode=? AND test_scenarios_version_id=? "
				+ "AND test_step_id_version_sequence_keyword=? "
				+ "ORDER BY version_id DESC";

		return jdbcTemplate.queryForObject(query,
				new RowMapper<TestScenarioStepVersion>() {
					@Override
					public TestScenarioStepVersion mapRow(ResultSet rs,
							int rownumber) throws SQLException {
						TestScenarioStepVersion testScenarioStepVersion = new TestScenarioStepVersion();
						testScenarioStepVersion.setTestScenarioStepVersionId(
								rs.getInt("test_scenario_step_version_id"));
						testScenarioStepVersion
								.setTestScenariosHashcode(scenarioHashCode);
						testScenarioStepVersion.setTestScenariosVersionId(
								rs.getInt("test_scenarios_version_id"));
						testScenarioStepVersion
								.setTestStepIdVersionSequenceKeyword(
										rs.getString(
												"test_step_id_version_sequence_keyword"));
						testScenarioStepVersion
								.setCreatedBy(rs.getString("created_by"));
						testScenarioStepVersion
								.setModifiedBy(rs.getString("modified_by"));
						testScenarioStepVersion.setCreatedDate(
								rs.getTimestamp("created_date"));
						testScenarioStepVersion.setModifiedDate(
								rs.getTimestamp("modified_date"));
						testScenarioStepVersion.setTestScenarioStepVersionId(
								rs.getInt("version_id"));
						testScenarioStepVersion.setHardDeleted(
								rs.getBoolean("is_hard_deleted"));
						return testScenarioStepVersion;
					}
				}, new Object[] { scenarioHashCode, testScenariosVersionId,
						testStepInfo });
	}

	@Override
	public List<TestScenarioStepVersion> getScenarioStepVersionData(
			String scenarioHashCode, int testScenariosVersionId)
			throws APIExceptions {
		updateDataSource();

		String query = "SELECT * FROM test_scenario_step_version WHERE "
				+ "test_scenarios_hashcode=? AND test_scenarios_version_id=? "
				+ "ORDER BY version_id DESC";

		return jdbcTemplate.query(query,
				new RowMapper<TestScenarioStepVersion>() {
					@Override
					public TestScenarioStepVersion mapRow(ResultSet rs,
							int rownumber) throws SQLException {
						TestScenarioStepVersion testScenarioStepVersion = new TestScenarioStepVersion();
						testScenarioStepVersion.setTestScenarioStepVersionId(
								rs.getInt("test_scenario_step_version_id"));
						testScenarioStepVersion
								.setTestScenariosHashcode(scenarioHashCode);
						testScenarioStepVersion.setTestScenariosVersionId(
								rs.getInt("test_scenarios_version_id"));
						testScenarioStepVersion
								.setTestStepIdVersionSequenceKeyword(
										rs.getString(
												"test_step_id_version_sequence_keyword"));
						testScenarioStepVersion
								.setCreatedBy(rs.getString("created_by"));
						testScenarioStepVersion
								.setModifiedBy(rs.getString("modified_by"));
						testScenarioStepVersion.setCreatedDate(
								rs.getTimestamp("created_date"));
						testScenarioStepVersion.setModifiedDate(
								rs.getTimestamp("modified_date"));
						testScenarioStepVersion.setTestScenarioStepVersionId(
								rs.getInt("version_id"));
						testScenarioStepVersion.setHardDeleted(
								rs.getBoolean("is_hard_deleted"));
						return testScenarioStepVersion;
					}
				}, new Object[] { scenarioHashCode, testScenariosVersionId });
	}

	@Override
	public FeatureVersion persistFeatureVersion(FeatureVersion featureVersion)
			throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO feature_version (client_project_id, feature_name, "
					+ "test_scenario_hash_version, created_by, modified_by, "
					+ "is_hard_deleted) VALUES(?,?,?,?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "feature_version_id" });
					ps.setInt(1, featureVersion.getClientProjectId());
					ps.setString(2, featureVersion.getFeatureFileName());
					ps.setString(3,
							featureVersion.getTestScenariosHashVersionInfo());
					ps.setString(4, currentUser);
					ps.setString(5, currentUser);
//					ps.setString(6, testScenarioStepVersion
//							.getTestScenariosStepVersion());
					ps.setBoolean(6, false);
					return ps;
				}
			}, requestKeyHolder);
			int logEntityId = requestKeyHolder.getKey().intValue();
			featureVersion.setFeatureVersionId(logEntityId);
			return featureVersion;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public List<FeatureVersion> getFeatureVersion(int clientProjectId,
			String featureFileName) throws APIExceptions {
		updateDataSource();

		String query = "SELECT * FROM feature_version WHERE client_project_id=?"
				+ " AND feature_name=? ORDER BY version_id DESC";

		return jdbcTemplate.query(query, new RowMapper<FeatureVersion>() {
			@Override
			public FeatureVersion mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				FeatureVersion featureVersion = new FeatureVersion();
				featureVersion
						.setFeatureVersionId(rs.getInt("feature_version_id"));
				featureVersion.setFeatureFileName(rs.getString("feature_name"));
				featureVersion.setTestScenariosHashVersionInfo(
						rs.getString("test_scenario_hash_version"));
				featureVersion.setCreatedBy(rs.getString("created_by"));
				featureVersion.setModifiedBy(rs.getString("modified_by"));
				featureVersion.setCreatedDate(rs.getTimestamp("created_date"));
				featureVersion
						.setModifiedDate(rs.getTimestamp("modified_date"));
				featureVersion.setFeatureVersion("V" + rs.getInt("version_id"));
				featureVersion.setHardDeleted(rs.getBoolean("is_hard_deleted"));
				return featureVersion;
			}
		}, new Object[] { clientProjectId, featureFileName });
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
