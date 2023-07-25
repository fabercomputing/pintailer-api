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

import com.fw.dao.ITestScenarioStepManager;
import com.fw.dao.ITestScenariosManager;
import com.fw.dao.ITestStepManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.TestScenarios;
import com.fw.domain.TestScenariosVersion;
import com.fw.exceptions.APIExceptions;
import com.fw.utils.ApplicationCommonUtil;
import com.fw.utils.GenerateUniqueHash;
import com.fw.utils.ValueValidations;

@Repository
public class TestScenariosManagerImpl implements ITestScenariosManager {

	private Logger log = Logger.getLogger(TestScenariosManagerImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	ITestScenarioStepManager testScenariosStepManager;

	@Autowired
	ITestStepManager testStepManager;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	public TestScenarios persistTestScenarios(TestScenarios testScenarios)
			throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO test_scenarios (name, feature_file_name, "
					+ "created_by, modified_by, hashcode, client_project_id, "
					+ "scenario_tags, isbackground, isfeaturedescription, scenario_sequence) "
					+ " VALUES(?,?,?,?,?,?,?,?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "test_scenarios_id" });
					ps.setString(1, testScenarios.getName());
					ps.setString(2, testScenarios.getFeatureFileName());
					ps.setString(3, currentUser);
					ps.setString(4, currentUser);
					ps.setString(5, testScenarios.getHashCode());
					ps.setLong(6, testScenarios.getClientProjectId());
					ps.setString(7, testScenarios.getScenarioTag());
					ps.setBoolean(8, testScenarios.isBackground());
					ps.setBoolean(9, testScenarios.isFeature());
					ps.setInt(10, testScenarios.getScenarioSequence());
					return ps;
				}
			}, requestKeyHolder);
			int logEntityId = requestKeyHolder.getKey().intValue();
			testScenarios.setTestScenarioId(logEntityId);
			return testScenarios;
		} catch (DataAccessException e) {
			String message = "Error occured while creating the scenario ["
					+ testScenarios.getName() + "] : ";
			if (e.getMessage().toLowerCase().contains("duplicate key")) {
				message += " Data already exist in DB and duplicates are not allowed.";
			} else {
				message += e.getMessage();
			}

			log.error(message);
			throw new APIExceptions(message);
		}
	}

	@Override
	public int updateTestScenariosById(TestScenarios testScenarios)
			throws APIExceptions {
		updateDataSource();
		String sql = "UPDATE test_scenarios SET name=?, feature_file_name=?,"
				+ " modified_by=?, hashcode=?, client_project_id=?,"
				+ " scenario_tags=?, isbackground=?, isfeaturedescription=?,"
				+ " scenario_sequence=?, is_deleted=? WHERE test_scenarios_id=?";
		if (!ValueValidations.isValueValid("" + testScenarios.isDeleted())) {
			testScenarios.setDeleted(false);
		}
		try {
			return jdbcTemplate.update(sql, new Object[] {
					testScenarios.getName(), testScenarios.getFeatureFileName(),
					applicationCommonUtil.getCurrentUser(),
					testScenarios.getHashCode(),
					testScenarios.getClientProjectId(),
					testScenarios.getScenarioTag(),
					testScenarios.isBackground(), testScenarios.isFeature(),
					testScenarios.getScenarioSequence(),
					testScenarios.isDeleted(),
					testScenarios.getTestScenarioId() });
		} catch (Exception e) {
			log.error("Error occured while updating the scenarios : "
					+ e.getMessage());
			return 0;
		}
	}

	@Override
	public void updateTestScenariosSequenceById(TestScenarios testScenarios)
			throws APIExceptions {
		updateDataSource();
		String sql = "UPDATE test_scenarios SET scenario_sequence=? "
				+ "WHERE test_scenarios_id=?";
		jdbcTemplate.update(sql, testScenarios.getScenarioSequence(),
				testScenarios.getTestScenarioId());
	}

