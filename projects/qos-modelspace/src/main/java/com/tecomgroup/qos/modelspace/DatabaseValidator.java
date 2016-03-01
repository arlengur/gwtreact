/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.modelspace;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.api.MigrationInfoService;
import com.tecomgroup.qos.exception.ModelSpaceException;

/**
 * @see {@link #validate()}, {@link #setValidationMode(String)}
 * 
 * @author sviyazov.a
 * 
 */
public class DatabaseValidator {

	@Autowired
	private DataSource dataSource;

	private String validationMode;

	private String migrationDialect;

	private final static String[] DEFAULT_MIGRATION_BASE_PACKAGE = new String[]{"com.tecomgroup.qos.migration"};

	private String[] migrationBasePackages = DEFAULT_MIGRATION_BASE_PACKAGE;

	private final static String MODE_VALIDATE = "validate";
	private final static String MODE_INIT_AND_MIGRATE = "init_and_migrate";
	private final static String MODE_CLEAN_INIT_AND_MIGRATE = "clean_init_and_migrate";

	private Flyway createAndConfigureFlyway() {
		final Flyway flyway = new Flyway();
		flyway.setDataSource(dataSource);

		if (migrationDialect == null || migrationDialect.trim().isEmpty()) {
			// Find dialect from datasource
			if (dataSource instanceof BasicDataSource) {
				final String driver = ((BasicDataSource) dataSource)
						.getDriverClassName();
				if (driver.contains("org.hsqldb")) {
					migrationDialect = "hsql";
				} else if (driver.contains("org.postgresql")) {
					migrationDialect = "postgres";
				}
			}
		}

		Assert.notNull(migrationDialect,
				"Dialect is not selected. <hsql> and <postgres> dialects are supported");

		final List<String> locations = new ArrayList<String>();
		for (final String location : migrationBasePackages) {
			locations.add(location + ".common");
			locations.add(location + "." + migrationDialect.toLowerCase());
		}
		flyway.setLocations(locations.toArray(new String[0]));
		return flyway;
	}

	public void setDataSource(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @param migrationBasePackages
	 *            the migrationBasePackages to set
	 */
	public void setMigrationBasePackages(final String[] migrationBasePackages) {
		this.migrationBasePackages = migrationBasePackages;
	}

	/**
	 * Default value {@value #DEFAULT_MIGRATION_BASE_PACKAGE}
	 * 
	 * @param migrationDialect
	 */
	public void setMigrationDialect(final String migrationDialect) {
		this.migrationDialect = migrationDialect;
	}

	/**
	 * Sets mode for {@link #validate()}.
	 * <p>
	 * Supported values: <br/>
	 * {@value #MODE_VALIDATE} - validates that all migrations are applied, if
	 * not - terminates process<br/>
	 * {@value #MODE_INIT_AND_MIGRATE} - applies all pending migrations,
	 * initializes version if needed<br/>
	 * {@value #MODE_CLEAN_INIT_AND_MIGRATE} - cleans database, then inits and
	 * applies all pending migrations <br/>
	 * "none" (or any other value) - no action is performed
	 * </p>
	 * <p>
	 * By default {@value #MODE_VALIDATE} should be used in production,
	 * {@value #MODE_INIT_AND_MIGRATE} in development mode, and
	 * {@value #MODE_CLEAN_INIT_AND_MIGRATE} for unit tests.
	 * </p>
	 * 
	 * @param validationMode
	 */
	public void setValidationMode(final String validationMode) {
		this.validationMode = validationMode;
	}

	/**
	 * Validates database version of {@link #dataSource}. If invalid, logs error
	 * and terminates JVM.
	 * 
	 * @throws ModelSpaceException
	 */
	public void validate() throws ModelSpaceException {
		if (MODE_VALIDATE.equalsIgnoreCase(validationMode)) {
			final Flyway flyway = createAndConfigureFlyway();

			final MigrationInfoService migrationInfoService = flyway.info();

			if (migrationInfoService.pending().length > 0) {
				throw new ModelSpaceException(
						"The database is outdated. Please apply new migrations via command-line tool.");
			}
		} else if (MODE_INIT_AND_MIGRATE.equalsIgnoreCase(validationMode)
				|| MODE_CLEAN_INIT_AND_MIGRATE.equalsIgnoreCase(validationMode)) {
			final Flyway flyway = createAndConfigureFlyway();

			if (MODE_CLEAN_INIT_AND_MIGRATE.equalsIgnoreCase(validationMode)) {
				flyway.clean();
			}

			flyway.setInitVersion("0");
			flyway.setInitOnMigrate(true);
			flyway.migrate();
		}
	}
}
