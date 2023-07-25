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

import com.fw.dao.IReleaseManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.Release;
import com.fw.exceptions.APIExceptions;
import com.fw.utils.ApplicationCommonUtil;

@Repository
public class ReleaseManagerImpl implements IReleaseManager {

	private Logger log = Logger.getLogger(ReleaseManagerImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	public Release persistRelease(Release release) throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO release (release_no, client_project_id, "
					+ "isClosed, created_by, modified_by) VALUES(?,?,?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "release_id" });
					ps.setString(1, release.getReleaseNumber());
					ps.setInt(2, release.getClientProjectId());
					ps.setBoolean(3, release.isClosed());
					ps.setString(4, currentUser);
					ps.setString(5, currentUser);
					return ps;
				}
			}, requestKeyHolder);
			int logEntityId = requestKeyHolder.getKey().intValue();
			release.setReleaseId(logEntityId);
			return release;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public int updateRelease(Release logEntity) throws APIExceptions {
		updateDataSource();
		String sql = "UPDATE release SET isClosed=?, modified_by=? "
				+ "WHERE release_id=?";
		return jdbcTemplate.update(sql, logEntity.isClosed(),
				applicationCommonUtil.getCurrentUser(),
				logEntity.getReleaseId());
	}

	@Override
	public List<Release> getAllReleases(int clientProjectId, String condition)
			throws APIExceptions {
		updateDataSource();
		String query = "SELECT release_id, release_no, isclosed FROM release "
				+ "WHERE client_project_id=" + clientProjectId;

		if (!(null == condition || condition.trim().equals("")
				|| condition.trim().equals("null") || condition.toLowerCase()
				.equals("all"))) {
			query += " AND isClosed=" + Boolean.valueOf(condition);
		}
		return jdbcTemplate.query(query, new RowMapper<Release>() {
			@Override
			public Release mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				Release logEntity = new Release();
				logEntity.setReleaseId(rs.getInt("release_id"));
				logEntity.setReleaseNumber(rs.getString("release_no"));
				logEntity.setClientProjectId(clientProjectId);
				logEntity.setClosed(rs.getBoolean("isclosed"));
				return logEntity;
			}
		});
	}

	@Override
	public int deleteRelease(int releaseId) throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM release WHERE release_id=?";
		try {
			return jdbcTemplate.update(sql, releaseId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return 0;
		}
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}

	@Override
	public Release getReleaseByProjectAndRelease(int clientProjectId,
			int releaseId) throws APIExceptions {
		updateDataSource();
		String query = "SELECT release_no, isClosed FROM release "
				+ "WHERE client_project_id=" + clientProjectId
				+ " AND release_id=" + releaseId + "";
		try {
			return jdbcTemplate.queryForObject(query, new RowMapper<Release>() {
				@Override
				public Release mapRow(ResultSet rs, int rowNum)
						throws SQLException, DataAccessException {
					Release logEntity = new Release();
					logEntity.setReleaseId(releaseId);
					logEntity.setReleaseNumber(rs.getString("release_no"));
					logEntity.setClientProjectId(clientProjectId);
					logEntity.setClosed(rs.getBoolean("isClosed"));
					return logEntity;
				}
			});
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

}
