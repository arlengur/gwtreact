/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.filter;

import java.util.Date;

import com.google.inject.Inject;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.FilterHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.grid.filters.BooleanFilter;
import com.sencha.gxt.widget.core.client.grid.filters.BooleanFilter.BooleanFilterMessages;
import com.sencha.gxt.widget.core.client.grid.filters.DateFilter;
import com.sencha.gxt.widget.core.client.grid.filters.DateFilter.DateFilterMessages;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.sencha.gxt.widget.core.client.grid.filters.ListMenu;
import com.sencha.gxt.widget.core.client.grid.filters.NumericFilter;
import com.sencha.gxt.widget.core.client.grid.filters.NumericFilter.NumericFilterMessages;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter.StringFilterMessages;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.messages.LocalizedBooleanFilterMessages;
import com.tecomgroup.qos.gwt.client.messages.LocalizedDateFilterMessages;
import com.tecomgroup.qos.gwt.client.messages.LocalizedNumericFilterMessages;
import com.tecomgroup.qos.gwt.client.messages.LocalizedStringFilterMessages;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer.EnumerationPropertyEditor;

/**
 * Implementation of {@link LocalizedFilterFactory} with localized filters.
 * 
 * @author meleshin.o
 */
public class DefaultLocalizedFilterFactory extends DefaultFilterFactory
		implements
			LocalizedFilterFactory {

	private static class EnumFilterHandler<V extends Enum<?>>
			extends
				FilterHandler<V> {

		private final EnumerationPropertyEditor<V> propertyEditor;
		private final Class<?> enumType;

		/**
		 * @param propertyEditor
		 */
		public EnumFilterHandler(
				final EnumerationPropertyEditor<V> propertyEditor) {
			super();
			this.propertyEditor = propertyEditor;
			this.enumType = getEnumType(propertyEditor);
		}

		@Override
		public V convertToObject(final String value) {
			return (V) Enum.valueOf((Class<Enum>) enumType, value);
		}

		@Override
		public String convertToString(final V key) {
			String result = "";
			if (key != null) {
				result = propertyEditor.getLabel(key);
			}

			return result;
		}

		private Class<?> getEnumType(
				final EnumerationPropertyEditor<V> propertyEditor) {
			final ListStore<V> store = propertyEditor.getStore();

			return store.get(0).getClass();
		}
	}

	private static class ShowListFilterMenuHandler<V extends Enum<?>>
			implements
				ShowHandler {
		@Override
		public void onShow(final ShowEvent event) {
			final ListMenu<?, V> listMenu = (ListMenu<?, V>) event.getSource();
			final int menuItemsCount = listMenu.getWidgetCount();
			final FilterHandler<V> handler = listMenu.getFilter().getHandler();

			for (int i = 0; i < menuItemsCount; i++) {
				final MenuItem menuItem = (MenuItem) listMenu.getWidget(i);

				final V value = handler.convertToObject(menuItem.getText());
				menuItem.setText(handler.convertToString(value));
			}
		}
	}

	private final BooleanFilterMessages booleanFilterMessages;
	private final DateFilterMessages dateFilterMessages;
	private final NumericFilterMessages numericFilterMessages;
	private final StringFilterMessages stringFilterMessages;

	@Inject
	public DefaultLocalizedFilterFactory(final QoSMessages messages) {
		booleanFilterMessages = new LocalizedBooleanFilterMessages(messages);
		dateFilterMessages = new LocalizedDateFilterMessages(messages);
		numericFilterMessages = new LocalizedNumericFilterMessages(messages);
		stringFilterMessages = new LocalizedStringFilterMessages(messages);
	}

	@Override
	public <M> BooleanFilter<M> createBooleanFilter(
			final ValueProvider<M, Boolean> valueProvider) {
		final BooleanFilter<M> filter = super
				.createBooleanFilter(valueProvider);
		filter.setMessages(booleanFilterMessages);

		return filter;
	}

	@Override
	public <M> DateFilter<M> createDateFilter(
			final ValueProvider<M, Date> valueProvider) {
		final DateFilter<M> filter = super.createDateFilter(valueProvider);
		filter.setMessages(dateFilterMessages);
		return filter;
	}

	@Override
	public <M, V extends Enum<?>> ListFilter<M, V> createEnumListFilter(
			final ValueProvider<M, V> valueProvider,
			final EnumerationPropertyEditor<V> propertyEditor) {

		final ListFilter<M, V> listFilter = createListFilter(valueProvider,
				propertyEditor.getStore());
		listFilter.getMenu().addShowHandler(new ShowListFilterMenuHandler<V>());
		listFilter.setHandler(new EnumFilterHandler<V>(propertyEditor));

		return listFilter;
	}

	@Override
	public <M, V extends Number> NumericFilter<M, V> createNumericFilter(
			final ValueProvider<M, V> valueProvider,
			final NumberPropertyEditor<V> propertyEditor) {
		final NumericFilter<M, V> filter = super.createNumericFilter(
				valueProvider, propertyEditor);

		filter.setMessages(numericFilterMessages);
		return filter;
	}

	@Override
	public <M> StringFilter<M> createStringFilter(
			final ValueProvider<M, String> valueProvider) {
		final StringFilter<M> filter = super.createStringFilter(valueProvider);
		filter.setMessages(stringFilterMessages);
		return filter;
	}

}
