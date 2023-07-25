package com.fw.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import com.fw.dao.IEmailManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.Email;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;
import com.fw.utils.ApplicationCommonUtil;
import com.fw.utils.ValueValidations;

@Service
public class EmailManagerImpl implements IEmailManager {

	private Logger log = Logger.getLogger(EmailManagerImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	public Email persistEmailController(Email email) throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO email_controller (email_type, "
					+ "email_frequency, recipient_list, is_active, created_by, "
					+ "modified_by) VALUES(?,?,?,?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "email_controller_id" });
					ps.setString(1, email.getEmailType());
					ps.setString(2, email.getEmailFrequency());
					ps.setString(3, email.getRecipientList());
					ps.setBoolean(4, email.isActive());
					ps.setString(5, currentUser);
					ps.setString(6, currentUser);
					return ps;
				}
			}, requestKeyHolder);
			int logEntityId = requestKeyHolder.getKey().intValue();
			email.setEmailControllerId(logEntityId);
			return email;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions(
					"Error occured while storing the email info.");
		}
	}

	@Override
	public int updateEmailControllerById(Email email) throws APIExceptions {
		updateDataSource();
		String sql = "UPDATE email_controller SET email_type=?, email_frequency=?, "
				+ "recipient_list=?, is_active=?, modified_by=? "
				+ "WHERE email_controller_id=?";
		try {
			return jdbcTemplate.update(sql, email.getEmailType(),
					email.getEmailFrequency(), email.getRecipientList(),
					email.isActive(), applicationCommonUtil.getCurrentUser(),
					email.getEmailControllerId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new APIExceptions(
					"Error : Update email info cannot be done : "
							+ e.getMessage());
		}
	}

	@Override
	public Email getEmailControllerById(int emailControllerId, String isActive)
			throws APIExceptions {
		try {
			updateDataSource();
			String query = "SELECT * FROM email_controller WHERE email_controller_id=? ";
			if (!(!ValueValidations.isValueValid(isActive)
					|| isActive.equalsIgnoreCase("all"))) {
				query += "AND t.is_active=" + Boolean.valueOf(isActive);
			}
			return jdbcTemplate.queryForObject(query, new RowMapper<Email>() {
				@Override
				public Email mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					Email email = new Email();
					email.setEmailControllerId(
							rs.getInt("email_controller_id"));
					email.setEmailType(rs.getString("email_type"));
					email.setEmailFrequency(rs.getString("email_frequency"));
					email.setRecipientList(rs.getString("recipient_list"));
					email.setActive(rs.getBoolean("is_active"));
					email.setCreatedBy(rs.getString("created_by"));
					email.setModifiedBy(rs.getString("modified_by"));
					return email;
				}
			}, emailControllerId);
		} catch (EmptyResultDataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions("Error : Invalid email id ["
					+ emailControllerId + "] is given");
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public void deleteEmailControllerById(int emailControllerId)
			throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM email_controller WHERE email_controller_id=?";
		try {
			jdbcTemplate.update(sql, emailControllerId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public Boolean isEmailActive(String emailType) throws APIExceptions {
		try {
			updateDataSource();
			String query = "SELECT * FROM email_controller WHERE email_type=? "
					+ "AND is_active=true";
			return jdbcTemplate.queryForObject(query, new RowMapper<Boolean>() {
				@Override
				public Boolean mapRow(ResultSet rs, int rownumber)
						throws SQLException {
					return true;
				}
			}, emailType);
		} catch (EmptyResultDataAccessException e) {
			log.error(e.getMessage());
			return false;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return false;
		}
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(PintailerConstants.COMMON_ORG);
	}

}
