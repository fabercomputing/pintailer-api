package com.fw.dao.impl;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fw.dao.IAppAuditManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.AppAuditBean;
import com.fw.exceptions.APIExceptions;
import com.fw.utils.ApplicationCommonUtil;

@Repository
public class AppAuditManagerImpl implements IAppAuditManager {

	private Logger log = Logger.getLogger(AppAuditManagerImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	public List<String> getSchemaNames() throws APIExceptions {
		updateDataSource();
		String query = "SELECT schema_name FROM information_schema.schemata "
				+ "WHERE schema_name NOT IN ('public', 'information_schema') "
				+ "AND schema_name NOT LIKE 'pg_%'";
		return jdbcTemplate.query(query, new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				return rs.getString("schema_name");
			}
		});
	}

	@Override
	public List<String> getSchemaTableNames(String schemaName)
			throws APIExceptions {
		updateDataSource();
		String query = "SELECT table_name FROM information_schema.tables "
				+ "WHERE table_type='BASE TABLE' AND table_schema=?";
		return jdbcTemplate.query(query, new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				return rs.getString("table_name");
			}
		}, schemaName);
	}

	@Override
	public List<String> getTableColumnNames(String schemaName, String tableName)
			throws APIExceptions {
		updateDataSource();
		String query = "SELECT column_name FROM information_schema.columns "
				+ "WHERE table_schema=? AND table_name=?";
		return jdbcTemplate.query(query, new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				return rs.getString("column_name");
			}
		}, new Object[] { schemaName, tableName });
	}

	@Override
	public List<AppAuditBean> getAuditDetails(String schema_name,
			String table_name, String actionTime, String actionTimeCondition,
			String operation, String username, String oldColumnName,
			String newColumnName, String oldValue, String newValue)
			throws APIExceptions {
		updateDataSource();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM fw_test_mgmt_logs.data_history "
				+ "WHERE schema_name='" + schema_name + "'");

		if (!(null == table_name || table_name.equals("")
				|| table_name.equals("null"))) {
			sql.append(" AND table_name='" + table_name + "'");
		}
		if (!(null == operation || operation.equals("")
				|| operation.equals("null"))) {
			sql.append(" AND operation='" + operation.toUpperCase() + "'");
		}
		if (!(null == username || username.equals("")
				|| username.equals("null"))) {
			sql.append(" AND username='" + username.toUpperCase() + "'");
		}
		if (!(null == actionTime || actionTime.equals("")
				|| actionTime.equals("null"))) {
			Timestamp timestamp = new Timestamp(applicationCommonUtil
					.convertDateTimeInMillisec(actionTime));
			if (actionTimeCondition.equalsIgnoreCase("greater")) {
				sql.append(" AND action_time>='" + timestamp + "'");
			} else if (actionTimeCondition.equalsIgnoreCase("lesser")) {
				sql.append(" AND action_time<='" + timestamp + "'");
			}
		}

		if (!(null == newColumnName || newColumnName.equals("")
				|| newColumnName.equals("null"))
				&& !(null == newValue || newValue.equals("")
						|| newValue.equals("null"))) {
			sql.append(" AND new_val->> '" + newColumnName + "' LIKE '%"
					+ newValue + "%'");
		}

		if (!(null == oldColumnName || oldColumnName.equals("")
				|| oldColumnName.equals("null"))
				&& !(null == oldValue || oldValue.equals("")
						|| oldValue.equals("null"))) {
			sql.append(" AND old_val->> '" + oldColumnName + "' LIKE '%"
					+ oldValue + "%'");
		}

		log.info(sql.toString());
		try {
			return jdbcTemplate.query(sql.toString(),
					new RowMapper<AppAuditBean>() {
						@Override
						public AppAuditBean mapRow(ResultSet rs, int rownumber)
								throws SQLException {
							AppAuditBean appAuditBean = new AppAuditBean();
							appAuditBean.setAppId(rs.getLong("id"));
							appAuditBean.setActionTime(
									rs.getTimestamp("action_time"));
							appAuditBean
									.setSchemaName(rs.getString("schema_name"));
							appAuditBean
									.setTableName(rs.getString("table_name"));
							appAuditBean
									.setOperation(rs.getString("operation"));
							appAuditBean.setUsername(rs.getString("username"));
							appAuditBean.setOldVal(rs.getString("old_val"));
							appAuditBean.setNewVal(rs.getString("new_val"));
							return appAuditBean;
						}
					});
		} catch (Exception e) {
			throw new APIExceptions("Error occured while fetching audit info : "
					+ e.getMessage());
		}

		// String query = "SELECT * FROM fw_test_mgmt_logs.data_history WHERE "
		// + "table_name='"
		// + table_name
		// + "' and operation='"
		// + operation
		// +
		// "' AND new_val->> 'created_by' LIKE '%rinku.sharma%' AND
		// action_time>='2018-09-28'";
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}

}
