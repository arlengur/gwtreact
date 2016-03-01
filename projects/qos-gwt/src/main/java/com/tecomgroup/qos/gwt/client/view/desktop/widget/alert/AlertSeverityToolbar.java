/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.alert;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.cell.core.client.form.CheckBoxCell;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer.CssFloatData;
import com.sencha.gxt.widget.core.client.event.DeactivateEvent;
import com.sencha.gxt.widget.core.client.event.DeactivateEvent.DeactivateHandler;
import com.sencha.gxt.widget.core.client.event.UpdateEvent;
import com.sencha.gxt.widget.core.client.event.UpdateEvent.UpdateHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.sencha.gxt.widget.core.client.grid.filters.ListMenu;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.gwt.client.style.common.AppearanceUtils;
import com.tecomgroup.qos.gwt.client.style.common.field.CheckBoxBorderedAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer.AlertSeverityPropertyEditor;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractRemoteDataGridView;

/**
 * @author ivlev.e
 * 
 */
public class AlertSeverityToolbar<M> {

	private final CssFloatLayoutContainer parentToolbar;

	private final AppearanceFactory appearanceFactory;

	private final AlertSeverityPropertyEditor severityPropertyEditor;

	private ListFilter<M, PerceivedSeverity> severityFilter;

	private final Map<PerceivedSeverity, CheckBox> severitiesCheckboxes;

	private final AbstractRemoteDataGridView<M, ?> gridView;

	public AlertSeverityToolbar(final CssFloatLayoutContainer parentToolbar,
			final AppearanceFactory appearanceFactory,
			final AlertSeverityPropertyEditor severityPropertyEditor) {
		this(parentToolbar, appearanceFactory, severityPropertyEditor, null);
	}

	public AlertSeverityToolbar(final CssFloatLayoutContainer parentToolbar,
			final AppearanceFactory appearanceFactory,
			final AlertSeverityPropertyEditor severityPropertyEditor,
			final AbstractRemoteDataGridView<M, ?> gridView) {
		this.parentToolbar = parentToolbar;
		this.appearanceFactory = appearanceFactory;
		this.severityPropertyEditor = severityPropertyEditor;
		this.severitiesCheckboxes = new HashMap<MAlertType.PerceivedSeverity, CheckBox>();
		this.gridView = gridView;
	}

	public void addSeverityFilterButtons() {
		final CheckBoxBorderedAppearance appearance = appearanceFactory
				.checkBoxBorderedAppearance();
		for (final PerceivedSeverity severity : PerceivedSeverity.values()) {
			final CheckBox checkBox = new CheckBox(new CheckBoxCell(appearance));
			checkBox.addStyleName(AppearanceUtils.getSeverityStyle(
					appearance.getStyle(), severity));
			checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				@Override
				public void onValueChange(final ValueChangeEvent<Boolean> event) {
					if (!hasSeverityFilter()) {
						return;
					}
					@SuppressWarnings("unchecked")
					final ListMenu<?, PerceivedSeverity> menu = ((ListMenu<?, PerceivedSeverity>) severityFilter
							.getMenu());
					final List<PerceivedSeverity> selected = menu.getSelected();
					final boolean wasEmpty = selected.isEmpty();
					if (event.getValue()) {
						if (!selected.contains(severity)) {
							selected.add(severity);
						}
					} else {
						selected.remove(severity);
					}
					menu.setSelected(selected);
					if (wasEmpty != selected.isEmpty()) {
						severityFilter.setActive(wasEmpty, true);
					}
					if (gridView != null) {
						gridView.refreshFilters(); 
						((DefaultAlertsGridWidgetView) gridView).setTemplateLabel(null);
					}
				}
			});
			checkBox.setSize("20px", "20px");
			checkBox.getElement().setMargins(new Margins(8, 5, 3, 5));

			final Label label = new Label(
					severityPropertyEditor.getLabel(severity));
			label.getElement().<XElement> cast()
					.setMargins(new Margins(8, 5, 3, 0));
			label.addStyleName(appearanceFactory.resources().css()
					.simpleLabel());

			final HorizontalPanel severityEntry = new HorizontalPanel();
			severityEntry.add(checkBox);
			severityEntry.add(label);
			parentToolbar.add(severityEntry, new CssFloatData());
			severitiesCheckboxes.put(severity, checkBox);
		}
	}

	/**
	 * Clears all severty checkboxes with synchronization of severityFilter.
	 * 
	 * Before use this method it is obligatory to set severity filter by using
	 * {@link AlertSeverityToolbar#setSeverityFilter(ListFilter)}.
	 */
	public void clearAllSeverityCheckboxes() {
		if (severityFilter != null) {
			severityFilter.setActive(false, false);
		} else {
			clearToolbarSeverityCheckboxes();
		}
	}

	private void clearToolbarSeverityCheckboxes() {
		for (final CheckBox checkBox : severitiesCheckboxes.values()) {
			checkBox.setValue(false, false, true);
		}
	}

	public Set<PerceivedSeverity> getCheckedValues() {
		final Set<PerceivedSeverity> values = new HashSet<PerceivedSeverity>();
		for (final Entry<PerceivedSeverity, CheckBox> entry : severitiesCheckboxes
				.entrySet()) {
			if (entry.getValue().getValue()) {
				values.add(entry.getKey());
			}
		}
		return values;
	}

	private boolean hasSeverityFilter() {
		return severityFilter != null;
	}

	/**
	 * Sets severity filter to synchronize with toolbar.
	 * 
	 * @param severityFilter
	 *            the severityFilter to set
	 */
	public void setSeverityFilter(
			final ListFilter<M, PerceivedSeverity> severityFilter) {
		this.severityFilter = severityFilter;

		severityFilter.addUpdateHandler(new UpdateHandler() {
			@Override
			public void onUpdate(final UpdateEvent event) {
				updateSeverityCheckboxes();
			}
		});

		severityFilter
				.addDeactivateHandler(new DeactivateHandler<Filter<M, ?>>() {

					@SuppressWarnings("unchecked")
					@Override
					public void onDeactivate(
							final DeactivateEvent<Filter<M, ?>> event) {
						clearToolbarSeverityCheckboxes();
						((ListMenu<MAlert, PerceivedSeverity>) severityFilter
								.getMenu()).setSelected(Collections
								.<PerceivedSeverity> emptyList());
					}
				});
	}

	/**
	 * Updates severty checkboxes to synchronize with severityFilter.
	 * 
	 * Before use this method it is obligatory to set severity filter by using
	 * {@link AlertSeverityToolbar#setSeverityFilter(ListFilter)}.
	 */
	@SuppressWarnings("unchecked")
	public void updateSeverityCheckboxes() {
		final List<PerceivedSeverity> selected = ((ListMenu<MAlert, PerceivedSeverity>) severityFilter
				.getMenu()).getSelected();
		for (final PerceivedSeverity severity : severitiesCheckboxes.keySet()) {
			final CheckBox checkBox = severitiesCheckboxes.get(severity);
			checkBox.setValue(
					severityFilter.isActive() && selected.contains(severity),
					false, true);
		}
	}
}
