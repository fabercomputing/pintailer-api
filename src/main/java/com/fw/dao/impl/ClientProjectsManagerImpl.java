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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.fw.dao.IClientProjectsManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.ClientProjects;
import com.fw.exceptions.APIExceptions;
import com.fw.utils.ApplicationCommonUtil;

@Repository
public class ClientProjectsManagerImpl implements IClientProjectsManager {

	private Logger log = Logger.getLogger(ClientProjectsManagerImpl.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	public ClientProjects persistClientProjects(ClientProjects logEntity)
			throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO client_projects (name, client_organization, "
					+ "created_by, modified_by) VALUES(?,?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "client_project_id" });
					ps.setString(1, logEntity.getName());
					ps.setString(2, logEntity.getClientOrganization());
					ps.setString(3, currentUser);
					ps.setString(4, currentUser);
					return ps;
				}
			}, requestKeyHolder);
			int logEntityId = requestKeyHolder.getKey().intValue();
			logEntity.setClientProjectId(logEntityId);
			return logEntity;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions(
					"Error occured while adding the new project details.");
		}

	}

	@Override
	public void updateClientProjectsById(ClientProjects logEntity)
			throws APIExceptions {
		updateDataSource();
		String sql = "UPDATE client_projects SET name=?, client_organization=?, "
				+ "modified_by=?  WHERE client_project_id=?";
		jdbcTemplate.update(sql, logEntity.getName(),
				logEntity.getClientOrganization(),
				applicationCommonUtil.getCurrentUser(),
				logEntity.getClientProjectId());
	}

	@Override
	public void deleteClientProjectsById(int Id) throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM client_projects WHERE client_project_id=?";
		try {
			jdbcTemplate.update(sql, Id);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
		}

	}

	@Override
	public ClientProjects getClientProjectsById(int logEntityId)
			throws APIExceptions {
		updateDataSource();
		try {
			return jdbcTemplate.queryForObject(
					"SELECT * FROM client_projects WHERE client_project_id=?",
					new RowMapper<ClientProjects>() {
						@Override
						public ClientProjects mapRow(ResultSet rs, int rownumber)
								throws SQLException {
							return fetchRS(rs);
						}
					}, logEntityId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public List<ClientProjects> getAllClientProjectsForOrg(
			String organizationName, String DBName) throws APIExceptions {
		ClientDatabaseContextHolder.set(DBName);
		try {
			return jdbcTemplate
					.query("SELECT * FROM client_projects WHERE client_organization=?",
							new RowMapper<ClientProjects>() {
								@Override
								public ClientProjects mapRow(ResultSet rs,
										int rownumber) throws SQLException {
									return fetchRS(rs);
								}
							}, organizationName);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	private ClientProjects fetchRS(ResultSet rs) throws SQLException {
		ClientProjects clientProjects = new ClientProjects();
		clientProjects.setClientProjectId(rs.getInt("client_project_id"));
		clientProjects.setName(rs.getString("name"));
		clientProjects.setClientOrganization(rs
				.getString("client_organization"));
		clientProjects.setCreatedBy(rs.getString("created_by"));
		clientProjects.setModifiedBy(rs.getString("modified_by"));
		clientProjects.setCreatedDate(rs.getTimestamp("created_date"));
		clientProjects.setModifiedDate(rs.getTimestamp("modified_date"));
		return clientProjects;
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
