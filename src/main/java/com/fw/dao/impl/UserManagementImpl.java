package com.fw.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.fw.dao.IUserManagementManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.UserManagement;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;
import com.fw.utils.ApplicationCommonUtil;

@Repository
public class UserManagementImpl implements IUserManagementManager {

	private Logger log = Logger.getLogger(UserManagementImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	public UserManagement persistUserManagementInfo(UserManagement logEntity)
			throws APIExceptions {
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			updateDataSource();

			String sql = "INSERT INTO user_management (user_name, user_organization, "
					+ "user_email, user_password, user_role, user_token, user_project, "
					+ "applicable, is_default, created_by,  modified_by, is_deleted) "
					+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";

			String user = applicationCommonUtil.getCurrentUser() == null ? "Organization Admin Auto"
					: applicationCommonUtil.getCurrentUser();

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "user_id" });
					ps.setString(1, logEntity.getUserName());
					ps.setString(2, logEntity.getUserOrg());
					ps.setString(3, logEntity.getUserEmail());
					ps.setString(4, logEntity.getUserPass());
					ps.setString(5, logEntity.getUserRole());
					ps.setString(6, logEntity.getUserToken());
					ps.setString(7, logEntity.getUserProject());
					ps.setBoolean(8, logEntity.isApplicable());
					ps.setBoolean(9, logEntity.isDefaultOrg());
					ps.setString(10, user);
					ps.setString(11, user);
					ps.setBoolean(12, logEntity.isDeleted());
					return ps;
				}
			}, requestKeyHolder);
			long logEntityId = requestKeyHolder.getKey().longValue();
			logEntity.setUserId(logEntityId);
			return logEntity;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions("Error occured while adding user info");
		}
	}

	@Override
	public int updateUserManagementInfoById(UserManagement logEntity)
			throws APIExceptions {
		String sql = "UPDATE user_management SET user_name=?, user_organization=?, "
				+ "user_email=?, user_password=?, user_role=?, user_token=?, "
				+ "user_project=?, applicable=?, is_default=?, modified_by=?, "
				+ "is_deleted=? WHERE user_id=?";
		try {
			updateDataSource();

			return jdbcTemplate
					.update(sql,
							logEntity.getUserName(),
							logEntity.getUserOrg(),
							logEntity.getUserEmail(),
							logEntity.getUserPass(),
							logEntity.getUserRole(),
							logEntity.getUserToken(),
							logEntity.getUserProject(),
							logEntity.isApplicable(),
							logEntity.isDefaultOrg(),
							applicationCommonUtil.getCurrentUser() == null ? "Organization Admin"
									: applicationCommonUtil.getCurrentUser(),
							logEntity.isDeleted(), logEntity.getUserId());
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if (message.toLowerCase().contains("violates not-null constraint")) {
				message = "One or more required data for update in not provided.";
			}
			throw new APIExceptions(
					"Error : Update test case cannot be done : " + message);
		}
	}

	@Override
	public void deleteUserManagementInfoById(long Id) throws APIExceptions {
		String sql = "DELETE FROM user_management WHERE user_id=?";
		try {
			updateDataSource();
			jdbcTemplate.update(sql, Id);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions("Error occured while deleting the data");
		}
	}

	@SuppressWarnings("serial")
	private static final Map<String, String> columns = new HashMap<String, String>() {
		{
			put("User ID", "user_id");
			put("User Name", "user_name");
			put("User Organization", "user_organization");
			put("User Email", "user_email");
			put("User Role", "user_role");
			put("User Project", "user_project");
			put("Default Organization", "default_org");
			put("Applicable", "applicable");
			put("Created By", "created_by");
			put("Modified By", "modified_by");
			put("Created Date", "created_date");
			put("Modified Date", "modified_date");
			put("Deleted", "is_deleted");
		}
	};

	@Override
	public List<UserManagement> getAllUserManagements(int clientProjectId,
			String applicable, String userIds, String userSearchTxt,
			String sortByColumn, String ascOrDesc, int limit, int pageNumber,
			String startDate, String endDate) throws APIExceptions {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM user_management AS t1, "
				+ "client_projects AS p1 ");

		sql.append(" WHERE ");
		// adding filter condition for test case page
		if (!(null == userSearchTxt || userSearchTxt.trim().equals("") || userSearchTxt
				.trim().toLowerCase().equals("null"))) {
			sql.append(" (CAST(t1.user_id AS text) LIKE '%" + userSearchTxt
					+ "%' OR t1.user_name ILIKE '%" + userSearchTxt
					+ "%' OR t1.user_organization ILIKE '%" + userSearchTxt
					+ "%' OR t1.user_email ILIKE '%" + userSearchTxt
					+ "%' OR t1.user_role ILIKE '%" + userSearchTxt
					+ "%' OR t1.user_project ILIKE '%" + userSearchTxt
					+ "%' OR t1.default_org ILIKE '%" + userSearchTxt
					+ "%' OR t1.created_by ILIKE '%" + userSearchTxt
					+ "%' OR t1.modified_by ILIKE '%" + userSearchTxt
					+ "%' OR CAST(t1.created_date AS text) LIKE '%"
					+ userSearchTxt
					+ "%' OR CAST(t1.modified_date AS text) LIKE '%"
					+ userSearchTxt + "%') AND ");
		}

		sql.append(" m1.client_project_id = p1.client_project_id");

		String clientOrganization = applicationCommonUtil
				.getDefaultOrgInOriginalCase();
		if (clientProjectId > 0) {
			sql.append(" AND p1.client_project_id=" + clientProjectId);
		} else if (null != clientOrganization) {
			sql.append(" AND p1.client_project_id IN (SELECT client_project_id FROM "
					+ "client_projects WHERE client_organization='"
					+ clientOrganization + "')");
		}

		if (!(null == applicable || applicable.trim().equals("") || applicable
				.trim().toLowerCase().equals("all"))) {
			sql.append(" AND t1.applicable=" + Boolean.valueOf(applicable));
		}

		if (!(null == userIds || userIds.equals("") || userIds.equals("0"))) {
			sql.append(" AND t1.user_id IN (" + userIds + ")");
		}

		if (!(null == startDate || startDate.trim().equals("") || startDate
				.trim().equalsIgnoreCase("null"))) {
			sql.append(" AND t1.created_date >='" + startDate + "'");
		}

		if (!(null == endDate || endDate.trim().equals("") || endDate.trim()
				.equalsIgnoreCase("null"))) {
			sql.append(" AND t1.created_date <='" + endDate + "'");
		}

		sql.append(" AND t1.is_deleted=false");

		// Added for server side sorting and pagination
		if (!(null == sortByColumn || sortByColumn.equals("") || sortByColumn
				.equals("null"))
				&& !(null == ascOrDesc || ascOrDesc.equals("") || ascOrDesc
						.equals("null"))) {
			sql.append(" ORDER BY t1." + columns.get(sortByColumn) + " "
					+ ascOrDesc);
		} else {
			sql.append(" ORDER BY t1.created_date DESC, t1.user_id ASC");
		}

		if (limit > 0 && pageNumber >= 0) {
			sql.append(" LIMIT " + limit + " OFFSET " + pageNumber * limit);
		}
		try {
			updateDataSource();

			return jdbcTemplate.query(sql.toString(),
					new RowMapper<UserManagement>() {
						@Override
						public UserManagement mapRow(ResultSet rs, int rownumber)
								throws SQLException {
							return readRS(rs);
						}
					});
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new APIExceptions(
					"Error : Some error occured while fetching "
							+ "the user information for given filters : "
							+ e.getMessage());
		}
	}

	public UserManagement getUserManagementById(long logEntityId,
			boolean applicable, boolean isDeleted) throws APIExceptions {
		try {
			updateDataSource();

			return jdbcTemplate.queryForObject(
					"SELECT * FROM user_management WHERE user_id=? "
							+ "AND applicable=" + applicable
							+ "AND is_deleted=" + isDeleted,
					new RowMapper<UserManagement>() {
						@Override
						public UserManagement mapRow(ResultSet rs, int rownumber)
								throws SQLException {
							return readRS(rs);
						}
					}, logEntityId);
		} catch (EmptyResultDataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions("Error : Invalid user id [" + logEntityId
					+ "] is given as no user is available");
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public UserManagement getUserManagementByUserAndOrgName(String userName,
			String orgName) throws APIExceptions {
		try {
			updateDataSource();

			return jdbcTemplate.queryForObject(
					"SELECT * FROM user_management WHERE user_name=? AND user_organization=?"
							+ "AND applicable=true AND is_deleted=false",
					new RowMapper<UserManagement>() {
						@Override
						public UserManagement mapRow(ResultSet rs, int rownumber)
								throws SQLException {
							return readRS(rs);
						}
					}, new Object[] { userName, orgName });
		} catch (EmptyResultDataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions("Error : Invalid user name [" + userName
					+ "] and organization name [" + orgName
					+ "] is given as no data is available");
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	// Added for DB operations on all rows i.e. update hash code
	@Override
	public List<UserManagement> getAllUserManagements() throws APIExceptions {
		try {
			updateDataSource();

			String query = "SELECT * FROM user_management";
			return jdbcTemplate.query(query, new RowMapper<UserManagement>() {
				@Override
				public UserManagement mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					return readRS(rs);
				}
			});
		} catch (EmptyResultDataAccessException e) {
			log.error(e.getMessage());
			return null;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public List<UserManagement> getUserOrganizationInfoFromDB(String userName)
			throws APIExceptions {
		try {
			updateDataSource();

			String query = "SELECT * FROM user_management "
					+ "WHERE user_name=?";
			return jdbcTemplate.query(query, new RowMapper<UserManagement>() {
				@Override
				public UserManagement mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					return readRS(rs);
				}
			}, userName);
		} catch (EmptyResultDataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions("User with user name [" + userName
					+ "] does not exist in DB. Please add the organization "
					+ "project info with default organization "
					+ "and try again");
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public UserManagement getUserOrganizationInfoFromDB(String orgName,
			String userName) throws APIExceptions {
		try {
			updateDataSource();

			String query = "SELECT * FROM user_management "
					+ "WHERE user_organization=? AND user_name=?";
			return jdbcTemplate.queryForObject(query,
					new RowMapper<UserManagement>() {
						@Override
						public UserManagement mapRow(ResultSet rs, int rownumber)
								throws SQLException {
							return readRS(rs);
						}
					}, new Object[] { orgName, userName });
		} catch (EmptyResultDataAccessException e) {
			log.info("User with user name [" + userName
					+ "] for the organization [" + orgName
					+ "] does not exist in DB. Adding the information "
					+ "and assigning one project as default for the user");
			return null;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public boolean isUserOrgInfoExist(String orgName, String userName)
			throws APIExceptions {
		try {
			updateDataSource();

			String query = "SELECT * FROM user_management "
					+ "WHERE user_organization=? AND user_name=?";
			return jdbcTemplate.queryForObject(query, new RowMapper<Boolean>() {
				@Override
				public Boolean mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					return true;
				}
			}, new Object[] { orgName, userName });
		} catch (EmptyResultDataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions("User with user name [" + userName
					+ "] for the organization [" + orgName
					+ "] does not exist in DB. Please contact the "
					+ "admin and try again");
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return false;
		}
	}

	@Override
	public String getDefaultOrg(String userName) throws APIExceptions {
		try {
			updateDataSource();

			String query = "SELECT user_organization FROM user_management "
					+ "WHERE user_name=? AND is_default=true";
			return jdbcTemplate.queryForObject(query, new RowMapper<String>() {
				@Override
				public String mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					return rs.getString("user_organization");
				}
			}, new Object[] { userName });
		} catch (EmptyResultDataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions("User with user name [" + userName
					+ "] does not exist in DB. Please contact the "
					+ "admin and try again");
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(PintailerConstants.COMMON_ORG);
	}

	private UserManagement readRS(ResultSet rs) throws SQLException {
		UserManagement logEntity = new UserManagement();
		logEntity.setUserId(rs.getLong("user_id"));
		logEntity.setUserName(rs.getString("user_name"));
		logEntity.setUserOrg(rs.getString("user_organization"));
		logEntity.setUserEmail(rs.getString("user_email"));
		logEntity.setUserPass(rs.getString("user_password"));
		logEntity.setUserRole(rs.getString("user_role"));
		logEntity.setUserToken(rs.getString("user_token"));
		logEntity.setUserProject(rs.getString("user_project"));
		logEntity.setApplicable(rs.getBoolean("applicable"));
		logEntity.setDefaultOrg(rs.getBoolean("is_default"));
		logEntity.setCreatedBy(rs.getString("created_by"));
		logEntity.setModifiedBy(rs.getString("modified_by"));
		logEntity.setCreatedDate(rs.getTimestamp("created_date"));
		logEntity.setModifiedDate(rs.getTimestamp("modified_date"));
		logEntity.setDeleted(rs.getBoolean("is_deleted"));
		return logEntity;
	}
}