//	@Override
//	public void deleteTestScenariosById(int testScenariosId)
//			throws APIExceptions {
//		updateDataSource();
//		String sql = "DELETE FROM test_scenarios WHERE test_scenarios_id=?";
//		try {
//			jdbcTemplate.update(sql, testScenariosId);
//		} catch (DataAccessException e) {
//			String message = e.getMessage();
//			if (message.contains("foreign key constraint")) {
//				message = "The dependent data is not removed from DB.";
//			}
//			log.error(e.getMessage());
//			throw new APIExceptions(
//					"Error occured while deleting the test scenario : "
//							+ message);
//		}
//	}

	@Override
	public TestScenarios getTestScenariosById(int testScenariosId,
			String isDeleted) throws APIExceptions {
		updateDataSource();
		String qry = "SELECT v.version_id, t.* FROM fw_test_mgmt.test_scenarios t "
				+ "LEFT JOIN fw_test_mgmt.test_scenarios_version v "
				+ "ON t.hashcode=v.hashcode AND v.version_id=(SELECT MAX(version_id) "
				+ "FROM fw_test_mgmt.test_scenarios_version WHERE hashcode= t.hashcode) "
				+ "WHERE t.test_scenarios_id=? ";

		if (!(!ValueValidations.isValueValid(isDeleted)
				|| isDeleted.equalsIgnoreCase("all"))) {
			qry += "AND t.is_deleted=" + Boolean.valueOf(isDeleted);
		}
		qry += " ORDER BY t.scenario_sequence ASC";

		try {
			return jdbcTemplate.queryForObject(qry,
					new RowMapper<TestScenarios>() {
						@Override
						public TestScenarios mapRow(ResultSet rs, int rownumber)
								throws SQLException {
							TestScenarios testScenarios = new TestScenarios();
							testScenarios.setTestScenarioId(
									rs.getInt("test_scenarios_id"));
							testScenarios.setName(rs.getString("name"));
							testScenarios.setFeatureFileName(
									rs.getString("feature_file_name"));
							testScenarios.setHashCode(rs.getString("hashcode"));
							testScenarios.setClientProjectId(
									rs.getInt("client_project_id"));
							testScenarios
									.setCreatedBy(rs.getString("created_by"));
							testScenarios.setScenarioTag(
									rs.getString("scenario_tags"));
							testScenarios.setBackground(
									rs.getBoolean("isbackground"));
							testScenarios.setFeature(
									rs.getBoolean("isfeaturedescription"));
							testScenarios.setScenarioSequence(
									rs.getInt("scenario_sequence"));
							testScenarios.setScenarioLatestVersion(
									"V" + (rs.getInt("version_id") == 0 ? 1
											: rs.getInt("version_id")));
							testScenarios.setScenarioSelectedVersion(
									testScenarios.getScenarioLatestVersion());
							return testScenarios;
						}
					}, testScenariosId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public TestScenarios getTestScenariosByHashCode(
			String testScenariosHashCode) throws APIExceptions {
		updateDataSource();
		String qry = "SELECT v.version_id, t.* FROM fw_test_mgmt.test_scenarios t, "
				+ "fw_test_mgmt.test_scenarios_version v WHERE t.is_deleted=false"
				+ " AND t.hashcode=? AND t.hashcode=v.hashcode AND v.version_id="
				+ "(SELECT MAX(version_id) FROM fw_test_mgmt.test_scenarios_version "
				+ "WHERE hashcode= t.hashcode) ORDER BY t.scenario_sequence ASC";
		try {
			return jdbcTemplate.queryForObject(qry,
					new RowMapper<TestScenarios>() {
						@Override
						public TestScenarios mapRow(ResultSet rs, int rownumber)
								throws SQLException {
							TestScenarios testScenarios = new TestScenarios();
							testScenarios.setTestScenarioId(
									rs.getInt("test_scenarios_id"));
							testScenarios.setName(rs.getString("name"));
							testScenarios.setFeatureFileName(
									rs.getString("feature_file_name"));
							testScenarios.setHashCode(rs.getString("hashcode"));
							testScenarios.setClientProjectId(
									rs.getInt("client_project_id"));
							testScenarios
									.setCreatedBy(rs.getString("created_by"));
							testScenarios.setScenarioTag(
									rs.getString("scenario_tags"));
							testScenarios.setBackground(
									rs.getBoolean("isbackground"));
							testScenarios.setFeature(
									rs.getBoolean("isfeaturedescription"));
							testScenarios.setScenarioSequence(
									rs.getInt("scenario_sequence"));
							testScenarios.setScenarioLatestVersion(
									"V" + (rs.getInt("version_id") == 0 ? 1
											: rs.getInt("version_id")));
							testScenarios.setScenarioSelectedVersion(
									testScenarios.getScenarioLatestVersion());
							return testScenarios;
						}
					}, testScenariosHashCode);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	// Added for DB operations on all rows i.e. update hash code
	@Override
	public List<TestScenarios> getAllTestScenarios() throws APIExceptions {
		updateDataSource();
		try {
			return jdbcTemplate.query("SELECT * FROM test_scenarios",
					new RowMapper<TestScenarios>() {
						@Override
						public TestScenarios mapRow(ResultSet rs, int rownumber)
								throws SQLException {
							TestScenarios testScenarios = new TestScenarios();
							testScenarios.setTestScenarioId(
									rs.getInt("test_scenarios_id"));
							testScenarios.setName(rs.getString("name"));
							testScenarios.setFeatureFileName(
									rs.getString("feature_file_name"));
							testScenarios.setHashCode(rs.getString("hashcode"));
							testScenarios.setClientProjectId(
									rs.getInt("client_project_id"));
							testScenarios.setScenarioTag(
									rs.getString("scenario_tags"));
							testScenarios.setBackground(
									rs.getBoolean("isbackground"));
							testScenarios.setFeature(
									rs.getBoolean("isfeaturedescription"));
							testScenarios.setScenarioSequence(
									rs.getInt("scenario_sequence"));
							testScenarios
									.setCreatedBy(rs.getString("created_by"));
							testScenarios
									.setModifiedBy(rs.getString("modified_by"));
							return testScenarios;
						}
					});
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public List<TestScenarios> getTestScenariosByFeatureName(
			int clientProjectId, String featureName) throws APIExceptions {
		updateDataSource();
//		String qry = "SELECT test_scenarios_id, name, hashcode, scenario_sequence "
//				+ "FROM test_scenarios WHERE client_project_id=? "
//				+ "AND feature_file_name=? AND isfeaturedescription=false "
//				+ "ORDER BY scenario_sequence";

		String qry = "SELECT v.version_id, t.* FROM fw_test_mgmt.test_scenarios t "
				+ "LEFT JOIN fw_test_mgmt.test_scenarios_version v ON "
				+ "t.hashcode=v.hashcode AND v.version_id=(SELECT MAX(version_id) "
				+ "FROM fw_test_mgmt.test_scenarios_version WHERE hashcode= t.hashcode)"
				+ "WHERE t.is_deleted=false AND t.client_project_id=? "
				+ "AND t.feature_file_name=? ORDER BY t.scenario_sequence ASC";

		return jdbcTemplate.query(qry, new RowMapper<TestScenarios>() {
			@Override
			public TestScenarios mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				TestScenarios testScenarios = new TestScenarios();
				testScenarios.setTestScenarioId(rs.getInt("test_scenarios_id"));
				testScenarios.setName(rs.getString("name"));
				testScenarios.setFeatureFileName(featureName);
				testScenarios.setHashCode(rs.getString("hashcode"));
				testScenarios
						.setClientProjectId(rs.getInt("client_project_id"));
				testScenarios.setCreatedBy(rs.getString("created_by"));
				testScenarios.setScenarioTag(rs.getString("scenario_tags"));
				testScenarios.setBackground(rs.getBoolean("isbackground"));
				testScenarios.setFeature(rs.getBoolean("isfeaturedescription"));

				testScenarios
						.setScenarioSequence(rs.getInt("scenario_sequence"));
				testScenarios.setScenarioLatestVersion(
						"V" + (rs.getInt("version_id") == 0 ? 1
								: rs.getInt("version_id")));
				testScenarios.setScenarioSelectedVersion(
						testScenarios.getScenarioLatestVersion());
				return testScenarios;
			}
		}, new Object[] { clientProjectId, featureName });
	}

	@Override
	public int getTestScenariosIdByHashCode(int clientProjectId,
			String hashCode) throws APIExceptions {
		updateDataSource();
		try {
			String sql = "SELECT test_scenarios_id FROM test_scenarios "
					+ "WHERE hashcode ='" + hashCode
					+ "' AND client_project_id=" + clientProjectId;
			return jdbcTemplate.query(sql, new ResultSetExtractor<Integer>() {
				@Override
				public Integer extractData(ResultSet rs)
						throws SQLException, DataAccessException {
					if (rs.next()) {
						return rs.getInt("test_scenarios_id");
					} else {
						return 0;
					}
				}
			});
		} catch (EmptyResultDataAccessException e) {
			log.info("Given hashcode of scenario does not exist : "
					+ e.getMessage());
			return 0;
		} catch (DataAccessException e) {
			log.error("Error : " + e.getMessage(), e);
			throw new APIExceptions(e.getMessage());
		}
	}

	@Override
	public List<String> getFeatureFileList(int clientProjectId)
			throws APIExceptions {
		updateDataSource();
		return jdbcTemplate.query(
				"SELECT DISTINCT(feature_file_name) FROM test_scenarios "
						+ "WHERE client_project_id=" + clientProjectId
						+ " ORDER BY feature_file_name",
				new RowMapper<String>() {
					@Override
					public String mapRow(ResultSet rs, int rownumber)
							throws SQLException {
						return rs.getString("feature_file_name");
					}
				});
	}

	@Override
	public int getTestScenarioIdByScenarioAndFeatureFile(int clientProjectId,
			String scenarioName, String featureFileName) throws APIExceptions {
		String hashCode = GenerateUniqueHash.getFeatureScenarioHash(
				clientProjectId, featureFileName, scenarioName);
		return getTestScenariosIdByHashCode(clientProjectId, hashCode);
	}

	@Override
	public Map<String, Integer> getTestScenarioHashCode(int clientProjectId)
			throws APIExceptions {
		return getTestScenarioHashCode(clientProjectId, null);
	}

	@Override
	public Map<String, Integer> getTestScenarioHashCode(int clientProjectId,
			String featureFileName) throws APIExceptions {
		updateDataSource();
		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		String sql = "SELECT hashcode,test_scenarios_id FROM test_scenarios "
				+ "WHERE client_project_id=" + clientProjectId;
		if (!(null == featureFileName || featureFileName.trim().equals(""))) {
			sql += " AND feature_file_name='" + featureFileName + "'";
		}
		try {
			return jdbcTemplate.query(sql,
					new ResultSetExtractor<Map<String, Integer>>() {
						@Override
						public Map<String, Integer> extractData(ResultSet rs)
								throws SQLException, DataAccessException {
							while (rs.next()) {
								result.put(rs.getString("hashcode"),
										rs.getInt("test_scenarios_id"));
							}
							return result;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<TestScenarios> getEntireFeature(int clientProjectId,
			String featureName) throws APIExceptions {
		updateDataSource();
//		String qry = "SELECT test_scenarios_id, name, hashcode, scenario_tags, "
//				+ "isbackground, isfeaturedescription, created_by, modified_by "
//				+ "FROM test_scenarios WHERE is_deleted=false "
//				+ "AND client_project_id=" + clientProjectId
//				+ " AND feature_file_name='" + featureName
//				+ "' ORDER BY t.scenario_sequence";
		String qry = "SELECT v.version_id, t.test_scenarios_id, t.name, "
				+ "t.feature_file_name, t.hashcode, t.scenario_tags, "
				+ "t.isbackground, t.isfeaturedescription, t.created_by, "
				+ "t.modified_by, t.created_date, t.modified_date "
				+ "FROM fw_test_mgmt.test_scenarios t LEFT JOIN "
				+ "fw_test_mgmt.test_scenarios_version v ON t.hashcode=v.hashcode "
				+ "AND v.version_id=(SELECT MAX(version_id) FROM "
				+ "fw_test_mgmt.test_scenarios_version WHERE hashcode=t.hashcode) "
				+ "WHERE t.is_deleted=false AND t.client_project_id="
				+ clientProjectId + " AND t.feature_file_name='" + featureName
				+ "' ORDER BY t.scenario_sequence ASC";
		return jdbcTemplate.query(qry, new RowMapper<TestScenarios>() {
			@Override
			public TestScenarios mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				TestScenarios testScenarios = new TestScenarios();
				testScenarios.setTestScenarioId(rs.getInt("test_scenarios_id"));
				testScenarios.setName(rs.getString("name"));
				testScenarios.setHashCode(rs.getString("hashcode"));
				testScenarios.setFeatureFileName(featureName);
				testScenarios.setClientProjectId(clientProjectId);
				testScenarios.setScenarioTag(rs.getString("scenario_tags"));
				testScenarios.setBackground(rs.getBoolean("isbackground"));
				testScenarios.setFeature(rs.getBoolean("isfeaturedescription"));
				testScenarios.setCreatedBy(rs.getString("created_by"));
				testScenarios.setModifiedBy(rs.getString("modified_by"));
				testScenarios.setScenarioLatestVersion(
						"V" + (rs.getInt("version_id") == 0 ? 1
								: rs.getInt("version_id")));
				testScenarios.setScenarioSelectedVersion(
						testScenarios.getScenarioLatestVersion());
				try {
					if (!testScenarios.isFeature()) {
						// testScenarios.setTestStepsList(testStepManager
						// .getTestStepsByIds(testScenariosStepManager
						// .getTestStepIdByScenarioId(
						// clientOrganization,
						// clientProjectId,
						// testScenarios.getTestScenarioId())));
						testScenarios.setTestStepsList(
								testStepManager.getTestStepsByScenarioId(
										testScenarios.getTestScenarioId()));
					}
				} catch (APIExceptions e) {
					String message = "Error occurd while fetching the list of "
							+ "test steps in a scenario for editing the feature "
							+ "file : " + e.getMessage();
					log.error(message);
					throw new SQLException(message);
				}
				return testScenarios;
			}
		});
	}

	@Override
	public List<TestScenariosVersion> getTestScenariosVersionByHashCode(
			int clientProjectId, String featureFileName, String scenarioName)
			throws APIExceptions {
		updateDataSource();
		String hashCode = GenerateUniqueHash.getFeatureScenarioHash(
				clientProjectId, featureFileName, scenarioName);
		try {
			String sql = "SELECT * FROM test_scenarios_version WHERE test_scenarios_id=? "
					+ "ORDER BY version_id DESC";
			return jdbcTemplate.query(sql,
					new RowMapper<TestScenariosVersion>() {
						@Override
						public TestScenariosVersion mapRow(ResultSet rs,
								int rownumber)
								throws SQLException, DataAccessException {
							TestScenariosVersion testScenariosVersion = new TestScenariosVersion();
							testScenariosVersion.setTestScenariosVersionId(
									rs.getInt("test_scenarios_version_id"));
							testScenariosVersion.setTestScenarioId(
									rs.getInt("test_scenarios_id"));
							testScenariosVersion.setName(rs.getString("name"));
							testScenariosVersion
									.setHashCode(rs.getString("hashcode"));
							testScenariosVersion.setFeatureFileName(
									rs.getString("feature_file_name"));
							testScenariosVersion.setClientProjectId(
									rs.getInt("client_project_id"));
							testScenariosVersion.setScenarioTag(
									rs.getString("scenario_tags"));
							testScenariosVersion.setBackground(
									rs.getBoolean("isbackground"));
							testScenariosVersion.setFeature(
									rs.getBoolean("isfeaturedescription"));
							testScenariosVersion
									.setCreatedBy(rs.getString("created_by"));
							testScenariosVersion
									.setModifiedBy(rs.getString("modified_by"));
							testScenariosVersion.setVersionId(
									"V" + rs.getInt("version_id"));
							testScenariosVersion.setHardDeleted(
									rs.getBoolean("is_hard_deleted"));
							return testScenariosVersion;
						}
					}, hashCode);
		} catch (EmptyResultDataAccessException e) {
			log.info("Given hashcode of scenario does not exist : "
					+ e.getMessage());
			return null;
		} catch (DataAccessException e) {
			log.info("Error : " + e.getMessage(), e);
			return null;
		}
	}

	@Override
	public List<TestScenariosVersion> getTestScenariosVersionByScenarioId(
			int clientProjectId, int testScenariosId) throws APIExceptions {
		updateDataSource();
		try {
			String sql = "SELECT * FROM test_scenarios_version WHERE test_scenarios_id=? "
					+ "ORDER BY version_id DESC";
			return jdbcTemplate.query(sql,
					new RowMapper<TestScenariosVersion>() {
						@Override
						public TestScenariosVersion mapRow(ResultSet rs,
								int rownumber)
								throws SQLException, DataAccessException {
							TestScenariosVersion testScenariosVersion = new TestScenariosVersion();
							testScenariosVersion.setTestScenariosVersionId(
									rs.getInt("test_scenarios_version_id"));
							testScenariosVersion.setTestScenarioId(
									rs.getInt("test_scenarios_id"));
							testScenariosVersion.setName(rs.getString("name"));
							testScenariosVersion
									.setHashCode(rs.getString("hashcode"));
							testScenariosVersion.setFeatureFileName(
									rs.getString("feature_file_name"));
							testScenariosVersion.setClientProjectId(
									rs.getInt("client_project_id"));
							testScenariosVersion.setScenarioTag(
									rs.getString("scenario_tags"));
							testScenariosVersion.setBackground(
									rs.getBoolean("isbackground"));
							testScenariosVersion.setFeature(
									rs.getBoolean("isfeaturedescription"));
							testScenariosVersion
									.setCreatedBy(rs.getString("created_by"));
							testScenariosVersion
									.setModifiedBy(rs.getString("modified_by"));
							testScenariosVersion.setVersionId(
									"V" + rs.getInt("version_id"));
							testScenariosVersion.setHardDeleted(
									rs.getBoolean("is_hard_deleted"));
							return testScenariosVersion;
						}
					}, testScenariosId);
		} catch (EmptyResultDataAccessException e) {
			log.info("Given hashcode of scenario does not exist : "
					+ e.getMessage());
			return null;
		} catch (DataAccessException e) {
			log.info("Error : " + e.getMessage(), e);
			return null;
		}
	}

	@Override
	public TestScenarios getSpecificTestScenariosVersion(int clientProjectId,
			String scenarioHashCode, int version_id) throws APIExceptions {
		updateDataSource();
		try {
			String sql = "SELECT * FROM test_scenarios_version WHERE hashcode=? "
					+ "AND version_id=?";
			return jdbcTemplate.queryForObject(sql,
					new RowMapper<TestScenarios>() {
						@Override
						public TestScenarios mapRow(ResultSet rs, int rownumber)
								throws SQLException, DataAccessException {
							TestScenarios testScenarios = new TestScenarios();
							testScenarios.setTestScenarioId(
									rs.getInt("test_scenarios_id"));
							testScenarios.setName(rs.getString("name"));
							testScenarios.setHashCode(rs.getString("hashcode"));
							testScenarios.setFeatureFileName(
									rs.getString("feature_file_name"));
							testScenarios.setClientProjectId(
									rs.getInt("client_project_id"));
							testScenarios.setScenarioTag(
									rs.getString("scenario_tags"));
							testScenarios.setBackground(
									rs.getBoolean("isbackground"));
							testScenarios.setFeature(
									rs.getBoolean("isfeaturedescription"));
							testScenarios
									.setCreatedBy(rs.getString("created_by"));
							testScenarios
									.setModifiedBy(rs.getString("modified_by"));
							testScenarios.setScenarioSelectedVersion(
									"V" + version_id);
							return testScenarios;
						}
					}, new Object[] { scenarioHashCode, version_id });
		} catch (EmptyResultDataAccessException e) {
			log.info("Given hashcode of scenario does not exist : "
					+ e.getMessage());
			return null;
		} catch (DataAccessException e) {
			log.info("Error : " + e.getMessage(), e);
			return null;
		}
	}

	@Override
	public TestScenarios getSpecificTestScenariosVersion(int clientProjectId,
			int testScenarioId, int testScenarioVersionId)
			throws APIExceptions {
		updateDataSource();
		try {
			String sql = "SELECT * FROM test_scenarios_version WHERE "
					+ "client_project_id=? AND test_scenarios_id=? "
					+ "AND version_id=?";
			return jdbcTemplate.queryForObject(sql,
					new RowMapper<TestScenarios>() {
						@Override
						public TestScenarios mapRow(ResultSet rs, int rownumber)
								throws SQLException, DataAccessException {
							TestScenarios testScenarios = new TestScenarios();
							testScenarios.setTestScenarioId(
									rs.getInt("test_scenarios_id"));
							testScenarios.setName(rs.getString("name"));
							testScenarios.setHashCode(rs.getString("hashcode"));
							testScenarios.setFeatureFileName(
									rs.getString("feature_file_name"));
							testScenarios.setClientProjectId(
									rs.getInt("client_project_id"));
							testScenarios.setScenarioTag(
									rs.getString("scenario_tags"));
							testScenarios.setBackground(
									rs.getBoolean("isbackground"));
							testScenarios.setFeature(
									rs.getBoolean("isfeaturedescription"));
							testScenarios
									.setCreatedBy(rs.getString("created_by"));
							testScenarios
									.setModifiedBy(rs.getString("modified_by"));
							testScenarios.setScenarioSelectedVersion(
									"V" + testScenarioVersionId);
							return testScenarios;
						}
					}, new Object[] { clientProjectId, testScenarioId,
							testScenarioVersionId });
		} catch (EmptyResultDataAccessException e) {
			log.debug("Given hashcode of scenario does not exist : "
					+ e.getMessage());
			return null;
		} catch (DataAccessException e) {
			log.info("Error : " + e.getMessage(), e);
			return null;
		}
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
