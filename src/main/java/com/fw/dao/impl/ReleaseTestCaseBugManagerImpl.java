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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.fw.dao.IReleaseTestCaseBugManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.Release;
import com.fw.domain.ReleaseTestCaseBug;
import com.fw.exceptions.APIExceptions;
import com.fw.utils.ApplicationCommonUtil;
import com.fw.utils.LocalUtils;
import com.fw.utils.ValueValidations;

@Repository
public class ReleaseTestCaseBugManagerImpl
		implements IReleaseTestCaseBugManager {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	private Logger log = Logger.getLogger(ReleaseTestCaseBugManagerImpl.class);

	@Override
	public ReleaseTestCaseBug persistReleaseTestCaseBug(
			ReleaseTestCaseBug releaseTestCaseBug) throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO release_tc_bug (release_id, testcase_id, "
					+ "testcase_version_id, bug_id, bug_type, execution_env_id, "
					+ "remarks, applicable, created_by, modified_by, is_deleted) "
					+ " VALUES(?,?,?,?,?,?,?,?,?,?,?)";
			final String currentUser = applicationCommonUtil.getCurrentUser();

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "release_tc_bug_id" });
					ps.setInt(1, releaseTestCaseBug.getReleaseId());
					ps.setInt(2, releaseTestCaseBug.getTestCaseId());
					ps.setInt(3, Integer.parseInt(releaseTestCaseBug
							.getTestCaseVersionId().replace("V", "")));
					ps.setString(4, releaseTestCaseBug.getBugId());
					ps.setString(5, releaseTestCaseBug.getBugType());
					ps.setInt(6, releaseTestCaseBug.getExecutionEnvId());
					ps.setString(7, releaseTestCaseBug.getRemarks());
					ps.setBoolean(8, releaseTestCaseBug.isApplicable());
					ps.setString(9, currentUser);
					ps.setString(10, currentUser);
					ps.setBoolean(11, releaseTestCaseBug.isDeleted());
					return ps;
				}
			}, requestKeyHolder);
			int releaseTestCaseBugId = requestKeyHolder.getKey().intValue();
			releaseTestCaseBug.setReleaseTestCaseBugId(releaseTestCaseBugId);
			return releaseTestCaseBug;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions(LocalUtils
					.getStringLocale("fw_test_mgmt_locale", "TestCaseImport"));
		}
	}

	@Override
	public int updateReleaseTestCaseBugById(
			ReleaseTestCaseBug releaseTestCaseBug) throws APIExceptions {
		updateDataSource();
		String sql = "UPDATE release_tc_bug SET release_id=?, testcase_id=?, "
				+ "testcase_version_id=?, bug_id=?, bug_type=?, "
				+ "execution_env_id=?, remarks=?, applicable=?, "
				+ "modified_by=?, is_deleted=? WHERE release_tc_bug_id=?";
		try {
			return jdbcTemplate.update(sql, releaseTestCaseBug.getReleaseId(),
					releaseTestCaseBug.getTestCaseId(),
					releaseTestCaseBug.getTestCaseVersionId(),
					releaseTestCaseBug.getBugId(),
					releaseTestCaseBug.getBugType(),
					releaseTestCaseBug.getExecutionEnvId(),
					releaseTestCaseBug.getRemarks(),
					releaseTestCaseBug.isApplicable(),
					applicationCommonUtil.getCurrentUser(),
					releaseTestCaseBug.isDeleted(),
					releaseTestCaseBug.getReleaseTestCaseBugId());
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if (message.toLowerCase()
					.contains("violates not-null constraint")) {
				message = "One or more required data for update in not provided.";
			}
			throw new APIExceptions(
					"Error : Update bug for test case cannot be done : "
							+ message);
		}
	}

	@Override
	public void deleteReleaseTestCaseBugById(long releaseTestCaseBugId)
			throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM release_tc_bug WHERE release_tc_bug_id=?";
		try {
			jdbcTemplate.update(sql, releaseTestCaseBugId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public ReleaseTestCaseBug getReleaseTestCaseBugById(
			int releaseTestCaseBugId, String applicable, String isDeleted)
			throws APIExceptions {
		try {
			updateDataSource();
			String query = "SELECT * FROM release_tc_bug WHERE release_tc_bug_id=?";
			if (!(!ValueValidations.isValueValid(applicable)
					|| applicable.equalsIgnoreCase("all"))) {
				query += " AND applicable=" + Boolean.valueOf(applicable);
			}
			if (!(!ValueValidations.isValueValid(isDeleted)
					|| isDeleted.equalsIgnoreCase("all"))) {
				query += " AND is_deleted=" + Boolean.valueOf(isDeleted);
			}
			return jdbcTemplate.queryForObject(query,
					new RowMapper<ReleaseTestCaseBug>() {
						@Override
						public ReleaseTestCaseBug mapRow(ResultSet rs,
								int rownumber) throws SQLException {
							return readRS(rs);
						}
					}, releaseTestCaseBugId);
		} catch (EmptyResultDataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions(
					"Error : Invalid bug id [" + releaseTestCaseBugId
							+ "] is given as no test case is available");
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}
	
	@Override
	public List<ReleaseTestCaseBug> getReleaseTestCaseBugByTestCaseId(
			int testCaseId, String applicable, String isDeleted)
			throws APIExceptions {
		try {
			updateDataSource();
			String query = "SELECT * FROM release_tc_bug WHERE testcase_id=?";
			if (!(!ValueValidations.isValueValid(applicable)
					|| applicable.equalsIgnoreCase("all"))) {
				query += " AND applicable=" + Boolean.valueOf(applicable);
			}
			if (!(!ValueValidations.isValueValid(isDeleted)
					|| isDeleted.equalsIgnoreCase("all"))) {
				query += " AND is_deleted=" + Boolean.valueOf(isDeleted);
			}
			return jdbcTemplate.query(query, new RowMapper<ReleaseTestCaseBug>() {
				@Override
				public ReleaseTestCaseBug mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					return readRS(rs);
				}
			}, testCaseId);
		} catch (EmptyResultDataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions(
					"Error : Invalid test case id [" + testCaseId
							+ "] is given as no test case is available");
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public List<ReleaseTestCaseBug> getReleaseTestCaseBug(int releaseId,
			int testCaseId, String testCaseVersionId, String bugId,
			String bugType, String applicable, String isDeleted,
			String createDateStart, String createDateEnd,
			String modifiedDateStart, String modifiedDateEnd)
			throws APIExceptions {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM release_tc_bug ");
		boolean isConditionAdded = false;
		if (releaseId > 0) {
			sql.append(conditionStatus(isConditionAdded));
			isConditionAdded = true;
			sql.append(" release_id=" + releaseId);
		}

		if (testCaseId > 0) {
			sql.append(conditionStatus(isConditionAdded));
			isConditionAdded = true;
			sql.append(" testcase_id=" + testCaseId);

			if (ValueValidations.isValueValid(testCaseVersionId)) {
				sql.append(" AND testcase_version_id="
						+ Integer.parseInt(testCaseVersionId.replace("V", "")));
			}
		}

		if (!(!ValueValidations.isValueValid(bugId))) {
			sql.append(conditionStatus(isConditionAdded));
			isConditionAdded = true;
			sql.append(" bug_id='" + bugId + "'");
		}

		if (!(!ValueValidations.isValueValid(bugType))) {
			sql.append(conditionStatus(isConditionAdded));
			isConditionAdded = true;
			sql.append(" bug_type='" + bugType + "'");
		}

		if (!(!ValueValidations.isValueValid(applicable)
				|| applicable.equalsIgnoreCase("all"))) {
			sql.append(conditionStatus(isConditionAdded));
			isConditionAdded = true;
			sql.append(" applicable=" + Boolean.valueOf(applicable));
		}
		if (!(!ValueValidations.isValueValid(isDeleted)
				|| isDeleted.equalsIgnoreCase("all"))) {
			sql.append(conditionStatus(isConditionAdded));
			isConditionAdded = true;
			sql.append(" is_deleted=" + Boolean.valueOf(isDeleted));
		}

		if (ValueValidations.isValueValid(createDateStart)) {
			sql.append(conditionStatus(isConditionAdded));
			isConditionAdded = true;
			sql.append(" t1.created_date >='" + createDateStart + "'");
		}

		if (ValueValidations.isValueValid(createDateEnd)) {
			sql.append(conditionStatus(isConditionAdded));
			isConditionAdded = true;
			sql.append(" t1.created_date <='" + createDateEnd + "'");
		}

		if (ValueValidations.isValueValid(modifiedDateStart)) {
			sql.append(conditionStatus(isConditionAdded));
			isConditionAdded = true;
			sql.append(" t1.modified_date >='" + modifiedDateStart + "'");
		}

		if (ValueValidations.isValueValid(modifiedDateEnd)) {
			sql.append(conditionStatus(isConditionAdded));
			isConditionAdded = true;
			sql.append(" t1.modified_date <='" + modifiedDateEnd + "'");
		}
		updateDataSource();
		try {
			return jdbcTemplate.query(sql.toString(),
					new RowMapper<ReleaseTestCaseBug>() {
						@Override
						public ReleaseTestCaseBug mapRow(ResultSet rs,
								int rownumber) throws SQLException {
							return readRS(rs);
						}
					});
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new APIExceptions("Error : Some error occured while fetching "
					+ "the test case bug information for given filters : "
					+ e.getMessage());
		}
	}

	private String conditionStatus(boolean isConditionAdded) {
		if (!isConditionAdded) {
			return " WHERE ";
		} else {
			return " AND ";
		}
	}

	private ReleaseTestCaseBug readRS(ResultSet rs) throws SQLException {
		ReleaseTestCaseBug releaseTestCaseBug = new ReleaseTestCaseBug();
		releaseTestCaseBug
				.setReleaseTestCaseBugId(rs.getInt("release_tc_bug_id"));
		releaseTestCaseBug.setReleaseId(rs.getInt("release_id"));
		;
		releaseTestCaseBug.setTestCaseId(rs.getInt("testcase_id"));
		releaseTestCaseBug
				.setTestCaseVersionId("V" + rs.getInt("testcase_version_id"));
		releaseTestCaseBug.setBugId(rs.getString("bug_id"));
		releaseTestCaseBug.setBugType(rs.getString("bug_type"));
		releaseTestCaseBug.setExecutionEnvId(rs.getInt("execution_env_id"));
		releaseTestCaseBug.setRemarks(rs.getString("remarks"));
		releaseTestCaseBug.setApplicable(rs.getBoolean("applicable"));
		releaseTestCaseBug.setCreatedBy(rs.getString("created_by"));
		releaseTestCaseBug.setModifiedBy(rs.getString("modified_by"));
		releaseTestCaseBug.setCreatedDate(rs.getTimestamp("created_date"));
		releaseTestCaseBug.setModifiedDate(rs.getTimestamp("modified_date"));
		releaseTestCaseBug.setDeleted(rs.getBoolean("is_deleted"));
		return releaseTestCaseBug;
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
