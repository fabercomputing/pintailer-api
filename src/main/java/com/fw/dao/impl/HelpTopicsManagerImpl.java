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

import com.fw.dao.IHelpTopicManager;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.HelpTopics;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;
import com.fw.utils.ApplicationCommonUtil;

@Repository
public class HelpTopicsManagerImpl implements IHelpTopicManager {

	private Logger log = Logger.getLogger(HelpTopicsManagerImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	public HelpTopics persistHelpTopicsInfo(HelpTopics logEntity)
			throws APIExceptions {
		updateDataSource();
		KeyHolder requestKeyHolder = new GeneratedKeyHolder();
		try {
			String sql = "INSERT INTO help_topics (title, topic_parent_id, "
					+ "created_by, modified_by) VALUES(?,?,?,?)";

			final String currentUser = applicationCommonUtil.getCurrentUser();

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "topic_id" });
					ps.setString(1, logEntity.getTitle());
					ps.setInt(2, logEntity.getTopicParentId());
					ps.setString(3, currentUser);
					ps.setString(4, currentUser);
					return ps;
				}
			}, requestKeyHolder);
			int logEntityId = requestKeyHolder.getKey().intValue();
			logEntity.setTopicId(logEntityId);
			return logEntity;
		} catch (DataAccessException e) {
			log.error(e.getMessage());
			return null;
		}

	}

	@Override
	public void updateHelpTopicsById(HelpTopics logEntity) throws APIExceptions {
		updateDataSource();
		String sql = "UPDATE help_topics SET title=?, topic_parent_id=?, modified_by=? "
				+ "WHERE topic_id=?";

		jdbcTemplate.update(sql, logEntity.getTitle(),
				logEntity.getTopicParentId(),
				applicationCommonUtil.getCurrentUser(), logEntity.getTopicId());

	}

	@Override
	public void deleteHelpTopicsById(HelpTopics logEntity) throws APIExceptions {
		updateDataSource();
		String sql = "DELETE FROM help_topics WHERE topic_id=?";
		try {
			jdbcTemplate.update(sql, logEntity.getTopicId());
		} catch (DataAccessException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public List<HelpTopics> getAllHelpTopicsRowMapper() throws APIExceptions {
		updateDataSource();
		return jdbcTemplate.query(
				"SELECT * FROM help_topics ORDER BY topic_id",
				new RowMapper<HelpTopics>() {
					@Override
					public HelpTopics mapRow(ResultSet rs, int rownumber)
							throws SQLException {
						HelpTopics logEntity = new HelpTopics();
						logEntity.setTopicId(rs.getInt("topic_id"));
						logEntity.setTitle(rs.getString("title"));
						logEntity.setTopicParentId(rs.getInt("topic_parent_id"));
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
	public HelpTopics getHelpTopicsById(int Id) throws APIExceptions {
		updateDataSource();
		try {
			return jdbcTemplate.queryForObject(
					"SELECT * FROM help_topics WHERE topic_id=?",
					new RowMapper<HelpTopics>() {
						@Override
						public HelpTopics mapRow(ResultSet rs, int rownumber)
								throws SQLException {
							HelpTopics logEntity = new HelpTopics();
							logEntity.setTopicId(rs.getInt("topic_id"));
							logEntity.setTitle(rs.getString("title"));
							logEntity.setTopicParentId(rs
									.getInt("topic_parent_id"));
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

	// First element of the list will contain the required complete hierarchy
	@Override
	public List<String> getHelpTopicsHierarchy(int helpTopicsId)
			throws APIExceptions {
		updateDataSource();
		String query = "WITH RECURSIVE test(topic_parent_id, level, title, name_path)"
				+ " AS (SELECT topic_parent_id, 0, title, Array[concat(title,'"
				+ PintailerConstants.HELP_TOPICS_HIERARCHY_SEPARATOR
				+ "',topic_id)]"
				+ " FROM help_topics WHERE topic_id = ? UNION ALL"
				+ " SELECT p.topic_parent_id, pr.level+1, p.title, "
				+ "ARRAY_APPEND(pr.name_path, CONCAT(p.title,'"
				+ PintailerConstants.HELP_TOPICS_HIERARCHY_SEPARATOR
				+ "', p.topic_id))"
				+ " FROM test pr, help_topics p WHERE "
				+ "p.topic_id = pr.topic_parent_id)"
				+ " SELECT name_path as helpTopicHierarchy, max(level) as lvl FROM test"
				+ " GROUP BY name_path ORDER BY lvl DESC";
		return jdbcTemplate.query(query, new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rownumber)
					throws SQLException {
				return rs.getString("helpTopicHierarchy");
			}
		}, helpTopicsId);
	}

	@Override
	public List<Long> getAllChildHelpTopics(int parentHelpTopicId)
			throws APIExceptions {
		updateDataSource();
		String query = "WITH RECURSIVE getAllchilds (topic_id) AS "
				+ "(SELECT topic_id, title, topic_parent_id FROM help_topics "
				+ "WHERE topic_id=? UNION ALL "
				+ "SELECT m.topic_id, m.title, m.topic_parent_id FROM help_topics m "
				+ "JOIN getAllchilds ON getAllchilds.topic_id = m.topic_parent_id) "
				+ "SELECT topic_id, title, topic_parent_id FROM getAllchilds ORDER BY topic_id";
		return jdbcTemplate.query(query, new RowMapper<Long>() {
			@Override
			public Long mapRow(ResultSet rs, int rownumber) throws SQLException {
				return rs.getLong("module_id");
			}
		}, parentHelpTopicId);
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(PintailerConstants.COMMON_ORG);
	}
}
