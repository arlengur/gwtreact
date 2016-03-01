/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.tools.QoSTool;

/**
 * Generates DDL SQL scripts for various dialects based on hibernate schema
 * 
 * @author sviyazov.a
 * 
 */
@Component
public class GenerateHibernateDDLSql implements QoSTool {

	private static enum Dialect {
		POSTGRES("org.hibernate.dialect.PostgreSQLDialect"), HSQL(
				"org.hibernate.dialect.HSQLDialect");

		private String dialectClass;

		private Dialect(final String dialectClass) {
			this.dialectClass = dialectClass;
		}

		public String getDialectClass() {
			return dialectClass;
		}
	}

	private final static Logger LOGGER = Logger
			.getLogger(GenerateHibernateDDLSql.class);

	private List<Class<?>> entities;

	@Value("${ddl.domain.package}")
	private String packageName;

	@Value("${ddl.output.directory}")
	private String outputDirectory;

	@Autowired
	private NamingStrategy namingStrategy;

	public GenerateHibernateDDLSql() {

	}

	private List<Class<?>> collectClasses(final String packageName,
			final File directory) throws ClassNotFoundException {
		final List<Class<?>> classes = new ArrayList<>();
		if (directory.exists()) {
			final File[] files = directory.listFiles();
			for (final File file : files) {
				final String fileName = file.getName();
				if (fileName.endsWith(".class")) {
					// removes the .class extension
					classes.add(Class.forName(packageName + '.'
							+ fileName.substring(0, fileName.length() - 6)));
				} else {
					if (file.isDirectory()) {
						classes.addAll(collectClasses(
								packageName + '.' + file.getName(), file));
					}
				}
			}
		} else {
			throw new ClassNotFoundException(packageName
					+ " is not a valid package");
		}
		return classes;
	}

	@Override
	public void execute() {

		entities = new ArrayList<Class<?>>();
		try {
			for (final Class<?> clazz : getClasses(packageName)) {
				entities.add(clazz);
			}
		} catch (final Exception e) {
			LOGGER.error("Error gathering entities", e);
			return;
		}

		try {
			Files.createDirectories(Paths.get(outputDirectory));
			for (final Dialect dialect : Dialect.values()) {
				generate(dialect, outputDirectory);
			}
		} catch (final IOException e) {
			LOGGER.error("Error generating ddl scripts", e);
		}
	}

	/**
	 * Method that actually creates the file.
	 * 
	 * @param dialect
	 *            to use
	 */
	private void generate(final Dialect dialect, final String directory) {
		// configuration has to be created anew for each Dialect
		final Configuration configuration = new Configuration();
		configuration.setProperty("hibernate.hbm2ddl.auto", "create");
		configuration.setNamingStrategy(namingStrategy);
		for (final Class<?> clazz : entities) {
			configuration.addAnnotatedClass(clazz);
		}

		configuration.setProperty("hibernate.dialect",
				dialect.getDialectClass());
		final SchemaExport export = new SchemaExport(configuration);
		export.setDelimiter(";");
		export.setOutputFile(directory + "ddl_" + dialect.name().toLowerCase()
				+ ".sql");
		export.setFormat(true);
		export.execute(true, false, false, true);
	}

	/**
	 * Utility method used to fetch Class list based on a package name.
	 * 
	 * @param packageName
	 *            (should be the package containing your annotated beans.
	 */
	private List<Class<?>> getClasses(final String packageName)
			throws Exception {
		File directory = null;
		final List<Class<?>> classes = new LinkedList<Class<?>>();
		final ClassLoader classLoader = getClassLoader();
		final Enumeration<URL> resources = getResources(packageName,
				classLoader);

		while (resources.hasMoreElements()) {
			directory = new File(resources.nextElement().getFile());
			classes.addAll(collectClasses(packageName, directory));
		}
		return classes;
	}

	private ClassLoader getClassLoader() throws ClassNotFoundException {
		final ClassLoader cld = Thread.currentThread().getContextClassLoader();
		if (cld == null) {
			throw new ClassNotFoundException("Can't get class loader.");
		}
		return cld;
	}

	@Override
	public String getDescription() {
		return "Generate DDL Sql scripts based on hibernate schema"
				+ "\nSupported VM arguments:"
				+ "\nddl.domain.package - a package to scan for JPA entities"
				+ "\nddl.output.directory - output directory for Sql scripts";
	}

	private Enumeration<URL> getResources(final String packageName,
			final ClassLoader classLoader) throws ClassNotFoundException {
		final String path = packageName.replace('.', '/');
		Enumeration<URL> resources = null;
		try {
			resources = classLoader.getResources(path);
		} catch (final IOException e) {
			LOGGER.error("Error loading resources", e);
		}
		if (!resources.hasMoreElements()) {
			throw new ClassNotFoundException("No resource for " + path);
		}
		return resources;
	}

}
