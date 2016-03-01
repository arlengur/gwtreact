/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.tecomgroup.qos.criterion.Order;

/**
 * 
 * Шаблон для страниц с таблицами
 * 
 * @author abondin
 * 
 */
@MappedSuperclass
public abstract class MUserTemplateWithGrid extends MUserTemplateWithCriterion {
	private static final long serialVersionUID = 4794893796153731513L;

	public static final String GRID_COLUMN_SEPARATOR = ";";

	/**
	 * Список скрытых колонок разделённый {@link #GRID_COLUMN_SEPARATOR}
	 */
	@Column(length = 1024)
	private String hiddenColumnsString;

	@Embedded
	private Order order;

	public MUserTemplateWithGrid() {
		super();
	}

	public MUserTemplateWithGrid(final MUserTemplateWithGrid templateWithGrid) {
		super(templateWithGrid);
		setHiddenColumnsString(templateWithGrid.getHiddenColumnsString());
		setOrder(new Order(templateWithGrid.getOrder()));
	}

	public MUserTemplateWithGrid(final String name) {
		super(name);
	}

	@Transient
	public String[] getHiddenColumns() {
		return hiddenColumnsString == null || hiddenColumnsString.isEmpty()
				? new String[0]
				: hiddenColumnsString.split(GRID_COLUMN_SEPARATOR);
	}

	/**
	 * @return the hiddenColumns
	 */
	protected String getHiddenColumnsString() {
		return hiddenColumnsString;
	}

	/**
	 * @return the order
	 */
	public Order getOrder() {
		return order;
	}

	public void setHiddenColumns(final String[] columns) {
		if (columns == null || columns.length == 0) {
			hiddenColumnsString = null;
		} else {
			final StringBuilder builder = new StringBuilder();
			for (final String column : columns) {
				if (builder.length() != 0) {
					builder.append(GRID_COLUMN_SEPARATOR);
				}
				builder.append(column.trim());
			}
			hiddenColumnsString = builder.toString();
		}
	}

	/**
	 * @param hiddenColumnsString
	 *            the HiddenColumns to set
	 */
	protected void setHiddenColumnsString(final String hiddenColumnsString) {
		this.hiddenColumnsString = hiddenColumnsString;
	}

	/**
	 * @param order
	 *            the order to set
	 */
	public void setOrder(final Order order) {
		this.order = order;
	}

}
