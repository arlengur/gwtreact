/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiConstructor;
import com.sencha.gxt.cell.core.client.LabelProviderSafeHtmlRenderer;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.cell.core.client.form.TriggerFieldCell.TriggerFieldAppearance;
import com.sencha.gxt.core.client.util.TextMetrics;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;

/**
 * This Combo is extended for additional functionality:
 * 
 * Updates its value when selection change event occurs. This property is
 * disabled by default. It can be enabled by using
 * {@link CustomComboBox#setUpdateValueOnSelection(boolean)}.
 * 
 * <br/>
 * <br/>
 * 
 * Calculates max length of its values and adjust dropdown list width. This
 * property is enabled by default. It can be disabled by using
 * {@link CustomComboBox#setAutoUpdateMinListWidth(boolean)}.
 * 
 * <br/>
 * <br/>
 * 
 * Fixes GXT's {@link ComboBox} bug to support equal display names.
 * 
 * @author ivlev.e
 * 
 */
public class CustomComboBox<T> extends ComboBox<T> {

	private HandlerRegistration selectionEventRegistrationObject;

	private HandlerRegistration autoUpdateMinListWidthRegistrationObject;

	private TextMetrics textMetrics;

	private final int SCROLL_BAR_SPACE_WIDTH = 25;

	public CustomComboBox(final ComboBoxCell<T> cell) {
		super(cell);
	}

	@UiConstructor
	public CustomComboBox(final ListStore<T> store,
			final LabelProvider<? super T> labelProvider) {
		this(new ComboBoxCell<T>(store, labelProvider,
				new LabelProviderSafeHtmlRenderer<T>(labelProvider)));
		postInitialize();
	}

	public CustomComboBox(final ListStore<T> store,
			final LabelProvider<? super T> labelProvider,
			final ListView<T, ?> listView) {
		this(new ComboBoxCell<T>(store, labelProvider, listView));
		postInitialize();
	}

	public CustomComboBox(final ListStore<T> store,
			final LabelProvider<? super T> labelProvider,
			final ListView<T, ?> listView,
			final TriggerFieldAppearance appearance) {
		this(new ComboBoxCell<T>(store, labelProvider, listView, appearance));
		postInitialize();
	}

	public CustomComboBox(final ListStore<T> store,
			final LabelProvider<? super T> labelProvider,
			final SafeHtmlRenderer<T> renderer) {
		this(new ComboBoxCell<T>(store, labelProvider, renderer));
		postInitialize();
	}

	public CustomComboBox(final ListStore<T> store,
			final LabelProvider<? super T> labelProvider,
			final SafeHtmlRenderer<T> renderer,
			final TriggerFieldAppearance appearance) {
		this(new ComboBoxCell<T>(store, labelProvider, renderer));
		postInitialize();
	}

	public CustomComboBox(final ListStore<T> store,
			final LabelProvider<? super T> labelProvider,
			final TriggerFieldAppearance appearance) {
		this(
				new ComboBoxCell<T>(store, labelProvider,
						new LabelProviderSafeHtmlRenderer<T>(labelProvider),
						appearance));
		postInitialize();
	}

	private HandlerRegistration addAutoUpdateMinListWidthHandler() {
		return this.addExpandHandler(new ExpandHandler() {

			@Override
			public void onExpand(final ExpandEvent event) {
				final int maxValueLength = getMaxValueLength();
				final int offsetWidth = CustomComboBox.this.getOffsetWidth();

				if (maxValueLength > offsetWidth) {
					setMinListWidth(maxValueLength + SCROLL_BAR_SPACE_WIDTH);
				} else {
					setMinListWidth(offsetWidth);
				}
			}
		});
	}

	private HandlerRegistration addUpdateValueOnSelectionHandler() {
		return addSelectionHandler(new SelectionHandler<T>() {

			@Override
			public void onSelection(final SelectionEvent<T> event) {
				// CustomComboBox.this.finishEditing();
				CustomComboBox.this.setValue(event.getSelectedItem(), true);
			}
		});
	}

	private Integer getMaxValueLength() {
		String maxWidthLabel = "";
		final LabelProvider<? super T> labelProvider = getLabelProvider();
		for (final T item : getStore().getAll()) {

			final String currentLabel = labelProvider.getLabel(item);

			if (currentLabel.length() > maxWidthLabel.length()) {
				maxWidthLabel = currentLabel;
			}
		}
		return textMetrics.getWidth(maxWidthLabel);
	}

	@Override
	protected void onAfterFirstAttach() {
		super.onAfterFirstAttach();
		textMetrics = TextMetrics.get();
		textMetrics.bind(this.getElement());
	}

	private void postInitialize() {
		this.addExpandHandler(new ExpandHandler() {
			@Override
			public void onExpand(final ExpandEvent event) {
				final T selected = CustomComboBox.this.getValue();
				if (selected == null) {
					return;
				}
				final int index = CustomComboBox.this.getStore().indexOf(
						selected);
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						if (index >= 0) {
							CustomComboBox.this.getListView().getElement(index)
									.scrollIntoView();
						}
					}
				});
			}
		});
		autoUpdateMinListWidthRegistrationObject = addAutoUpdateMinListWidthHandler();
	}

	@Override
	public boolean redraw(final boolean force) {
		return super.redraw(force);
	}

	/**
	 * Whether combobox should update its list view width in accordance with max
	 * item's length
	 * 
	 * @param autoUpdateMinListWidth
	 */
	public void setAutoUpdateMinListWidth(final boolean autoUpdateMinListWidth) {
		if (autoUpdateMinListWidth) {
			autoUpdateMinListWidthRegistrationObject = addAutoUpdateMinListWidthHandler();
		} else {
			autoUpdateMinListWidthRegistrationObject.removeHandler();
		}
	}

	/**
	 * Whether field should update it's value when selection change event occurs
	 * (default to true)
	 * 
	 * @param updateWhenSelection
	 */
	public void setUpdateValueOnSelection(final boolean updateWhenSelection) {
		if (updateWhenSelection) {
			selectionEventRegistrationObject = addUpdateValueOnSelectionHandler();
		} else if (selectionEventRegistrationObject != null) {
			selectionEventRegistrationObject.removeHandler();
		}
	}

}
