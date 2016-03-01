/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.migration.common;

import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;
import com.tecomgroup.qos.domain.pm.MPolicyActionWithContacts;
import com.tecomgroup.qos.domain.pm.MPolicySendEmail;
import com.tecomgroup.qos.domain.pm.MPolicySendSms;
import com.tecomgroup.qos.modelspace.hibernate.HibernateEntityConverter;

/**
 * @author kunilov.p
 * 
 */
public class V1_0_28__create_policyactionwithcontacts_sequence
		implements
			SpringJdbcMigration {

	@Override
	public void migrate(final JdbcTemplate jdbcTemplate) throws Exception {
		final String sequenceName = MPolicyActionWithContacts.class
				.getSimpleName();

		final boolean sequenceExists = jdbcTemplate
				.queryForInt(
						"SELECT count(*) FROM HIBERNATE_SEQUENCES WHERE SEQUENCE_NAME = ?",
						sequenceName) > 0;

		if (!sequenceExists) {
			final String policySendEmailActionEntityName = HibernateEntityConverter
					.getEntityAnnotationName(MPolicySendEmail.class);
			final String policySendSmsActionEntityName = HibernateEntityConverter
					.getEntityAnnotationName(MPolicySendSms.class);

			final int policySendEmailActionMaxId = jdbcTemplate
					.queryForInt("SELECT max(id) FROM "
							+ policySendEmailActionEntityName);
			final int policySendSmsActionMaxId = jdbcTemplate
					.queryForInt("SELECT max(id) FROM "
							+ policySendSmsActionEntityName);
			jdbcTemplate
					.update("INSERT INTO HIBERNATE_SEQUENCES (SEQUENCE_NAME, SEQUENCE_NEXT_HI_VALUE) VALUES (?, ?)",
							sequenceName, Math.max(policySendEmailActionMaxId,
									policySendSmsActionMaxId) + 1);
		}
	}
}
