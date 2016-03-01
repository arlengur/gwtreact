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

public class V3_0_47_2__create_super_admin implements SpringJdbcMigration {

	@Override
	public void migrate(final JdbcTemplate jdbcTemplate) throws Exception {

		final PasswordEncoder encoder = new Md5PasswordEncoder();
		final int adminId = 123456;
		final String adminLogin = "SuperAdmin";
		final String adminPasswordHash = encoder.encodePassword("sa", null);

		final List<String> existingUserLogins = jdbcTemplate.query(
				"SELECT LOGIN FROM MUSER", new RowMapper<String>() {

					@Override
					public String mapRow(final ResultSet resultSet,
							final int rowNum) throws SQLException {
						return resultSet.getString(1);
					}
				});

		if (!existingUserLogins.contains(adminLogin)) {
			jdbcTemplate
					.update("INSERT INTO MUSER (ID, LOGIN, PASSWORD, ldap_authenticated, disabled) VALUES (?, ?, ?, ?, ?) ; ",
							adminId, adminLogin, adminPasswordHash, false, false);
		}

		if (!existingUserLogins.contains(adminLogin)) {
			jdbcTemplate
					.update("INSERT INTO MUSER_ROLES (MUSER_ID, ROLES) VALUES (?, ?) ;",
							adminId, "ROLE_SUPER_ADMIN");
		}
	}
}
