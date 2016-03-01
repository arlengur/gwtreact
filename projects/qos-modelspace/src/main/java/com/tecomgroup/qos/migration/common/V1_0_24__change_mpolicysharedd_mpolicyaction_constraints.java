package com.tecomgroup.qos.migration.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;

/**
 * @author meleshin.o
 */
public class V1_0_24__change_mpolicysharedd_mpolicyaction_constraints
		implements
			SpringJdbcMigration {

	private static final String COUNT_TABLES_QUERY = "SELECT count(*) FROM INFORMATION_SCHEMA.TABLES"
			+ " WHERE LOWER(TABLE_NAME)=?";

	private static final String JOIN_TABLE_NAME = "mpolicysharedd_mpolicyaction";

	@Override
	public void migrate(final JdbcTemplate jdbcTemplate) throws Exception {
		if (jdbcTemplate.queryForInt(COUNT_TABLES_QUERY, JOIN_TABLE_NAME) > 0) {
			final List<String> uniqueConstraints = jdbcTemplate.query(
					"SELECT CONSTRAINT_NAME FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS"
							+ " WHERE LOWER(TABLE_NAME)=?"
							+ " AND UPPER(CONSTRAINT_TYPE)='UNIQUE'",
					new String[]{JOIN_TABLE_NAME}, new RowMapper<String>() {

						@Override
						public String mapRow(final ResultSet resultSet,
								final int rowNum) throws SQLException {
							return resultSet.getString(1);
						}
					});

			for (final String uniqueConstraint : uniqueConstraints) {
				jdbcTemplate.execute("ALTER TABLE " + JOIN_TABLE_NAME
						+ " DROP CONSTRAINT " + uniqueConstraint);
			}

			jdbcTemplate.execute("ALTER TABLE " + JOIN_TABLE_NAME
					+ " ADD CONSTRAINT " + JOIN_TABLE_NAME
					+ "_unique_actions_id UNIQUE (actions_id)");
		}
	}
}
