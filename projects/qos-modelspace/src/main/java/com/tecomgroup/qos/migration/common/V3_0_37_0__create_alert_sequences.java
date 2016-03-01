/*
 * Copyright (C) 2015 Qligent.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.migration.common;

import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author uvarov.m
 */
public class V3_0_37_0__create_alert_sequences implements SpringJdbcMigration {

	@Override
	public void migrate(final JdbcTemplate jdbcTemplate) throws Exception {

		// Alert sequence
		createSequence(jdbcTemplate, "malert", "malert_id_seq");

		// Alert update sequence
		createSequence(jdbcTemplate, "malertupdate", "malertupdate_id_seq");

		// Alert report sequence
		createSequence(jdbcTemplate, "malertreport", "malertreport_id_seq");
	}

	private void createSequence(JdbcTemplate jdbcTemplate, String tableName, String sequenceName) {
		final Integer seqExistsNum = jdbcTemplate.queryForObject(
				"SELECT count(*) FROM INFORMATION_SCHEMA.SEQUENCES WHERE LOWER(SEQUENCE_NAME)=?;",
				Integer.class,
				sequenceName);

		if (seqExistsNum == null || seqExistsNum == 0) {
			Integer alertSequenceMaxId = null;
			try {
				alertSequenceMaxId = jdbcTemplate.queryForObject("SELECT max(id) FROM " + tableName + ";",
						Integer.class);
			} catch (EmptyResultDataAccessException e) {
				System.out.println("hibernate sequence not found: " + sequenceName);
			}

			if (alertSequenceMaxId == null) {
				alertSequenceMaxId = 0;
			}

			jdbcTemplate.execute(String.format("CREATE SEQUENCE %s START WITH %d INCREMENT BY 1;",
					sequenceName,
					alertSequenceMaxId + 1000));
		}
	}

}
