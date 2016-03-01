/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.inject.Inject;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.AgentProperties;
import com.tecomgroup.qos.service.TaskRetrieverAsync;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author ivlev.e
 * 
 */
public class ParameterSelectorWidgetWithAgentSelection
		extends
			ParameterSelectorWidget {

	public static Logger LOGGER = Logger
			.getLogger(ParameterSelectorWidgetWithAgentSelection.class
					.getName());

	protected final AgentProperties agentProps = GWT
			.create(AgentProperties.class);

	protected final CustomComboBox<MAgent> agentControl;

	private MAgent selectedAgent = null;

	/**
	 * 
	 */
	@Inject
	public ParameterSelectorWidgetWithAgentSelection(
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final TaskRetrieverAsync taskRetriever, final QoSMessages messages) {
		super(appearanceFactoryProvider, taskRetriever, messages);
		final ListStore<MAgent> store = new ListStore<MAgent>(agentProps.key());
		agentControl = new CustomComboBox<MAgent>(store,
				agentProps.displayName());
		agentControl.setEmptyText(messages.emptyAgentText());
		agentControl.setTypeAhead(true);
		agentControl.setEditable(true);
		agentControl.setTriggerAction(TriggerAction.ALL);
		agentControl.setForceSelection(true);
		agentControl.setUpdateValueOnSelection(false);
		initAgentListeners();
	}

	@Override
	public void disableControls() {
		// agentControl.setEnabled(false);
		super.disableControls();
	}

	@Override
	public void enableControls() {
		agentControl.setEnabled(true);
		super.enableControls();
	}

	/**
	 * @return the agentControl
	 */
	public ComboBox<MAgent> getAgentControl() {
		return agentControl;
	}

	protected void initAgentListeners() {
		agentControl.addSelectionHandler(new SelectionHandler<MAgent>() {

			@Override
			public void onSelection(final SelectionEvent<MAgent> event) {
				if (selectedAgent != event.getSelectedItem()) {
					selectAgent(event.getSelectedItem());
					selectedAgent = event.getSelectedItem();
				}
			}

		});

		agentControl.addValueChangeHandler(new ValueChangeHandler<MAgent>() {

			@Override
			public void onValueChange(final ValueChangeEvent<MAgent> event) {
				if (selectedAgent != event.getValue()) {
					selectAgent(event.getValue());
					selectedAgent = event.getValue();
				}
			}

		});

		agentControl.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(final KeyUpEvent event) {
				if (!SimpleUtils.isNotNullAndNotEmpty(agentControl.getText())) {
					if (selectedAgent != null) {
						selectAgent(null);
						selectedAgent = null;
					}
				}
			}
		});
		super.initListeners();
	}

	@Override
	public void reset() {
		agentControl.reset();
		agentControl.getStore().clear();
		super.reset();
	}

	public void setAgents(final List<MAgent> agents) {
		agentControl.getStore().clear();
		agentControl.getStore().addAll(agents);
	}
}
