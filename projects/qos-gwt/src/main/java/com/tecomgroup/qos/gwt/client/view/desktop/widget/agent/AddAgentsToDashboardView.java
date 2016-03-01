/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.agent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.agent.AddAgentsToDashboardWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AddNamedWidgetToDashboardView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.CustomCheckBoxSelectionModel;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.CustomGridView;

/**
 * @author ivlev.e
 *
 */
public abstract class AddAgentsToDashboardView
		extends
			AddNamedWidgetToDashboardView {

	private Grid<MAgent> grid;

	@Inject
	public AddAgentsToDashboardView(final EventBus eventBus,
			final AppearanceFactoryProvider appearanceFactoryPrvider,
			final QoSMessages messages) {
		super(eventBus, appearanceFactoryPrvider, messages);
	}

	/**
	 * Validates selected agentKeys. Show error message if there are no selected
	 * agent keys.
	 *
	 * @return
	 */
	protected boolean areAgentKeysValid() {
		final Set<String> selectedAgentKeys = getSelectedAgentKeys();
		final boolean result = (selectedAgentKeys == null)
				|| (!selectedAgentKeys.isEmpty());
		if (!result) {
			AppUtils.showErrorMessage(messages.agentNotSelected());
		}
		return result;
	}

	@Override
	protected boolean areUserFieldsValid() {
		return super.areUserFieldsValid() && areAgentKeysValid();
	}

	@Override
	protected void clearDialogFields() {
		super.clearDialogFields();
		grid.getSelectionModel().deselectAll();
	}

	/**
	 * If all agents are selected then return null to facilitate database query.
	 *
	 * @return
	 */
	protected Set<String> getSelectedAgentKeys() {
		Set<String> selectedKeys = null;

		final List<MAgent> selectedAgents = grid.getSelectionModel()
				.getSelectedItems();
		if (selectedAgents.size() != grid.getStore().getAll().size()) {
			selectedKeys = new HashSet<String>();
			for (final MAgent agent : selectedAgents) {
				selectedKeys.add(agent.getKey());
			}
		}

		return selectedKeys;
	}

	@Override
	public void initialize() {
		super.initialize();
		initializeGrid();
	}

	private void initializeGrid() {
		final IdentityValueProvider<MAgent> identity = new IdentityValueProvider<MAgent>();
		final CheckBoxSelectionModel<MAgent> selectionModel = new CustomCheckBoxSelectionModel<MAgent>(
				identity, appearanceFactory.<MAgent> checkBoxColumnAppearance());
		selectionModel.setSelectionMode(SelectionMode.SIMPLE);

		final List<ColumnConfig<MAgent, ?>> columns = new ArrayList<ColumnConfig<MAgent, ?>>();
		final ColumnConfig<MAgent, String> agentColumn = new ColumnConfig<MAgent, String>(
				getUiHandlers().<AddAgentsToDashboardWidgetPresenter> cast()
						.getAgentProperties().displayNameValue(), 100,
				messages.probe());
		columns.add(selectionModel.getColumn());
		columns.add(agentColumn);
		final ColumnModel<MAgent> columnModel = new ColumnModel<MAgent>(columns);

		grid = new Grid<MAgent>(getUiHandlers()
				.<AddAgentsToDashboardWidgetPresenter> cast().getStore(),
				columnModel, new CustomGridView<MAgent>(
						appearanceFactory.gridStandardAppearance(),
						appearanceFactory.columnHeaderAppearance()));
		grid.addStyleName(ClientConstants.QOS_GRID_STANDARD_STYLE);
		grid.setSelectionModel(selectionModel);
		grid.getView().setEmptyText(messages.noAgentsToDisplay());
		grid.getView().setStripeRows(false);
		grid.getView().setAutoFill(true);
		grid.getView().setColumnLines(true);
		grid.getStore().setAutoCommit(true);

		mainContainer.add(grid, new VerticalLayoutData(1, 1, new Margins(0, 5,
				5, 5)));
	}

}
