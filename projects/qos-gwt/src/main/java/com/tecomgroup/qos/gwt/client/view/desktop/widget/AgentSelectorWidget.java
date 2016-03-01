/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.AgentProperties;

/**
 * 
 * Компонент для выбора Блока Контроля
 * 
 * @author abondin
 * 
 */

public class AgentSelectorWidget
		implements
			IsWidget,
			ClientConstants,
			AgentSelectionListener {

	private ContentPanel panel;

	private MAgent selectedAgent = null;

	private ListStore<MAgent> agentStore;

	private CustomComboBox<MAgent> findAgent;

	private ComboBox<MAgent> selectAgent;

	protected final AgentProperties agentProperties = GWT
			.create(AgentProperties.class);

	private final Set<AgentSelectionListener> listeners = new HashSet<AgentSelectionListener>();

	@Inject
	public AgentSelectorWidget(final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider) {
		initializeUI(messages, appearanceFactoryProvider.get());
		initializeListeners();
	}

	public void addListener(final AgentSelectionListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void agentSelected(final MAgent agent) {
		selectAgent.select(agent);
		selectAgent.setValue(agent);
		findAgent.select(agent);
		findAgent.setValue(agent);
		selectedAgent = agent;
	}

	@Override
	public ContentPanel asWidget() {
		return panel;
	}
	/**
	 * @return the selectedAgent
	 */
	public MAgent getSelectedAgent() {
		return selectedAgent;
	}

	/**
	 * 
	 */
	private void initializeListeners() {
		selectAgent.addValueChangeHandler(new ValueChangeHandler<MAgent>() {
			@Override
			public void onValueChange(final ValueChangeEvent<MAgent> event) {
				findAgent.setValue(event.getValue());
				notifyListeners(event.getValue());
			}
		});
		selectAgent.addSelectionHandler(new SelectionHandler<MAgent>() {
			@Override
			public void onSelection(final SelectionEvent<MAgent> event) {
				findAgent.setValue(event.getSelectedItem());
				notifyListeners(event.getSelectedItem());
			}
		});
		findAgent.addSelectionHandler(new SelectionHandler<MAgent>() {
			@Override
			public void onSelection(final SelectionEvent<MAgent> event) {
				selectAgent.setValue(event.getSelectedItem());
				notifyListeners(event.getSelectedItem());
			}
		});
		findAgent.addValueChangeHandler(new ValueChangeHandler<MAgent>() {
			@Override
			public void onValueChange(final ValueChangeEvent<MAgent> event) {
				selectAgent.setValue(event.getValue());
				notifyListeners(event.getValue());
			}
		});
	}
	private void initializeUI(final QoSMessages messages,
			final AppearanceFactory appearanceFactory) {
		agentStore = new ListStore<MAgent>(new ModelKeyProvider<MAgent>() {
			@Override
			public String getKey(final MAgent item) {
				return item.getName();
			};
		});
		agentStore.addSortInfo(new StoreSortInfo<MAgent>(agentProperties
				.displayNameValue(), SortDir.ASC));
		findAgent = new CustomComboBox<MAgent>(agentStore,
				agentProperties.displayName(),
				appearanceFactory.triggerFieldAppearance());
		findAgent.setUpdateValueOnSelection(false);
		findAgent.setWidth(DEFAULT_FIELD_WIDTH);
		// TODO Uncomment me when Divisions will be done
		// findAgent.setHideTrigger(true);
		findAgent.setEmptyText(messages.emptyAgentText());
		findAgent.setTriggerAction(TriggerAction.ALL);
		findAgent.setAllowBlank(true);
		findAgent.setTypeAhead(true);

		selectAgent = new ComboBox<MAgent>(agentStore,
				agentProperties.displayName());
		selectAgent.setEmptyText(messages.emptyAgentText());
		selectAgent.setWidth(DEFAULT_FIELD_WIDTH);

		final HBoxLayoutContainer container = new HBoxLayoutContainer();
		container.setBorders(false);
		container.setHBoxLayoutAlign(HBoxLayoutAlign.STRETCHMAX);

		final BoxLayoutData layoutData = new BoxLayoutData(new Margins(5, 5, 5,
				5));
		container.add(findAgent, layoutData);

		// TODO Uncomment me when Divisions will be done
		// final BoxLayoutData flex = new BoxLayoutData();
		// flex.setFlex(1);
		// container.add(new Label(), flex);
		// container.add(selectAgent, layoutData);

		panel = new ContentPanel();
		panel.setHeaderVisible(false);

		final SimplePanel simplePanel = new SimplePanel();
		simplePanel.addStyleName(appearanceFactory.resources().css()
				.addChartSeriesSelectAgentPanel());
		simplePanel.add(container);

		panel.add(simplePanel);
		panel.setBorders(false);
		panel.setBodyBorder(false);
	}

	public void loadAllAgents(final List<MAgent> agents) {
		selectAgent.reset();
		findAgent.reset();
		agentStore.clear();
		agentStore.addAll(agents);
	}

	protected void notifyListeners(final MAgent agent) {
		if (selectedAgent != agent) {
			selectedAgent = agent;
			for (final AgentSelectionListener listener : listeners) {
				listener.agentSelected(agent);
			}
		}
	}

	public void removeListener(final AgentSelectionListener listener) {
		this.listeners.remove(listener);
	}
}
