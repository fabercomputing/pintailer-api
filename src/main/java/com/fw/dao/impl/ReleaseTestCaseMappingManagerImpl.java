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

import com.fw.dao.IReleaseTestCaseMappingManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.ReleaseTestCaseMapping;
import com.fw.exceptions.APIExceptions;
import com.fw.utils.ApplicationCommonUtil;

@Repository
public class ReleaseTestCaseMappingManagerImpl
		implements IReleaseTestCaseMappingManager {

	private Logger log = Logger
			.getLogger(ReleaseTestCaseMappingManagerImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	public ReleaseTestCaseMapping persistReleaseTestCaseMapping(
			ReleaseTestCaseMapping releaseTestCaseMapping)
			throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO release_testcasemap_version (client_project_id, release_id, "
					+ "testcase_id, testcase_version_id, testcase_map_version_id, "
					+ "is_deleted, created_by, modified_by) "
					+ " VALUES(?,?,?,?,?,?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "release_testcasemap_id" });
					ps.setInt(1, releaseTestCaseMapping.getClientProjectId());
					ps.setInt(2, releaseTestCaseMapping.getReleaseId());
					ps.setInt(3, releaseTestCaseMapping.getTestCaseId());
					ps.setInt(4, releaseTestCaseMapping.getTestCaseVersionId());
					ps.setInt(5,
							releaseTestCaseMapping.getTestCaseMapVersionId());
					ps.setBoolean(6, releaseTestCaseMapping.isDeleted());
					ps.setString(7, currentUser);
					ps.setString(8, currentUser);
					return ps;
				}
			}, requestKeyHolder);
			long logEntityId = requestKeyHolder.getKey().longValue();
			releaseTestCaseMapping.setReleaseTestCaseMapId(logEntityId);
			return releaseTestCaseMapping;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public int persistReleaseTestCaseMappingInBatch(final List<Object[]> args)
			throws APIExceptions {
		updateDataSource();
		String sql = "INSERT INTO release_testcasemap_version (client_project_id, release_id, "
				+ "testcase_id, testcase_version_id, testcase_map_version_id, "
				+ "is_deleted, created_by, modified_by) "
				+ " VALUES(?,?,?,?,?,?,?,?)";
		try {
			int[] arr = jdbcTemplate.batchUpdate(sql, args);
			return arr.length;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return 0;
		}
	}

	@Override
	public int deleteReleaseMap(int clientProjectId, int releaseId,
			String testCaseIds) throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM release_testcasemap_version WHERE client_project_id="
				+ clientProjectId + " AND release_id=" + releaseId
				+ " AND testcase_id IN (" + testCaseIds + ")";
		try {
			return jdbcTemplate.update(sql);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return 0;
		}
	}

	@Override
	public List<ReleaseTestCaseMapping> getReleaseTestCaseMapping(
			int clientProjectId, int releaseId) throws APIExceptions {
		updateDataSource();
		String sql = "SELECT * FROM release_testcasemap_version WHERE client_project_id="
				+ +clientProjectId + " AND release_id=" + releaseId;
		return jdbcTemplate.query(sql, new RowMapper<ReleaseTestCaseMapping>() {
			@Override
			public ReleaseTestCaseMapping mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				ReleaseTestCaseMapping releaseTestCaseMapping = new ReleaseTestCaseMapping();
				releaseTestCaseMapping.setReleaseTestCaseMapId(
						rs.getLong("release_testcasemap_id"));
				releaseTestCaseMapping.setReleaseId(rs.getInt("release_id"));
				releaseTestCaseMapping.setTestCaseId(rs.getInt("testcase_id"));
				releaseTestCaseMapping
						.setTestCaseVersionId(rs.getInt("testcase_version_id"));
				releaseTestCaseMapping.setTestCaseMapVersionId(
						rs.getInt("testcase_map_version_id"));
				releaseTestCaseMapping.setDeleted(rs.getBoolean("is_deleted"));
				releaseTestCaseMapping.setCreatedBy(rs.getString("created_by"));
				releaseTestCaseMapping
						.setModifiedBy(rs.getString("modified_by"));
				releaseTestCaseMapping
						.setCreatedDate(rs.getTimestamp("created_date"));
				releaseTestCaseMapping
						.setModifiedDate(rs.getTimestamp("modified_date"));
				return releaseTestCaseMapping;
			}
		});
	}

	@Override
	public List<ReleaseTestCaseMapping> getReleaseTestCaseMapping(
			int clientProjectId, int releaseId, int testCaseId,
			int testCaseVersionId) throws APIExceptions {
		updateDataSource();
		String sql = "SELECT * FROM release_testcasemap_version WHERE "
				+ "client_project_id=?";
		if (releaseId > 0) {
			sql += " AND release_id=" + releaseId;
		}
		sql += " AND testcase_id=? AND testcase_version_id=? "
				+ "ORDER BY testcase_map_version_id DESC";
		return jdbcTemplate.query(sql, new RowMapper<ReleaseTestCaseMapping>() {
			@Override
			public ReleaseTestCaseMapping mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				ReleaseTestCaseMapping releaseTestCaseMapping = new ReleaseTestCaseMapping();
				releaseTestCaseMapping.setReleaseTestCaseMapId(
						rs.getLong("release_testcasemap_id"));
				releaseTestCaseMapping.setReleaseId(rs.getInt("release_id"));
				releaseTestCaseMapping.setTestCaseId(rs.getInt("testcase_id"));
				releaseTestCaseMapping
						.setTestCaseVersionId(rs.getInt("testcase_version_id"));
				releaseTestCaseMapping.setTestCaseMapVersionId(
						rs.getInt("testcase_map_version_id"));
				releaseTestCaseMapping.setDeleted(rs.getBoolean("is_deleted"));
				releaseTestCaseMapping.setCreatedBy(rs.getString("created_by"));
				releaseTestCaseMapping
						.setModifiedBy(rs.getString("modified_by"));
				releaseTestCaseMapping
						.setCreatedDate(rs.getTimestamp("created_date"));
				releaseTestCaseMapping
						.setModifiedDate(rs.getTimestamp("modified_date"));
				return releaseTestCaseMapping;
			}
		}, new Object[] { clientProjectId, testCaseId, testCaseVersionId });
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
