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

import com.fw.dao.IAssetInfoManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.AssetInfo;
import com.fw.exceptions.APIExceptions;
import com.fw.utils.ApplicationCommonUtil;

@Repository
public class AssetInfoManagerImpl implements IAssetInfoManager {

	private Logger log = Logger.getLogger(AssetInfoManagerImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	public AssetInfo persistAssetInfo(AssetInfo assetInfo) throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO asset_info (asset_type_id, name, brand, model, serial,"
					+ " processor, ram, hd_type, hd_size, os_version, os_type, "
					+ "comments, imei, created_by, modified_by, asset_status) "
					+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "asset_id" });
					ps.setLong(1, assetInfo.getAssetTypeId());
					ps.setString(2, assetInfo.getName());
					ps.setString(3, assetInfo.getBrand());
					ps.setString(4, assetInfo.getModel());
					ps.setString(5, assetInfo.getSerial());
					ps.setString(6, assetInfo.getProcessor());
					ps.setLong(7, assetInfo.getRam());
					ps.setString(8, assetInfo.getHd_type());
					ps.setLong(9, assetInfo.getHd_size());
					ps.setString(10, assetInfo.getOs_version());
					ps.setString(11, assetInfo.getOs_type());
					ps.setString(12, assetInfo.getComments());
					ps.setLong(13, assetInfo.getImei());
					ps.setString(14, currentUser);
					ps.setString(15, currentUser);
					ps.setString(16, assetInfo.getStatus());
					return ps;
				}
			}, requestKeyHolder);
			int logEntityId = requestKeyHolder.getKey().intValue();
			assetInfo.setAssetInfoId(logEntityId);
			return assetInfo;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}

	}

	@Override
	public void updateAssetInfoById(AssetInfo assetInfo) throws APIExceptions {
		updateDataSource();
		String sql = "UPDATE asset_info SET name=?, brand=?, model=?, serial=?, "
				+ "processor=?, ram=?, hd_type=?, hd_size=?, os_version=?, "
				+ "os_type=?, comments=?, imei=?, modified_by=?, asset_status=?  "
				+ "WHERE asset_id=?";

		jdbcTemplate.update(sql, assetInfo.getName(), assetInfo.getBrand(),
				assetInfo.getModel(), assetInfo.getSerial(),
				assetInfo.getProcessor(), assetInfo.getRam(),
				assetInfo.getHd_type(), assetInfo.getHd_size(),
				assetInfo.getOs_version(), assetInfo.getOs_type(),
				assetInfo.getComments(), assetInfo.getImei(),
				applicationCommonUtil.getCurrentUser(), assetInfo.getStatus(),
				assetInfo.getAssetInfoId());

	}

	@Override
	public void deleteAssetInfoById(AssetInfo assetInfo) throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM asset_info WHERE asset_id=?";
		try {
			jdbcTemplate.update(sql, assetInfo.getAssetInfoId());
		} catch (DataAccessException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public AssetInfo getAssetInfoById(int assetId) throws APIExceptions {
		updateDataSource();
		try {
			return jdbcTemplate.queryForObject(
					"SELECT * FROM asset_info WHERE asset_id=?",
					new RowMapper<AssetInfo>() {
						@Override
						public AssetInfo mapRow(ResultSet rs, int rownumber)
								throws SQLException {
							AssetInfo assetInfo = new AssetInfo();
							assetInfo.setAssetInfoId(rs.getInt("asset_id"));
							assetInfo.setAssetTypeId(rs.getInt("asset_type_id"));
							assetInfo.setName(rs.getString("name"));
							assetInfo.setBrand(rs.getString("brand"));
							assetInfo.setModel(rs.getString("model"));
							assetInfo.setSerial(rs.getString("serial"));
							assetInfo.setProcessor(rs.getString("processor"));
							assetInfo.setRam(rs.getInt("ram"));
							assetInfo.setHd_type(rs.getString("hd_type"));
							assetInfo.setHd_size(rs.getInt("hd_size"));
							assetInfo.setOs_version(rs.getString("os_version"));
							assetInfo.setOs_type(rs.getString("os_type"));
							assetInfo.setComments(rs.getString("comments"));
							assetInfo.setImei(rs.getLong("imei"));
							assetInfo.setCreatedBy(rs.getString("created_by"));
							assetInfo.setModifiedBy(rs.getString("modified_by"));
							assetInfo.setCreatedDate(rs
									.getTimestamp("created_date"));
							assetInfo.setModifiedDate(rs
									.getTimestamp("modified_date"));
							assetInfo.setStatus(rs.getString("asset_status"));
							return assetInfo;
						}
					}, assetId);
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public List<AssetInfo> getAllAssetInfoRowMapper() throws APIExceptions {
		updateDataSource();
		return jdbcTemplate.query("SELECT * FROM asset_info ORDER BY asset_id",
				new RowMapper<AssetInfo>() {
					@Override
					public AssetInfo mapRow(ResultSet rs, int rownumber)
							throws SQLException {
						AssetInfo assetInfo = new AssetInfo();
						assetInfo.setAssetInfoId(rs.getInt("asset_id"));
						assetInfo.setAssetTypeId(rs.getInt("asset_type_id"));
						assetInfo.setName(rs.getString("name"));
						assetInfo.setBrand(rs.getString("brand"));
						assetInfo.setModel(rs.getString("model"));
						assetInfo.setSerial(rs.getString("serial"));
						assetInfo.setProcessor(rs.getString("processor"));
						assetInfo.setRam(rs.getInt("ram"));
						assetInfo.setHd_type(rs.getString("hd_type"));
						assetInfo.setHd_size(rs.getInt("hd_size"));
						assetInfo.setOs_version(rs.getString("os_version"));
						assetInfo.setOs_type(rs.getString("os_type"));
						assetInfo.setComments(rs.getString("comments"));
						assetInfo.setImei(rs.getLong("imei"));
						assetInfo.setCreatedBy(rs.getString("created_by"));
						assetInfo.setModifiedBy(rs.getString("modified_by"));
						assetInfo.setCreatedDate(rs
								.getTimestamp("created_date"));
						assetInfo.setModifiedDate(rs
								.getTimestamp("modified_date"));
						assetInfo.setStatus(rs.getString("asset_status"));

						return assetInfo;
					}
				});
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
