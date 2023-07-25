package com.fw.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

import com.fw.dao.IAssetUserManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.AssetUser;
import com.fw.exceptions.APIExceptions;
import com.fw.utils.ApplicationCommonUtil;

@Repository
public class AssetUserManagerImpl implements IAssetUserManager {

	private Logger log = Logger.getLogger(AssetUserManagerImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	public AssetUser persistAssetUser(AssetUser logEntity) throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO asset_user (asset_id, start_date, end_date, "
					+ "created_by,  modified_by, user_id)"
					+ " VALUES(?,?,?,?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();

			LocalDateTime startTime = (logEntity.getStartDate() == null) ? null
					: LocalDateTime.ofInstant(logEntity.getStartDate()
							.toInstant(), ZoneId.systemDefault());

			LocalDateTime endTime = (logEntity.getEndDate() == null) ? null
					: LocalDateTime.ofInstant(logEntity.getEndDate()
							.toInstant(), ZoneId.systemDefault());
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "user_id" });

					ps.setLong(1, logEntity.getAssetInfoId());
					if (startTime == null)
						ps.setNull(2, Types.TIMESTAMP);
					else
						ps.setTimestamp(2, Timestamp.valueOf(startTime));
					if (endTime == null)
						ps.setNull(3, Types.TIMESTAMP);
					else
						ps.setTimestamp(3, Timestamp.valueOf(endTime));
					ps.setString(4, currentUser);
					ps.setString(5, currentUser);
					ps.setString(6, logEntity.getAssetUserId());
					return ps;
				}
			}, requestKeyHolder);

			return logEntity;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}

	}

	@Override
	public void updateAssetUserById(AssetUser logEntity) throws APIExceptions {
		updateDataSource();
		String sql = "UPDATE asset_user SET end_date=?, modified_by=? WHERE user_id=? "
				+ "AND asset_id=? AND start_date=?";
		jdbcTemplate.update(sql, logEntity.getEndDate(),
				applicationCommonUtil.getCurrentUser(),
				logEntity.getAssetUserId(), logEntity.getAssetInfoId(),
				logEntity.getStartDate());
	}

	@Override
	public void deleteAssetUserById(AssetUser logEntity) throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM asset_user WHERE user_id=? AND asset_id=?";
		jdbcTemplate.update(sql, logEntity.getAssetUserId(),
				logEntity.getAssetInfoId());
	}

	@Override
	public List<AssetUser> getAssetUserById(String userId) throws APIExceptions {
		updateDataSource();
		return jdbcTemplate.query("SELECT * FROM asset_user WHERE user_id=?",
				new RowMapper<AssetUser>() {
					@Override
					public AssetUser mapRow(ResultSet rs, int rownumber)
							throws SQLException {
						AssetUser logEntity = new AssetUser();
						logEntity.setAssetInfoId(rs.getInt("asset_id"));
						logEntity.setAssetUserId(rs.getString("user_id"));
						logEntity.setStartDate(rs.getTimestamp("start_date"));
						logEntity.setEndDate(rs.getTimestamp("end_date"));
						logEntity.setCreatedBy(rs.getString("created_by"));
						logEntity.setModifiedBy(rs.getString("modified_by"));
						logEntity.setModifiedDate(rs
								.getTimestamp("created_date"));
						logEntity.setModifiedDate(rs
								.getTimestamp("modified_date"));

						return logEntity;
					}
				}, userId);
	}

	@Override
	public AssetUser importAssetUser(AssetUser logEntity) throws APIExceptions {
		updateDataSource();
		persistAssetUser(logEntity);
		return null;
	}

	@Override
	public List<AssetUser> getAllAssetUserRowMapper() throws APIExceptions {
		updateDataSource();
		return jdbcTemplate.query("SELECT * FROM asset_user",
				new RowMapper<AssetUser>() {
					@Override
					public AssetUser mapRow(ResultSet rs, int rownumber)
							throws SQLException {
						AssetUser logEntity = new AssetUser();

						logEntity.setAssetInfoId(rs.getInt("asset_id"));
						logEntity.setAssetUserId(rs.getString("user_id"));
						logEntity.setStartDate(rs.getTimestamp("start_date"));
						logEntity.setEndDate(rs.getTimestamp("end_date"));
						logEntity.setCreatedBy(rs.getString("created_by"));
						logEntity.setModifiedBy(rs.getString("modified_by"));
						logEntity.setModifiedDate(rs
								.getTimestamp("created_date"));
						logEntity.setModifiedDate(rs
								.getTimestamp("modified_date"));
						return logEntity;
					}
				});
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
