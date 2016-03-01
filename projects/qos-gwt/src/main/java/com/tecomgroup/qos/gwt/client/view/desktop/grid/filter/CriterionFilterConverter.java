/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterConfigBean;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.filters.BooleanFilter;
import com.sencha.gxt.widget.core.client.grid.filters.BooleanFilter.BooleanFilterMessages;
import com.sencha.gxt.widget.core.client.grid.filters.DateFilter;
import com.sencha.gxt.widget.core.client.grid.filters.DateFilter.DateFilterMessages;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.sencha.gxt.widget.core.client.grid.filters.ListMenu;
import com.sencha.gxt.widget.core.client.grid.filters.NumericFilter;
import com.sencha.gxt.widget.core.client.grid.filters.RangeMenu;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.DateMenu;
import com.tecomgroup.qos.criterion.BinaryCriterion;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.Criterion.BinaryOperation;
import com.tecomgroup.qos.criterion.Criterion.TernaryOperation;
import com.tecomgroup.qos.criterion.CriterionQueryFactory;
import com.tecomgroup.qos.criterion.CriterionWithParameter;
import com.tecomgroup.qos.criterion.TernaryCriterion.OnDayCriterion;

/**
 * @author ivlev.e
 * 
 */
public class CriterionFilterConverter {

	@SuppressWarnings("unchecked")
	public static <M> void applyBinaryCriterionToFilter(
			final FilterPagingLoadConfig pagingConfig,
			final GridFilters<M> filters, final CriterionWithParameter criterion) {
		final Filter<M, ?> filter = filters.getFilter(criterion.getParameter());
		final Object criterionValue;
		final Object operation;
		if (criterion instanceof BinaryCriterion) {
			criterionValue = ((BinaryCriterion<M>) criterion).getValue();
			operation = ((BinaryCriterion<M>) criterion).getOperation();
		} else if (criterion instanceof OnDayCriterion) {
			criterionValue = ((OnDayCriterion) criterion).getDay();
			operation = ((OnDayCriterion) criterion).getOperation();
		} else {
			throw new UnsupportedOperationException(
					"Unsupported criterion type for filter " + criterion);
		}
		if (filter != null) {
			if (filter instanceof StringFilter<?>) {
				final String value = criterionValue.toString().replace("%", "");
				updateStringFilter(filter, value);
			} else if (filter instanceof BooleanFilter<?>) {
				final Boolean value = (Boolean) criterionValue;
				updateBooleanFilter(filter, value);
			} else if (filter instanceof ListFilter<?, ?>) {
				final Object value = criterionValue;
				updateListFilter(filter, value);
			} else if (filter instanceof NumericFilter<?, ?>) {
				updateNumberFilter(filter, (Number) criterionValue, operation);
			} else if (filter instanceof DateFilter<?>) {
				updateDateFilter(filter, operation, (Date) criterionValue);
			}
			if (!filter.isActive()) {
				filter.setActive(true, true);
			}
		}
	}

	private static Criterion convertToBoolean(final String field,
			final Boolean value) {
		return CriterionQueryFactory.getQuery().eq(field, value);
	}

	private static Criterion convertToContextAndInSensitive(final String field,
			final String substring) {
		return CriterionQueryFactory.getQuery().istringContains(field,
				substring.trim());
	}

	public static Criterion convertToCriterion(final FilterConfig config,
			final EnumMapper mapper, final GridFilters<?> filters) {
		Criterion criterion = null;
		if (config instanceof FilterConfigBean) {
			final FilterConfigBean bean = (FilterConfigBean) config;
			final String comparison = bean.getComparison();
			final String field = bean.getField();
			final String value = bean.getValue();
			final String type = bean.getType();
			if ("list".equals(type)) {
				criterion = convertToFromEnum(field, value.split("::"), mapper);
			} else if ("string".equals(type)) {
				criterion = convertToContextAndInSensitive(field, value);
			} else if ("boolean".equals(type)) {
				criterion = convertToBoolean(field, Boolean.parseBoolean(value));
			} else if ("numeric".equals(type)) {
				final Filter<?, ?> filter = filters.getFilter(field);
				if (filter != null) {
					criterion = convertToNumeric(field, filter.getHandler()
							.convertToObject(value), comparison);
				}
			} else if ("date".equals(type)) {
				criterion = convertToDate(field, new Date(Long.valueOf(value)),
						comparison);
			}
		}
		return criterion;
	}

