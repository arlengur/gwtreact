package com.tecomgroup.qos.modelspace.jdbc.dao;

import com.tecomgroup.qos.domain.rbac.*;
import org.hsqldb.types.Types;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by kiselev.a on Fri Feb 26 17:30:56 2016.
 */

public class JdbcRbacUISubjectServiceDao extends JdbcRbacSequenceDao 
	implements RbacUISubjectServiceDao {

	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedJdbcTemplate;

	public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate	   = new JdbcTemplate(dataSource);
        this.jdbcTemplate.setResultsMapCaseInsensitive(true);
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		super.initStoredProcedure(this.jdbcTemplate);
	}

	@Override
	public UISubject getSubject(String name) { //???
		for(UISubject uis : PermissionScope.asArray()) {
			if(uis.getName().equals(name)) {
				return uis;
			}
		}
		return null;
	}

	@Override
	public List<UISubject> getSubjectById(Long ... id) { 
		try {
			return namedJdbcTemplate.query
				("SELECT *  FROM  uisubject WHERE id IN (:list)",
				 Collections.singletonMap("list", Arrays.asList(id)),
				 new RowMapper<UISubject>() {
					public UISubject mapRow(ResultSet rs, int rowNum) throws SQLException {
						UISubject tmpUis = new UISubject(rs.getString("name")); // TODO: need to handle array of extension
						tmpUis.setId(rs.getLong("id"));
						return tmpUis;
					}
				});
		 } catch (EmptyResultDataAccessException e) {
			e.printStackTrace();// FIXME: logging need
			return null;
		}
	}

	@Override
	public Long insertSubject(UISubject uis) { //useless methods
		if(uis.getId() == null || uis.getId() < 1) {
			uis.setId(getNextSeqId());
		}

		this.jdbcTemplate.update
			("INSERT INTO uisubject VALUES(?, ?, ?)",
			 uis.getId(),
			 uis.getName(),
			 uis.getExtension()); // TODO: array handle

		return uis.getId();
	}

	@Override
	public void updateSubject(UISubject uis) {
		this.jdbcTemplate.update
			("update uisubject set name=?, extension=? where id=?",
			 uis.getName(),
			 uis.getExtension(),
			 uis.getId());
	}
}
