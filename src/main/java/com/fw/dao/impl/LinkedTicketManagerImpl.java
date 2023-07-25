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

import com.fw.dao.ILinkedTicketManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.LinkedTicket;
import com.fw.exceptions.APIExceptions;
import com.fw.utils.ApplicationCommonUtil;

@Repository
public class LinkedTicketManagerImpl implements ILinkedTicketManager {

	private Logger log = Logger.getLogger(LinkedTicketManagerImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	public LinkedTicket persistLinkedTicket(LinkedTicket linkedTicket)
			throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO linked_ticket (testexecution_id, ticket_no, "
					+ "applicable, is_deleted, created_by, modified_by) "
					+ " VALUES(?,?,?,?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "linked_ticket_id" });
					ps.setLong(1, linkedTicket.getTestExecutionId());
					ps.setString(2, linkedTicket.getTicketNumber());
					ps.setBoolean(3, linkedTicket.isApplicable());
					ps.setBoolean(4, linkedTicket.isDeleted());
					ps.setString(5, currentUser);
					ps.setString(6, currentUser);
					return ps;
				}
			}, requestKeyHolder);
			int logEntityId = requestKeyHolder.getKey().intValue();
			linkedTicket.setLinkedTicketId(logEntityId);
			return linkedTicket;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public int updateLinkedTicket(LinkedTicket linkedTicket)
			throws APIExceptions {
		updateDataSource();
		String sql = "UPDATE linked_ticket SET testexecution_id=?, ticket_no=?, "
				+ "applicable=?, is_deleted=?, modified_by=? "
				+ "WHERE linked_ticket_id=?";
		return jdbcTemplate.update(sql, linkedTicket.getTestExecutionId(),
				linkedTicket.getTicketNumber(), linkedTicket.isApplicable(),
				linkedTicket.isDeleted(),
				applicationCommonUtil.getCurrentUser(),
				linkedTicket.getLinkedTicketId());
	}

	@Override
	public List<LinkedTicket> getAllLinkedTickets(int releaseId,
			int environmentId, String testCaseIds) throws APIExceptions {
		updateDataSource();
		StringBuilder query = new StringBuilder();
		query.append("SELECT l.linked_ticket_id, l.testexecution_id, "
				+ "l.ticket_no, l.applicable, l.created_by, l.modified_by, "
				+ "l.created_date, l.modified_date FROM linked_ticket l, "
				+ "testcase_execution e WHERE l.is_deleted=false "
				+ "AND l.applicable=true "
				+ "AND l.testexecution_id = e.testcase_execution_id");
		if (releaseId >= 0) {
			query.append(" AND e.release_id=" + releaseId);
		}

		if (environmentId >= 0) {
			query.append(" AND e.environment_id=" + environmentId);
		}

		if (!(null == testCaseIds || testCaseIds.trim().equals("") || testCaseIds
				.trim().equals("null"))) {
			query.append(" AND e.testcase_id IN (" + testCaseIds + ")");
		}

		return jdbcTemplate.query(query.toString(),
				new RowMapper<LinkedTicket>() {
					@Override
					public LinkedTicket mapRow(ResultSet rs, int rownumber)
							throws SQLException {
						LinkedTicket linkedTicket = new LinkedTicket();
						linkedTicket.setLinkedTicketId(rs
								.getInt("linked_ticket_id"));
						linkedTicket.setTestExecutionId(rs
								.getLong("testexecution_id"));
						linkedTicket.setTicketNumber(rs.getString("ticket_no"));
						linkedTicket.setApplicable(rs.getBoolean("applicable"));
						linkedTicket.setDeleted(false);
						linkedTicket.setCreatedBy(rs.getString("created_by"));
						linkedTicket.setModifiedBy(rs.getString("modified_by"));
						linkedTicket.setCreatedDate(rs
								.getTimestamp("created_date"));
						linkedTicket.setModifiedDate(rs
								.getTimestamp("modified_date"));
						return linkedTicket;
					}
				});
	}

	@Override
	public List<LinkedTicket> getLinkedTicketsForExecutionId(long executionId)
			throws APIExceptions {
		updateDataSource();
		StringBuilder query = new StringBuilder();
		query.append("SELECT linked_ticket_id, testexecution_id, "
				+ "ticket_no, applicable, created_by, modified_by, "
				+ "created_date, modified_date FROM linked_ticket l "
				+ "WHERE is_deleted=false AND applicable=true "
				+ "AND testexecution_id = " + executionId);

		return jdbcTemplate.query(query.toString(),
				new RowMapper<LinkedTicket>() {
					@Override
					public LinkedTicket mapRow(ResultSet rs, int rownumber)
							throws SQLException {
						LinkedTicket linkedTicket = new LinkedTicket();
						linkedTicket.setLinkedTicketId(rs
								.getInt("linked_ticket_id"));
						linkedTicket.setTestExecutionId(rs
								.getLong("testexecution_id"));
						linkedTicket.setTicketNumber(rs.getString("ticket_no"));
						linkedTicket.setApplicable(rs.getBoolean("applicable"));
						linkedTicket.setDeleted(false);
						linkedTicket.setCreatedBy(rs.getString("created_by"));
						linkedTicket.setModifiedBy(rs.getString("modified_by"));
						linkedTicket.setCreatedDate(rs
								.getTimestamp("created_date"));
						linkedTicket.setModifiedDate(rs
								.getTimestamp("modified_date"));
						return linkedTicket;
					}
				});
	}

	@Override
	public int deleteLinkedTicket(int linkedTicketId) throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM linked_ticket WHERE linked_ticket_id=?";
		try {
			return jdbcTemplate.update(sql, linkedTicketId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return 0;
		}
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
