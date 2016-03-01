/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.migration.common;

import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;
import com.tecomgroup.qos.domain.pm.MPolicyActionsTemplate;
import com.tecomgroup.qos.domain.pm.MPolicyComponentTemplate;
import com.tecomgroup.qos.domain.pm.MPolicyConditionsTemplate;
import com.tecomgroup.qos.modelspace.hibernate.HibernateEntityConverter;

/**
 * @author kunilov.p
 * 
 */
public class V1_0_37__create_policycomponenttemplate_sequence
		implements
			SpringJdbcMigration {

	@Override
	public void migrate(final JdbcTemplate jdbcTemplate) throws Exception {
		final String sequenceName = MPolicyComponentTemplate.class
				.getSimpleName();

		final boolean sequenceExists = jdbcTemplate
				.queryForInt(
						"SELECT count(*) FROM HIBERNATE_SEQUENCES WHERE SEQUENCE_NAME = ?",
						sequenceName) > 0;

		if (!sequenceExists) {
			final String policyActionsTemplateEntityName = HibernateEntityConverter
					.getEntityAnnotationName(MPolicyActionsTemplate.class);
			final String policyConditionsTemplateEntityName = HibernateEntityConverter
					.getEntityAnnotationName(MPolicyConditionsTemplate.class);

			final int policyActionsTemplateMaxId = jdbcTemplate
					.queryForInt("SELECT max(id) FROM "
							+ policyActionsTemplateEntityName);
			final int policyConditionsTemplateMaxId = jdbcTemplate
					.queryForInt("SELECT max(id) FROM "
							+ policyConditionsTemplateEntityName);
			jdbcTemplate
					.update("INSERT INTO HIBERNATE_SEQUENCES (SEQUENCE_NAME, SEQUENCE_NEXT_HI_VALUE) VALUES (?, ?)",
							sequenceName, Math.max(policyActionsTemplateMaxId,
									policyConditionsTemplateMaxId) + 1);
		}
	}
}
