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

import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;

/**
 * What it does: <br/>
 * 1. Finds names of all 'FOREIGN KEY' constraints affecting table
 * 'mpolicysharedd_mpolicysendal' <br/>
 * 2. Drops all these constraints <br/>
 * 3. Renames table 'mpolicysharedd_mpolicysendal' to
 * 'mpolicysharedd_mpolicyaction' <br/>
 * 4. Adds unique constraint on (mpolicyshareddata_id, actions_id) pair <br/>
 * 
 * @author novohatskiy.r
 * 
 */
public class V1_0_19__refactor_mpolicyshareddata_mpolicyaction_relation
		implements
			SpringJdbcMigration {

	private static final String COUNT_TABLES_QUERY = "SELECT count(*) FROM INFORMATION_SCHEMA.TABLES"
			+ " WHERE LOWER(TABLE_NAME)=?";

	private static final String JOIN_TABLE_NAME = "mpolicysharedd_mpolicysendal";
	private static final String NEW_JOIN_TABLE_NAME = "mpolicysharedd_mpolicyaction";

	@Override
	public void migrate(final JdbcTemplate jdbcTemplate) throws Exception {

		if (jdbcTemplate.queryForInt(COUNT_TABLES_QUERY, JOIN_TABLE_NAME) > 0) {
			if (jdbcTemplate.queryForInt(COUNT_TABLES_QUERY,
					NEW_JOIN_TABLE_NAME) == 0) {
				final List<String> foreignKeys = jdbcTemplate.query(
						"SELECT CONSTRAINT_NAME FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS"
								+ " WHERE LOWER(TABLE_NAME)=?"
								+ " AND UPPER(CONSTRAINT_TYPE)='FOREIGN KEY'",
						new String[]{JOIN_TABLE_NAME}, new RowMapper<String>() {

							@Override
							public String mapRow(final ResultSet resultSet,
									final int rowNum) throws SQLException {
								return resultSet.getString(1);
							}
						});
				for (final String fk : foreignKeys) {
					jdbcTemplate.execute("ALTER TABLE " + JOIN_TABLE_NAME
							+ " DROP CONSTRAINT " + fk);
				}

				jdbcTemplate.execute("ALTER TABLE " + JOIN_TABLE_NAME
						+ " RENAME TO " + NEW_JOIN_TABLE_NAME);

				jdbcTemplate.execute("ALTER TABLE " + NEW_JOIN_TABLE_NAME
						+ " ADD UNIQUE (mpolicyshareddata_id, actions_id)");
			} else {
				jdbcTemplate.execute("DROP TABLE " + JOIN_TABLE_NAME);
			}
		}

	}

}
