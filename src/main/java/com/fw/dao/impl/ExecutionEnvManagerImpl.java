package com.fw.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.fw.dao.IExecutionEnvManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.ExecutionEnv;
import com.fw.exceptions.APIExceptions;
import com.fw.utils.ApplicationCommonUtil;

@Repository
public class ExecutionEnvManagerImpl implements IExecutionEnvManager {

	private Logger log = Logger.getLogger(ExecutionEnvManagerImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	public ExecutionEnv persistExecutionEnv(ExecutionEnv executionEnv)
			throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO execution_environment (env_name, client_organization, "
					+ "created_by, modified_by) VALUES(?,?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "execution_env_id" });
					ps.setString(1, executionEnv.getExecutionEnvName());
					ps.setString(2, executionEnv.getClientOrganization());
					ps.setString(3, currentUser);
					ps.setString(4, currentUser);
					return ps;
				}
			}, requestKeyHolder);
			int logEntityId = requestKeyHolder.getKey().intValue();
			executionEnv.setExecutionEnvId(logEntityId);
			return executionEnv;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public int updateExecutionEnv(ExecutionEnv executionEnv)
			throws APIExceptions {
		updateDataSource();
		String sql = "UPDATE execution_environment SET env_name=?, "
				+ "client_organization=?, modified_by=? "
				+ "WHERE execution_env_id=?";
		return jdbcTemplate.update(sql, executionEnv.getExecutionEnvName(),
				executionEnv.getClientOrganization(),
				applicationCommonUtil.getCurrentUser(),
				executionEnv.getExecutionEnvId());
	}

	@Override
	public List<ExecutionEnv> getAllExecutionEnvs() throws APIExceptions {
		updateDataSource();
		String orgName = applicationCommonUtil.getDefaultOrgInOriginalCase();
		return jdbcTemplate.query(
				"SELECT execution_env_id, env_name, created_by, "
						+ "modified_by, created_date, modified_date "
						+ "FROM execution_environment WHERE "
						+ "client_organization='" + orgName + "'",
				new RowMapper<ExecutionEnv>() {
					@Override
					public ExecutionEnv mapRow(ResultSet rs, int rownumber)
							throws SQLException {
						ExecutionEnv executionEnv = new ExecutionEnv();
						executionEnv.setExecutionEnvId(rs
								.getInt("execution_env_id"));
						executionEnv.setExecutionEnvName(rs
								.getString("env_name"));
						executionEnv.setClientOrganization(orgName);
						executionEnv.setCreatedBy(rs.getString("created_by"));
						executionEnv.setModifiedBy(rs.getString("modified_by"));
						executionEnv.setCreatedDate(rs
								.getTimestamp("created_date"));
						executionEnv.setModifiedDate(rs
								.getTimestamp("modified_date"));
						return executionEnv;
					}
				});
	}

	@Override
	public int deleteExecutionEnvById(int logEntityId) throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM execution_environment WHERE execution_env_id=?";
		try {
			return jdbcTemplate.update(sql, logEntityId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
		}
		return 0;
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