	private static Criterion convertToDate(final String field,
			final Date value, final String comparison) {
		Criterion criterion = null;
		if ("before".equals(comparison)) {
			criterion = CriterionQueryFactory.getQuery().le(field, value);
		} else if ("after".equals(comparison)) {
			criterion = CriterionQueryFactory.getQuery().ge(field, value);
		} else if ("on".equals(comparison)) {
			criterion = new OnDayCriterion(field, value);
		}
		return criterion;
	}

	private static Criterion convertToFromEnum(final String field,
			final String[] values, final EnumMapper mapper) {
		Criterion leftCriterion = null;
		for (int i = 0; i < values.length; i++) {
			Criterion rigthCriterion = CriterionQueryFactory.getQuery().eq(
					field, mapper.tryConvertToEnum(field, values[i]));
			if (leftCriterion != null) {
				rigthCriterion = CriterionQueryFactory.getQuery().or(
						leftCriterion, rigthCriterion);
			}
			leftCriterion = rigthCriterion;
		}
		return leftCriterion;
	}

	private static Criterion convertToNumeric(final String field,
			final Object value, final String comparison) {
		Criterion criterion = null;
		if ("lt".equals(comparison)) {
			criterion = CriterionQueryFactory.getQuery().le(field, value);
		} else if ("gt".equals(comparison)) {
			criterion = CriterionQueryFactory.getQuery().ge(field, value);
		} else if ("eq".equals(comparison)) {
			criterion = CriterionQueryFactory.getQuery().eq(field, value);
		}
		return criterion;
	}

	@SuppressWarnings("unchecked")
	private static <M> void updateBooleanFilter(final Filter<M, ?> filter,
			final Boolean value) {
		final int widgetsCount = filter.getMenu().getWidgetCount();
		final BooleanFilterMessages messages = ((BooleanFilter<M>) filter)
				.getMessages();
		for (int i = 0; i < widgetsCount; i++) {
			final Widget widget = filter.getMenu().getWidget(i);
			if (widget instanceof CheckMenuItem) {
				if (((CheckMenuItem) widget).getText().equals(
						messages.yesText())) {
					((CheckMenuItem) widget).setChecked(value, true);
				} else {
					((CheckMenuItem) widget).setChecked(!value, true);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static <M> void updateDateFilter(final Filter<M, ?> filter,
			final Object operation, final Date value) {
		final int widgetsCount = filter.getMenu().getWidgetCount();
		final DateFilterMessages messages = ((DateFilter<M>) filter)
				.getMessages();
		for (int i = 0; i < widgetsCount; i++) {
			final Widget widget = filter.getMenu().getWidget(i);
			if (widget instanceof CheckMenuItem) {
				final CheckMenuItem item = (CheckMenuItem) widget;
				DateMenu menu = null;
				if (item.getText().equals(messages.afterText())
						&& operation == BinaryOperation.GE) {
					menu = (DateMenu) item.getSubMenu();
				} else if (item.getText().equals(messages.beforeText())
						&& operation == BinaryOperation.LE) {
					menu = (DateMenu) item.getSubMenu();
				} else if (item.getText().equals(messages.onText())
						&& operation == TernaryOperation.BETWEEN) {
					menu = (DateMenu) item.getSubMenu();
				}
				if (menu != null) {
					menu.setDate(value);
				}
			}
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static <M> void updateListFilter(final Filter<M, ?> filter,
			final Object value) {
		final ListMenu<M, ?> menu = (ListMenu<M, ?>) filter.getMenu();
		final List<Object> selected = new ArrayList<Object>(menu.getSelected());
		selected.add(value);
		menu.setSelected((List) selected);
	}

	private static <M> void updateNumberFilter(final Filter<M, ?> filter,
			final Number value, final Object operation) {
		@SuppressWarnings("unchecked")
		final RangeMenu<M, Number> menu = (RangeMenu<M, Number>) filter
				.getMenu();
		final FilterConfig config = new FilterConfigBean();
		config.setValue(value.toString());
		if (operation == BinaryOperation.EQ) {
			config.setComparison("eq");
		} else if (operation == BinaryOperation.LE) {
			config.setComparison("lt");
		} else if (operation == BinaryOperation.GE) {
			config.setComparison("gt");
		} else {
			throw new UnsupportedOperationException(
					"Unsupported numeric operation " + operation);
		}
		menu.setValue(Arrays.asList(config));
	}

	private static <M> void updateStringFilter(final Filter<M, ?> filter,
			final String value) {
		final TextField textMenuField = (TextField) filter.getMenu().getWidget(
				0);
		textMenuField.setValue(value, true);
		filter.setActive(true, true);
	}
}
