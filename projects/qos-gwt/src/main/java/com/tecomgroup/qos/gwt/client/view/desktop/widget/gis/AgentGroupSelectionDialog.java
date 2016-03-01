/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.gis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.QoSDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.CustomGridView;

/**
 * @author kshnyakin.m
 * 
 */

public class AgentGroupSelectionDialog extends QoSDialog {

	public static class AgentAnchorWrapper implements Serializable {

		private static final long serialVersionUID = 6703233298298680276L;

		private final String agent;

		private final String displayName;

		private PerceivedSeverity status;

		private MAgent sourceAgent;

		private AgentAnchorWrapper(final String agent, final String displayName) {
			super();
			this.agent = agent;
			this.displayName = displayName;
		}

		public AgentAnchorWrapper(final String agent, final String displayName,
				final PerceivedSeverity status, final MAgent sourceAgent) {
			this(agent, displayName);
			this.status = status;
			this.sourceAgent = sourceAgent;
		}

		public String getAgent() {
			return agent;
		}

		public String getDisplayName() {
			return displayName;
		}

		public MAgent getSourceAgent() {
			return sourceAgent;
		}

		public PerceivedSeverity getStatus() {
			return status;
		}
	}

	interface GridProperties extends PropertyAccess<AgentAnchorWrapper> {

		@Path("displayName")
		ValueProvider<AgentAnchorWrapper, String> displayName();

		@Path("agent")
		ModelKeyProvider<AgentAnchorWrapper> key();
	}

	private Grid<AgentAnchorWrapper> grid;

	private final ListStore<AgentAnchorWrapper> store;

	private final GridProperties gridProps = GWT.create(GridProperties.class);

	private final AppearanceFactory appearanceFactory;

	private final AgentGisWidget agentGisWidget;

	public AgentGroupSelectionDialog(final AppearanceFactory factory,
			final QoSMessages messages, final AgentGisWidget agentGisWidget) {
		super(factory, messages);
		this.appearanceFactory = factory;
		this.agentGisWidget = agentGisWidget;
		store = new ListStore<AgentAnchorWrapper>(gridProps.key());
		setWidth(300);
		setHeight(200);
		setPredefinedButtons(PredefinedButton.CLOSE);
	}

	@Override
	protected String getTitleText(final QoSMessages messages) {
		return messages.navigationAgentsList();
	}

	@Override
	protected void initializeComponents() {
		store.addSortInfo(new StoreSortInfo<AgentAnchorWrapper>(gridProps
				.displayName(), SortDir.ASC));
		final List<ColumnConfig<AgentAnchorWrapper, ?>> columnList = new ArrayList<ColumnConfig<AgentAnchorWrapper, ?>>();
		final ColumnConfig<AgentAnchorWrapper, String> displayNameColumn = new ColumnConfig<AgentAnchorWrapper, String>(
				gridProps.displayName(), 100);

		columnList.add(displayNameColumn);
		final ColumnModel<AgentAnchorWrapper> agentColumnModel = new ColumnModel<AgentAnchorWrapper>(
				columnList);

		grid = new Grid<AgentAnchorWrapper>(store, agentColumnModel,
				new CustomGridView<AgentAnchorWrapper>(
						appearanceFactory.gridAlertsAppearance(),
						appearanceFactory.columnHeaderAppearance()));

		final AgentGroupGridViewConfig viewConfig = new AgentGroupGridViewConfig(
				appearanceFactory.gridAlertsAppearance().getResources(),
				appearanceFactory);

		grid.getView().setViewConfig(viewConfig);
		grid.setHideHeaders(true);
		grid.addStyleName(ClientConstants.QOS_GRID_STANDARD_STYLE);
		grid.addStyleName(ClientConstants.QOS_GRID_ALERTS_STYLE);
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.getSelectionModel()
				.addSelectionHandler(
						new SelectionHandler<AgentGroupSelectionDialog.AgentAnchorWrapper>() {

							@Override
							public void onSelection(
									final SelectionEvent<AgentAnchorWrapper> event) {
								final AgentAnchorWrapper agentWrapper = event
										.getSelectedItem();
								if (agentWrapper != null) {
									agentGisWidget
											.actionAgentSelected(agentWrapper
													.getSourceAgent());
								}
								AgentGroupSelectionDialog.this.hide();
							}
						});
		grid.getView().setAutoFill(true);
		this.add(grid);
	}

	public void setAgentGroup(final Map<MAgent, PerceivedSeverity> agentGroup) {
		store.clear();
		for (final Entry<MAgent, PerceivedSeverity> entrySet : agentGroup
				.entrySet()) {
			store.add(new AgentAnchorWrapper(entrySet.getKey().getName(),
					entrySet.getKey().getDisplayName(), entrySet.getValue(),
					entrySet.getKey()));
		}
	}

}
