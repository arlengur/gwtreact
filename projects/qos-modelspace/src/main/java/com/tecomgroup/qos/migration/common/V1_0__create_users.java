/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.migration.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;

public class V1_0__create_users implements SpringJdbcMigration {

	@Override
	public void migrate(final JdbcTemplate jdbcTemplate) throws Exception {

		final PasswordEncoder encoder = new Md5PasswordEncoder();
		final int userId = 1;
		final String userLogin = "User";
		final String userPasswordHash = encoder.encodePassword("u", null);
		final int adminId = 2;
		final String adminLogin = "Admin";
		final String adminPasswordHash = encoder.encodePassword("a", null);

		final List<String> existingUserLogins = jdbcTemplate.query(
				"SELECT LOGIN FROM MUSER", new RowMapper<String>() {

					@Override
					public String mapRow(final ResultSet resultSet,
							final int rowNum) throws SQLException {
						return resultSet.getString(1);
					}
				});

		if (!existingUserLogins.contains(userLogin)) {
			jdbcTemplate
					.update("INSERT INTO MUSER (ID, LOGIN, PASSWORD) VALUES (?, ?, ?), (?, ?, ?)",
							userId, userLogin, userPasswordHash, adminId,
							adminLogin, adminPasswordHash);
		}

		if (!existingUserLogins.contains(adminLogin)) {
			jdbcTemplate
					.update("INSERT INTO MUSER_ROLES (MUSER_ID, ROLES) VALUES (?, ?), (?, ?), (?, ?)",
							userId, "ROLE_USER", adminId, "ROLE_USER", adminId,
							"ROLE_ADMIN");
		}
	}
}
