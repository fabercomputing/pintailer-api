package com.fw.dao.impl;

import java.sql.Array;

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

import org.apache.commons.lang3.ArrayUtils;
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

import com.fw.dao.IModulesManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.Modules;
import com.fw.domain.ModulesVersion;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;
import com.fw.utils.ApplicationCommonUtil;

@Repository
public class ModulesManagerImpl implements IModulesManager {

	private Logger log = Logger.getLogger(ModulesManagerImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	public Modules persistModules(Modules logEntity) throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO modules (name, module_parent_id, "
					+ "client_project_id, created_by, modified_by) "
					+ "VALUES(?,?,?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "module_id" });
					ps.setString(1, logEntity.getName());
					ps.setLong(2, logEntity.getModuleParentId());
					ps.setLong(3, logEntity.getClientProjectsId());
					ps.setString(4, currentUser);
					ps.setString(5, currentUser);
					return ps;
				}
			}, requestKeyHolder);
			logEntity.setModuleId(requestKeyHolder.getKey().intValue());
			return logEntity;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}

	}

	@Override
	public void updateModulesById(Modules logEntity) throws APIExceptions {
		updateDataSource();
		String sql = "UPDATE modules SET name=?, module_parent_id=?, "
				+ "client_project_id=?, modified_by=? WHERE module_id=?";
		jdbcTemplate.update(sql, logEntity.getName(),
				logEntity.getModuleParentId(), logEntity.getClientProjectsId(),
				applicationCommonUtil.getCurrentUser(),
				logEntity.getModuleId());
	}

	@Override
	public void deleteModulesById(long moduleId) throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM modules WHERE module_id=?";
		try {
			jdbcTemplate.update(sql, moduleId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
		}

	}

	@Override
	public Modules getModulesById(long logEntityId) throws APIExceptions {
		updateDataSource();
		try {
			return jdbcTemplate.queryForObject(
					"SELECT name, module_parent_id, client_project_id "
							+ "FROM modules WHERE module_id=?",
					new RowMapper<Modules>() {
						@Override
						public Modules mapRow(ResultSet rs, int rownumber)
								throws SQLException {
							Modules logEntity = new Modules();
							logEntity.setModuleId(logEntityId);
							logEntity.setName(rs.getString("name"));
							logEntity.setModuleParentId(
									rs.getLong("module_parent_id"));
							logEntity.setClientProjectsId(
									rs.getInt("client_project_id"));
							return logEntity;
						}
					}, logEntityId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public Modules getModulesByModuleNameAndClientProjectId(String moduleName,
			long parentModuleId, int clientProjectId) throws APIExceptions {
		updateDataSource();
		try {
			String sql = "SELECT m.module_id FROM modules m, "
					+ "client_projects c WHERE m.name='" + moduleName
					+ "' AND m.client_project_id = " + clientProjectId
					+ " AND m.module_parent_id=" + parentModuleId
					+ " AND m.client_project_id=c.client_project_id"
					+ " AND c.client_organization='"
					+ applicationCommonUtil.getDefaultOrgInOriginalCase() + "'";
			return jdbcTemplate.queryForObject(sql, new RowMapper<Modules>() {
				@Override
				public Modules mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					Modules logEntity = new Modules();
					logEntity.setModuleId(rs.getInt("module_id"));
					logEntity.setName(moduleName);
					logEntity.setModuleParentId(parentModuleId);
					logEntity.setClientProjectsId(clientProjectId);
					return logEntity;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			log.info("Module [" + moduleName + "] is not available");
			return null;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public Modules getModulesByModuleName(String moduleName,
			int clientProjectId) throws APIExceptions {
		updateDataSource();
		try {
			String sql = "SELECT m.module_id, m.module_parent_id,"
					+ " m.client_project_id FROM modules m, client_projects c"
					+ " WHERE m.name=? AND m.client_project_id=c.client_project_id"
					+ " AND c.client_organization='"
					+ applicationCommonUtil.getDefaultOrgInOriginalCase() + "'";
			if (clientProjectId > 0) {
				sql += " AND c.client_project_id=" + clientProjectId;
			} else {
				if (null != applicationCommonUtil.getAssignedProjectIds()) {
					sql += " AND c.client_project_id IN ("
							+ applicationCommonUtil.getAssignedProjectIds()
							+ ")";
				}
			}
			return jdbcTemplate.queryForObject(sql, new RowMapper<Modules>() {
				@Override
				public Modules mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					Modules logEntity = new Modules();
					logEntity.setModuleId(rs.getInt("module_id"));
					logEntity.setName(moduleName);
					logEntity.setModuleParentId(rs.getLong("module_parent_id"));
					logEntity.setClientProjectsId(
							rs.getInt("client_project_id"));
					return logEntity;
				}
			}, moduleName);
		} catch (DataAccessException e) {
			log.info("Given module[" + moduleName
					+ "] is not available for organization ["
					+ applicationCommonUtil.getDefaultOrgInOriginalCase()
					+ "]");
			return null;
		}
	}

	@Override
	public List<Modules> getModulesByProjectId(int clientProjectId)
			throws APIExceptions {
		updateDataSource();
		String sql = null;
		if (clientProjectId > 0) {
			sql = "SELECT module_id, name, module_parent_id, client_project_id "
					+ "FROM modules WHERE client_project_id=" + clientProjectId;
		} else {
			sql = "SELECT m.module_id, m.name, m.module_parent_id, m.client_project_id"
					+ " FROM modules m, client_projects c"
					+ " WHERE m.client_project_id=c.client_project_id"
					+ " AND c.client_organization='"
					+ applicationCommonUtil.getDefaultOrgInOriginalCase() + "'";

			if (null != applicationCommonUtil.getAssignedProjectIds()) {
				sql += " AND c.client_project_id IN ("
						+ applicationCommonUtil.getAssignedProjectIds() + ")";
			}
		}
		return jdbcTemplate.query(sql, new RowMapper<Modules>() {
			@Override
			public Modules mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				Modules logEntity = new Modules();
				logEntity.setModuleId(rs.getInt("module_id"));
				logEntity.setName(rs.getString("name"));
				logEntity.setModuleParentId(rs.getLong("module_parent_id"));
				logEntity.setClientProjectsId(rs.getInt("client_project_id"));
				return logEntity;
			}
		});
	}

	@Override
	public List<Modules> getTopParentModulesByProjectIdForReport(
			int clientProjectId) throws APIExceptions {
		updateDataSource();
		String sql = "SELECT m.module_id, m.name, m.module_parent_id, m.client_project_id"
				+ " FROM modules m, client_projects c WHERE m.module_parent_id=0"
				+ " AND m.client_project_id=c.client_project_id"
				+ " AND c.client_organization='"
				+ applicationCommonUtil.getDefaultOrgInOriginalCase() + "'";
		if (clientProjectId > 0) {
			sql += " AND c.client_project_id=" + clientProjectId;
		} else {
			if (null != applicationCommonUtil.getAssignedProjectIds()) {
				sql += " AND c.client_project_id IN ("
						+ applicationCommonUtil.getAssignedProjectIds() + ")";
			}
		}
		return jdbcTemplate.query(sql, new RowMapper<Modules>() {
			@Override
			public Modules mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				Modules logEntity = new Modules();
				logEntity.setModuleId(rs.getInt("module_id"));
				logEntity.setName(rs.getString("name"));
				logEntity.setModuleParentId(rs.getLong("module_parent_id"));
				logEntity.setClientProjectsId(rs.getInt("client_project_id"));
				return logEntity;
			}
		});
	}

	// First element of the list will contain the required complete hierarchy
	@Override
	public List<String> getModuleHierarchy(long moduleId) throws APIExceptions {
		updateDataSource();
		String query = "WITH RECURSIVE test(module_parent_id, level, name, name_path)"
				+ " AS (SELECT module_parent_id, 0, name, Array[concat(name,'"
				+ PintailerConstants.MODULE_HIERARCHY_SEPARATOR
				+ "',module_id)]"
				+ " FROM modules WHERE module_id = ? UNION ALL"
				+ " SELECT p.module_parent_id, pr.level+1, p.name, ARRAY_APPEND(pr.name_path, CONCAT(p.name,'"
				+ PintailerConstants.MODULE_HIERARCHY_SEPARATOR
				+ "', p.module_id))"
				+ " FROM test pr, modules p WHERE p.module_id = pr.module_parent_id)"
				+ " SELECT name_path as modulesHierarchy, max(level) as lvl FROM test"
				+ " GROUP BY name_path ORDER BY lvl DESC";
		return jdbcTemplate.query(query, new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				return rs.getString("modulesHierarchy");
			}
		}, moduleId);
	}

	@Override
	public Map<String, String[]> getModuleHierarchy(String moduleIds)
			throws APIExceptions {
		updateDataSource();
		String query = "WITH RECURSIVE test(module_parent_id, level, name, "
				+ "name_path, original_module_id) AS (SELECT module_parent_id, "
				+ "0, name, Array[concat(name)], module_id FROM modules "
				+ "WHERE module_id IN ("+moduleIds+") UNION ALL SELECT p.module_parent_id, "
				+ "pr.level+1, p.name, ARRAY_APPEND(pr.name_path, p.name), "
				+ "pr.original_module_id FROM test pr, modules p WHERE "
				+ "p.module_id = pr.module_parent_id) SELECT original_module_id, "
				+ "name_path AS modulesHierarchy, max(level) AS lvl FROM test "
				+ "WHERE module_parent_id=0 GROUP BY name_path, original_module_id "
				+ "ORDER BY lvl DESC";
		return jdbcTemplate.query(query,
				new ResultSetExtractor<Map<String, String[]>>() {
					@Override
					public Map<String, String[]> extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						Map<String, String[]> result = new LinkedHashMap<String, String[]>();
						while (rs.next()) {
							Array array = rs.getArray("modulesHierarchy");
							String[] arr = (String[]) array.getArray();
							ArrayUtils.reverse(arr);
							result.put(rs.getString("original_module_id"), arr);
						}
						return result;
					}

				});
	}

	@Override
	public List<Long> getAllChildModules(long parentModuleId)
			throws APIExceptions {
		updateDataSource();
		String query = "WITH RECURSIVE getAllchilds (module_id) AS "
				+ "(SELECT module_id, name, module_parent_id FROM modules "
				+ "WHERE module_id=? UNION ALL "
				+ "SELECT m.module_id, m.name, m.module_parent_id FROM modules m "
				+ "JOIN getAllchilds ON getAllchilds.module_id = m.module_parent_id) "
				+ "SELECT module_id, name, module_parent_id FROM getAllchilds ORDER BY module_id";
		return jdbcTemplate.query(query, new RowMapper<Long>() {
			@Override
			public Long mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				return rs.getLong("module_id");
			}
		}, parentModuleId);
	}

	@Override
	public long getModuleIdFromHierarchy(String sql) throws APIExceptions {
		updateDataSource();
		return jdbcTemplate.query(sql, new ResultSetExtractor<Long>() {
			@Override
			public Long extractData(ResultSet rs)
					throws SQLException, DataAccessException {
				if (rs.next()) {
					return rs.getLong("module_id");
				} else {
					return 0l;
				}
			}
		});
	}

	@Override
	public List<ModulesVersion> getModulesVersionById(long moduleId)
			throws APIExceptions {
		updateDataSource();
		try {
			return jdbcTemplate.query(
					"SELECT * FROM modules_version WHERE module_id=? "
							+ "ORDER BY version_id DESC",
					new RowMapper<ModulesVersion>() {
						@Override
						public ModulesVersion mapRow(ResultSet rs,
								int rownumber) throws SQLException {
							ModulesVersion modulesVersion = new ModulesVersion();
							modulesVersion.setModulesVersionId(
									rs.getInt("modules_version_id"));
							modulesVersion.setModuleId(rs.getInt("module_id"));
							modulesVersion.setName(rs.getString("name"));
							modulesVersion.setModuleParentId(
									rs.getLong("module_parent_id"));
							modulesVersion.setClientProjectsId(
									rs.getInt("client_project_id"));
							modulesVersion
									.setCreatedBy(rs.getString("created_by"));
							modulesVersion
									.setModifiedBy(rs.getString("modified_by"));
							modulesVersion.setVersionId(
									"V" + rs.getInt("version_id"));
							modulesVersion.setHardDeleted(
									rs.getBoolean("is_hard_deleted"));
							return modulesVersion;
						}
					}, moduleId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}

}
