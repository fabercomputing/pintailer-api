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

import com.fw.dao.IAssetTypeManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.AssetType;
import com.fw.exceptions.APIExceptions;
import com.fw.utils.ApplicationCommonUtil;

@Repository
public class AssetTypeManagerImpl implements IAssetTypeManager {

	private Logger log = Logger.getLogger(AssetTypeManagerImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	public AssetType persistAssetType(AssetType assetType) throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO asset_type (asset_type_name, created_by, "
					+ "modified_by) VALUES(?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "asset_type_id" });
					ps.setString(1, assetType.getAssetTypeName());
					ps.setString(2, currentUser);
					ps.setString(3, currentUser);
					return ps;
				}
			}, requestKeyHolder);
			int logEntityId = requestKeyHolder.getKey().intValue();
			assetType.setAssetTypeId(logEntityId);
			return assetType;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}

	}

	@Override
	public void updateAssetTypeById(AssetType assetType) throws APIExceptions {
		updateDataSource();
		String sql = "UPDATE asset_type SET asset_type_name=?, modified_by=? "
				+ "WHERE asset_type_id=?";
		jdbcTemplate.update(sql, assetType.getAssetTypeName(),
				applicationCommonUtil.getCurrentUser(),
				assetType.getAssetTypeId());
	}

	@Override
	public void deleteAssetTypeById(AssetType Id) throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM asset_type WHERE asset_type_id=?";
		try {
			jdbcTemplate.update(sql, Id);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public AssetType getAssetTypeById(long assetTypeId) throws APIExceptions {
		updateDataSource();
		try {
			return jdbcTemplate.queryForObject(
					"SELECT * FROM asset_type WHERE asset_type_id=?",
					new RowMapper<AssetType>() {
						@Override
						public AssetType mapRow(ResultSet rs, int rownumber)
								throws SQLException {
							AssetType assetType = new AssetType();
							assetType.setAssetTypeId(rs.getInt("asset_type_id"));
							assetType.setAssetTypeName(rs
									.getString("asset_type_name"));
							assetType.setCreatedBy(rs.getString("created_by"));
							assetType.setModifiedBy(rs.getString("modified_by"));
							assetType.setModifiedDate(rs
									.getTimestamp("created_date"));
							assetType.setModifiedDate(rs
									.getTimestamp("modified_date"));
							return assetType;
						}
					}, assetTypeId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public AssetType importAssetType(AssetType assetType) throws APIExceptions {
		updateDataSource();
		persistAssetType(assetType);
		return null;
	}

	@Override
	public List<AssetType> getAllAssetTypeRowMapper() throws APIExceptions {
		updateDataSource();
		return jdbcTemplate.query("SELECT * FROM asset_type",
				new RowMapper<AssetType>() {
					@Override
					public AssetType mapRow(ResultSet rs, int rownumber)
							throws SQLException {
						AssetType assetType = new AssetType();
						assetType.setAssetTypeId(rs.getInt("asset_type_id"));
						assetType.setAssetTypeName(rs
								.getString("asset_type_name"));
						assetType.setCreatedBy(rs.getString("created_by"));
						assetType.setModifiedBy(rs.getString("modified_by"));
						assetType.setModifiedDate(rs
								.getTimestamp("created_date"));
						assetType.setModifiedDate(rs
								.getTimestamp("modified_date"));
						return assetType;
					}
				});
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
