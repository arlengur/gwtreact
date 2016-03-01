/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.migration.common;

import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class V3_0_47_1__remove_user_role_from_admin implements SpringJdbcMigration {

	@Override
	public void migrate(final JdbcTemplate jdbcTemplate) throws Exception {

		final int adminId = 2;
		final String adminLogin = "Admin";

		final List<String> existingUserLogins = jdbcTemplate.query(
				"SELECT LOGIN FROM MUSER", new RowMapper<String>() {

					@Override
					public String mapRow(final ResultSet resultSet,
							final int rowNum) throws SQLException {
						return resultSet.getString(1);
					}
				});

		if (existingUserLogins.contains(adminLogin)) {
			jdbcTemplate
					.update("DELETE FROM MUSER_ROLES WHERE MUSER_ID = ? AND ROLES = 'ROLE_USER' ;",	adminId);
		}
	}
}
