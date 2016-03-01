/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.modelspace;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.hibernate.cfg.DefaultComponentSafeNamingStrategy;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
public class CustomNamingStrategy extends DefaultComponentSafeNamingStrategy {

	Logger LOGGER = Logger.getLogger(CustomNamingStrategy.class);

	private int maxColumnNameSize;

	private static int MIN_COLUMN_NAME_SIZE = 3;

	private String abbreviateName(final String someName) {
		if (someName.length() <= maxColumnNameSize) {
			return someName;
		}

		final String[] tokens = splitName(someName);
		shortenName(someName, tokens);

		return assembleResults(tokens);
	}

	private String assembleResults(final String[] tokens) {
		final StringBuilder result = new StringBuilder(tokens[0]);
		for (int j = 1; j < tokens.length; j++) {
			result.append("_").append(tokens[j]);
		}
		return result.toString();
	}

	@Override
	public String classToTableName(final String aClassName) {

		return abbreviateName(super.classToTableName(aClassName));
	}

	@Override
	public String collectionTableName(final String ownerEntity,
			final String ownerEntityTable, final String associatedEntity,
			final String associatedEntityTable, final String propertyName) {
		return abbreviateName(super.collectionTableName(ownerEntity,
				ownerEntityTable, associatedEntity, associatedEntityTable,
				propertyName));
	}

	private String cutName(String name) {
		if (name.length() <= maxColumnNameSize
				&& name.length() > 2 * MIN_COLUMN_NAME_SIZE) {
			name = name.substring(0, name.length() - MIN_COLUMN_NAME_SIZE);
		} else if (name.length() > maxColumnNameSize) {
			name = name.substring(0, maxColumnNameSize);
		}
		return name;
	}

	@Override
	public String foreignKeyColumnName(final String propertyName,
			final String propertyEntityName, final String propertyTableName,
			final String referencedColumnName) {
		return abbreviateName(super.foreignKeyColumnName(propertyName,
				propertyEntityName, propertyTableName, referencedColumnName));
	}

	private int getIndexOfLongest(final String[] tokens) {
		int maxLength = 0;
		int index = -1;
		for (int i = 0; i < tokens.length; i++) {
			final String string = tokens[i];
			if (maxLength < string.length()) {
				maxLength = string.length();
				index = i;
			}
		}
		return index;
	}

	@Override
	public String logicalCollectionColumnName(final String columnName,
			final String propertyName, final String referencedColumn) {
		return abbreviateName(super.logicalCollectionColumnName(columnName,
				propertyName, referencedColumn));
	}

	@Override
	public String logicalCollectionTableName(final String tableName,
			final String ownerEntityTable, final String associatedEntityTable,
			final String propertyName) {
		return abbreviateName(super.logicalCollectionTableName(tableName,
				ownerEntityTable, associatedEntityTable, propertyName));
	}

	@Override
	public String logicalColumnName(final String columnName,
			final String propertyName) {
		return abbreviateName(super.logicalColumnName(columnName, propertyName));
	}

	@Override
	public String propertyToColumnName(final String propertyName) {
		return abbreviateName(super.propertyToColumnName(propertyName));
	}

	/**
	 * @param maxColumnNameSize
	 *            the maxColumnNameSize to set
	 */
	public void setMaxColumnNameSize(final int maxColumnNameSize) {
		this.maxColumnNameSize = maxColumnNameSize;
	}

	private void shortenName(final String someName, final String[] tokens) {
		int currentLength = someName.length();
		while (currentLength > maxColumnNameSize) {
			final int tokenIndex = getIndexOfLongest(tokens);
			final String oldToken = tokens[tokenIndex];
			tokens[tokenIndex] = cutName(oldToken);
			currentLength -= oldToken.length() - tokens[tokenIndex].length();
		}
	}

	private String[] splitName(final String someName) {
		final StringTokenizer toki = new StringTokenizer(someName, "_");
		final String[] tokens = new String[toki.countTokens()];
		int i = 0;
		while (toki.hasMoreTokens()) {
			tokens[i] = toki.nextToken();
			i++;
		}
		return tokens;
	}
}
