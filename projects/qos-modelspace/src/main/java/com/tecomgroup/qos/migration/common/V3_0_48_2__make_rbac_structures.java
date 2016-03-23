package com.tecomgroup.qos.migration.common;


import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.tecomgroup.qos.domain.rbac.UISubject;
import com.tecomgroup.qos.domain.rbac.MRole;
import com.tecomgroup.qos.domain.rbac.PermissionScope;
import com.tecomgroup.qos.domain.rbac.PredefinedRoles;
import com.tecomgroup.qos.modelspace.jdbc.dao.JdbcRbacSequenceDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.sql.Array;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.math.BigDecimal;

/**
 * Created by kiselev.a on Fri Feb 26 12:12:47 2016.
 */

public class V3_0_48_2__make_rbac_structures implements SpringJdbcMigration {
	@Override
	public void migrate(final JdbcTemplate jdbcTemplate) throws Exception {
		JdbcRbacSequenceDao seq = new JdbcRbacSequenceDao();
		seq.initStoredProcedure(jdbcTemplate);
		
		for (UISubject uis : PermissionScope.asArray()) {
			if(uis.getId() == null) { // impossible situation
				uis.setId(seq.getNextSeqId());
			}

			jdbcTemplate.update
				("INSERT INTO uisubject (id, name, extension) VALUES (?, ?, ?)",
				 uis.getId(),
				 uis.getName(),
				 uis.getExtension()); // TODO: do it as below
		}

		try(Connection conn = jdbcTemplate.getDataSource().getConnection()) {
				for(MRole iRole : PredefinedRoles.asArray()) {
					final MRole role = iRole;
					final Array arrayParameter = conn
						.createArrayOf("bigint",
									   role.getSubjectsId());

					if(role.getId() == null) {
						role.setId(seq.getNextSeqId());
					}

					jdbcTemplate.update(
						"INSERT INTO mrole (id, name, subjects, comment) VALUES(?, ?, ?, ?)",
						new PreparedStatementSetter() {
							@Override
							public void setValues(PreparedStatement ps) {
								try {
									ps.setLong(1, role.getId());
									ps.setString(2, role.getName());
									ps.setArray(3, arrayParameter);
									ps.setString(4, role.getComment());
									
								} catch(SQLException sqle) {
									sqle.printStackTrace(); // FIXME: logging need
								}
							}
						});
				}
			}

		List<List<String>> currentRoles = jdbcTemplate.query(
			"SELECT muser_id, roles  FROM muser_roles",
			new RowMapper<List<String>>() {
				public List<String> mapRow(ResultSet rs, int cn) throws SQLException,  DataAccessException {
					List<String> rst = new ArrayList<String>();
					Long tmp = rs.getLong("muser_id");
					rst.add(tmp.toString());
					rst.add(rs.getString("roles")
							.substring(4)
							.replaceAll("_", ""));
					return rst;
				}
			});

		//at this moment all id initiated
		for(List<String> uRole : currentRoles) {
			MRole accordingNew = PredefinedRoles.findByNameIgnoreCase(uRole.get(1));
			
			if(accordingNew != null) {
				try(Connection conn = jdbcTemplate.getDataSource().getConnection()) { //something strange in hsql
						try(PreparedStatement ps = conn.prepareStatement("UPDATE muser SET roles=array_append(roles, ?) WHERE id = ?")) {
								ps.setLong(1, accordingNew.getId());
								ps.setLong(2, Long.parseLong(uRole.get(0)));
								ps.executeUpdate();
							}
					}
			}
		}
	}
}
