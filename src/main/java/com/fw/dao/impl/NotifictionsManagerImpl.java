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

import com.fw.dao.INotificationsManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.Notifications;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;
import com.fw.utils.ApplicationCommonUtil;

@Repository
public class NotifictionsManagerImpl implements INotificationsManager {

	private Logger log = Logger.getLogger(NotifictionsManagerImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	public Notifications persistNotifications(Notifications logEntity)
			throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO notifications (message_body, subject, "
					+ "username, user_email_id, attachment_path, read_flg, "
					+ "read_by) VALUES(?,?,?,?,?,?,?)";

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "notification_id" });
					ps.setString(1, logEntity.getMessage());
					ps.setString(2, logEntity.getSubject());
					ps.setString(3, logEntity.getUsername());
					ps.setString(4, logEntity.getUserEmailId());
					ps.setString(5, logEntity.getAttachmentPath());
					ps.setBoolean(6, logEntity.isReadFlg());
					ps.setString(7, logEntity.getReadBy());
					return ps;
				}
			}, requestKeyHolder);
			long logEntityId = requestKeyHolder.getKey().longValue();
			logEntity.setNotificationId(logEntityId);
			return logEntity;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			throw new APIExceptions(
					"Error occured while storing notification message : "
							+ e.getMessage());
		}
	}

	@Override
	public Notifications updateNotificationsById(Notifications logEntity)
			throws APIExceptions {
		updateDataSource();
		String sql = "UPDATE notifications SET message_body=?, subject=?, "
				+ "username=?, user_email_id=?, attachment_path=?, read_flg=?, "
				+ "read_by=? WHERE notification_id=?";
		jdbcTemplate.update(sql, logEntity.getMessage(),
				logEntity.getSubject(), logEntity.getUsername(),
				logEntity.getUserEmailId(), logEntity.getAttachmentPath(),
				logEntity.isReadFlg(), applicationCommonUtil.getCurrentUser(),
				logEntity.getNotificationId());
		return logEntity;
	}

	@Override
	public List<Notifications> getAllNotifications() throws APIExceptions {
		updateDataSource();
		return jdbcTemplate.query("SELECT * FROM notifications",
				new RowMapper<Notifications>() {
					@Override
					public Notifications mapRow(ResultSet rs, int rownumber)
							throws SQLException {
						Notifications logEntity = new Notifications();
						logEntity.setNotificationId(rs
								.getLong("notification_id"));
						logEntity.setMessage(rs.getString("message_body"));
						logEntity.setSubject(rs.getString("subject"));
						logEntity.setUsername(rs.getString("username"));
						logEntity.setUserEmailId(rs.getString("user_email_id"));
						logEntity.setAttachmentPath(rs
								.getString("attachment_path"));
						logEntity.setReadFlg(rs.getBoolean("read_flg"));
						logEntity.setReadBy(rs.getString("read_by"));
						logEntity.setCreatedDate(rs
								.getTimestamp("created_date"));
						logEntity.setModifiedDate(rs
								.getTimestamp("modified_date"));
						return logEntity;
					}
				});
	}

	@Override
	public Notifications getNotificationsById(long notificationId)
			throws APIExceptions {
		updateDataSource();
		String query = "SELECT * FROM notifications WHERE notification_id="
				+ notificationId;

		try {
			return jdbcTemplate.queryForObject(query.toString(),
					new RowMapper<Notifications>() {
						@Override
						public Notifications mapRow(ResultSet rs, int rownumber)
								throws SQLException {
							Notifications logEntity = new Notifications();
							logEntity.setNotificationId(rs
									.getLong("notification_id"));
							logEntity.setMessage(rs.getString("message_body"));
							logEntity.setSubject(rs.getString("subject"));
							logEntity.setUsername(rs.getString("username"));
							logEntity.setUserEmailId(rs
									.getString("user_email_id"));
							logEntity.setAttachmentPath(rs
									.getString("attachment_path"));
							logEntity.setReadFlg(rs.getBoolean("read_flg"));
							logEntity.setReadBy(rs.getString("read_by"));
							logEntity.setCreatedDate(rs
									.getTimestamp("created_date"));
							logEntity.setModifiedDate(rs
									.getTimestamp("modified_date"));

							return logEntity;
						}
					});
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(PintailerConstants.COMMON_ORG);
	}
}
