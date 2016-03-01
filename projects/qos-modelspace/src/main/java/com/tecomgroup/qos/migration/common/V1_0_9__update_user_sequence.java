/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.migration.common;

import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;
import com.tecomgroup.qos.domain.MUser;

/**
 * @author meleshin.o
 * 
 */
public class V1_0_9__update_user_sequence implements SpringJdbcMigration {

	@Override
	public void migrate(final JdbcTemplate jdbcTemplate) throws Exception {

		final String sequenceName = MUser.class.getSimpleName();

		final boolean sequenceExists = jdbcTemplate
				.queryForInt(
						"SELECT count(*) FROM HIBERNATE_SEQUENCES WHERE SEQUENCE_NAME = ?",
						sequenceName) > 0;

		if (!sequenceExists) {
			final int userMaxId = jdbcTemplate
					.queryForInt("SELECT max(id) FROM " + sequenceName);
			jdbcTemplate
					.update("INSERT INTO HIBERNATE_SEQUENCES (SEQUENCE_NAME, SEQUENCE_NEXT_HI_VALUE) VALUES (?, ?)",
							sequenceName, userMaxId + 1);
		}
	}
}
