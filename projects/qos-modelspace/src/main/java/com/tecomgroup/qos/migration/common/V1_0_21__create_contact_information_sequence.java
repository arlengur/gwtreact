/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.migration.common;

import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;
import com.tecomgroup.qos.domain.MContactInformation;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.MUserGroup;

/**
 * @author meleshin.o
 * 
 */
public class V1_0_21__create_contact_information_sequence
		implements
			SpringJdbcMigration {

	@Override
	public void migrate(final JdbcTemplate jdbcTemplate) throws Exception {

		final String sequenceName = MContactInformation.class.getSimpleName();

		final boolean sequenceExists = jdbcTemplate
				.queryForInt(
						"SELECT count(*) FROM HIBERNATE_SEQUENCES WHERE SEQUENCE_NAME = ?",
						sequenceName) > 0;

		if (!sequenceExists) {
			final int userMaxId = jdbcTemplate
					.queryForInt("SELECT max(id) FROM "
							+ MUser.class.getSimpleName());
			final int groupMaxId = jdbcTemplate
					.queryForInt("SELECT max(id) FROM "
							+ MUserGroup.class.getSimpleName());
			jdbcTemplate
					.update("INSERT INTO HIBERNATE_SEQUENCES (SEQUENCE_NAME, SEQUENCE_NEXT_HI_VALUE) VALUES (?, ?)",
							sequenceName, Math.max(userMaxId, groupMaxId) + 1);
		}
	}
}
