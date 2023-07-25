package com.fw.dao.impl;

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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.fw.dao.IReleaseMapManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.ReleaseMap;
import com.fw.domain.ReleaseMapVersion;
import com.fw.exceptions.APIExceptions;
import com.fw.utils.ApplicationCommonUtil;
import com.fw.utils.ValueValidations;

@Repository
public class ReleaseMapManagerImpl implements IReleaseMapManager {

	private Logger log = Logger.getLogger(ReleaseMapManagerImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	public ReleaseMap persistReleaseMap(ReleaseMap releaseMap)
			throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO release_testcase (release_id, "
					+ "testcase_id, created_by, modified_by) "
					+ " VALUES(?,?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "release_map_id" });
					ps.setLong(1, releaseMap.getReleaseId());
					ps.setLong(2, releaseMap.getTestCaseId());
					ps.setString(3, currentUser);
					ps.setString(4, currentUser);
					return ps;
				}
			}, requestKeyHolder);
			int logEntityId = requestKeyHolder.getKey().intValue();
			releaseMap.setReleaseMapId(logEntityId);
			return releaseMap;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public boolean persistReleaseMapVersion(ReleaseMapVersion releaseMapVersion)
			throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO release_testcase_version (release_id, "
					+ "testcase_ids, created_by, modified_by) "
					+ " VALUES(?,?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "release_testcase_version_id" });
					ps.setLong(1, releaseMapVersion.getReleaseId());
					ps.setString(2, releaseMapVersion.getTestCaseIds());
					ps.setString(3, currentUser);
					ps.setString(4, currentUser);
					return ps;
				}
			}, requestKeyHolder);
			int logEntityId = requestKeyHolder.getKey().intValue();
			if (logEntityId > 0)
				return true;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions(
					"Error occured while storing the release and test case mapping version info");
		}
		return false;
	}

	@Override
	public int persistReleaseMapInBatch(final List<Object[]> args)
			throws APIExceptions {
		updateDataSource();
		String sql = "INSERT INTO release_testcase (release_id, "
				+ "testcase_id, testcase_version_id, created_by, modified_by) "
				+ "VALUES(?,?,?,?,?)";
		try {
			int[] arr = jdbcTemplate.batchUpdate(sql, args);
			return arr.length;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return 0;
		}
	}

	@Override
	public int updateReleaseMap(ReleaseMap releaseMap) throws APIExceptions {
		updateDataSource();
		String sql = "UPDATE release_testcase SET release_id=?, testcase_id=?, "
				+ " modified_by=? WHERE release_map_id=?";
		try {
			return jdbcTemplate.update(sql, releaseMap.getReleaseId(),
					releaseMap.getTestCaseId(),
					applicationCommonUtil.getCurrentUser(),
					releaseMap.getReleaseMapId());
		} catch (Exception p) {
			log.error(
					"Error occured while updating release and test cases mapping : "
							+ p.getMessage());
			return 0;
		}
	}

	@Override
	public Map<Integer, String> getReleasesMapSelectedVersion(int releaseId,
			int clientProjectId) throws APIExceptions {
		updateDataSource();
		String sql = "SELECT rm.testcase_id, rm.testcase_version_id "
				+ "FROM release_testcase rm, testcase t, "
				+ "modules m WHERE rm.release_id=" + releaseId
				+ " AND rm.testcase_id=t.testcase_id AND "
				+ "t.module_id=m.module_id AND m.client_project_id="
				+ clientProjectId;
		return jdbcTemplate.query(sql,
				new ResultSetExtractor<Map<Integer, String>>() {
					@Override
					public Map<Integer, String> extractData(ResultSet rs)
							throws SQLException {
						Map<Integer, String> result = new LinkedHashMap<Integer, String>();
						while (rs.next()) {
							result.put(rs.getInt("testcase_id"),
									"V" + rs.getInt("testcase_version_id"));
						}
						return result;
					}
				});
	}

	@Override
	public List<ReleaseMapVersion> getReleasesMapVersion(int releaseId)
			throws APIExceptions {
		updateDataSource();
		String query = "SELECT * FROM release_testcase_version WHERE "
				+ "release_id=? ORDER BY version_id DESC";
		try {
			return jdbcTemplate.query(query,
					new RowMapper<ReleaseMapVersion>() {
						@Override
						public ReleaseMapVersion mapRow(ResultSet rs,
								int rownumber) throws SQLException {
							ReleaseMapVersion releaseMapVersion = new ReleaseMapVersion();
							releaseMapVersion.setReleaseTestCaseVersionId(
									rs.getInt("release_testcase_version_id"));
							releaseMapVersion
									.setReleaseId(rs.getInt("release_id"));
							releaseMapVersion.setTestCaseIds(
									rs.getString("testcase_ids"));
							releaseMapVersion
									.setCreatedBy(rs.getString("created_by"));
							releaseMapVersion
									.setModifiedBy(rs.getString("modified_by"));
							releaseMapVersion.setCreatedDate(
									rs.getTimestamp("created_date"));
							releaseMapVersion.setModifiedDate(
									rs.getTimestamp("modified_date"));
							releaseMapVersion.setReleaseMapVersionId(
									"V" + rs.getInt("version_id"));
							releaseMapVersion.setHardDeleted(
									rs.getBoolean("is_hard_deleted"));
							return releaseMapVersion;
						}
					}, releaseId);
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public int deleteReleaseMap(int releaseMapId) throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM release_testcase WHERE release_map_id=?";
		try {
			return jdbcTemplate.update(sql, releaseMapId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return 0;
		}
	}

	@Override
	public int deleteReleaseMapByReleaseUniqueId(int releaseId)
			throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM release_testcase WHERE release_id=?";
		try {
			return jdbcTemplate.update(sql, releaseId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return 0;
		}
	}

	@Override
	public int deleteReleaseMapByReleaseUniqueIdAndTestCaseId(
			int clientProjectId, int releaseId, String moduleIds)
			throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM release_testcase WHERE " + "release_id="
				+ releaseId + " AND testcase_id "
				+ "IN(SELECT testcase_id FROM testcase WHERE ";
		if (ValueValidations.isValueValid(moduleIds)) {
			sql += "module_id IN (" + moduleIds + "))";
		} else {
			sql += "module_id IN (SELECT module_id FROM modules "
					+ "WHERE client_project_id=" + clientProjectId + "))";
		}

		try {
			return jdbcTemplate.update(sql);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			log.info("Error occured while deleting the release mapping");
			return 0;
		}
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
