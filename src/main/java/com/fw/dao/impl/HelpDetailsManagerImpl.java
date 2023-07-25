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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.fw.dao.IHelpDetailsManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.HelpDetails;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;
import com.fw.utils.ApplicationCommonUtil;

@Repository
public class HelpDetailsManagerImpl implements IHelpDetailsManager {

	private Logger log = Logger.getLogger(HelpDetailsManagerImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	public HelpDetails persistHelpDetailsInfo(HelpDetails logEntity)
			throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO help_details (help_text, topic_id, "
					+ "created_by, modified_by) VALUES(?,?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "detail_id" });
					ps.setString(1, logEntity.getHelpText());
					ps.setInt(2, logEntity.getTopicId());
					ps.setString(3, currentUser);
					ps.setString(4, currentUser);
					return ps;
				}
			}, requestKeyHolder);
			int logEntityId = requestKeyHolder.getKey().intValue();
			logEntity.setDetailId(logEntityId);
			return logEntity;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}

	}

	@Override
	public void updateHelpDetailsById(HelpDetails logEntity)
			throws APIExceptions {
		updateDataSource();
		String sql = "UPDATE help_details SET help_text=?, topic_id=?, modified_by=? "
				+ "WHERE detail_id=?";

		jdbcTemplate.update(sql, logEntity.getHelpText(),
				logEntity.getTopicId(), applicationCommonUtil.getCurrentUser(),
				logEntity.getDetailId());
	}

	@Override
	public void deleteHelpDetailsById(HelpDetails logEntity)
			throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM help_details WHERE detail_id=?";
		try {
			jdbcTemplate.update(sql, logEntity.getTopicId());
		} catch (DataAccessException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public List<HelpDetails> getAllHelpDetailsRowMapper() throws APIExceptions {
		updateDataSource();
		return jdbcTemplate.query(
				"SELECT * FROM help_details ORDER BY detail_id",
				new RowMapper<HelpDetails>() {
					@Override
					public HelpDetails mapRow(ResultSet rs, int rownumber)
							throws SQLException {
						HelpDetails logEntity = new HelpDetails();
						logEntity.setDetailId(rs.getInt("detail_id"));
						logEntity.setHelpText(rs.getString("help_text"));
						logEntity.setTopicId(rs.getInt("topic_id"));
						logEntity.setCreatedBy(rs.getString("created_by"));
						logEntity.setModifiedBy(rs.getString("modified_by"));
						logEntity.setCreatedDate(rs
								.getTimestamp("created_date"));
						logEntity.setModifiedDate(rs
								.getTimestamp("modified_date"));

						return logEntity;
					}
				});
	}

	@Override
	public HelpDetails getHelpDetailsForTopicById(int Id) throws APIExceptions {
		updateDataSource();
		try {
			return jdbcTemplate.queryForObject(
					"SELECT * FROM help_details WHERE topic_id=?",
					new RowMapper<HelpDetails>() {
						@Override
						public HelpDetails mapRow(ResultSet rs, int rownumber)
								throws SQLException {
							HelpDetails logEntity = new HelpDetails();
							logEntity.setDetailId(rs.getInt("detail_id"));
							logEntity.setHelpText(rs.getString("help_text"));
							logEntity.setTopicId(rs.getInt("topic_id"));
							logEntity.setCreatedBy(rs.getString("created_by"));
							logEntity.setModifiedBy(rs.getString("modified_by"));
							logEntity.setCreatedDate(rs
									.getTimestamp("created_date"));
							logEntity.setModifiedDate(rs
									.getTimestamp("modified_date"));
							return logEntity;
						}
					}, Id);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(PintailerConstants.COMMON_ORG);
	}
}
