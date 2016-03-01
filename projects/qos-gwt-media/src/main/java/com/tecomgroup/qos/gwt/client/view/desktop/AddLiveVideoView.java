/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MLiveStreamWrapper;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.AddLiveVideoPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.StreamBaseValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractAgentDialogView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AgentSelectorWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.CustomCheckBoxSelectionModel;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.LiveStreamBasicCell;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.gis.AgentGisWidget;
import com.tecomgroup.qos.gwt.client.wrapper.StreamClientWrapper;

/**
 * @author ivlev.e
 * 
 */
public class AddLiveVideoView extends AbstractAgentDialogView
		implements
			AddLiveVideoPresenter.MyView {

	protected class UIFieldHolder {

		@UiField(provided = true)
		public Grid<StreamClientWrapper<MLiveStreamWrapper>> grid;

		@UiField(provided = true)
		public TextButton okButton;

	}

	interface ViewUiBinder extends UiBinder<VBoxLayoutContainer, UIFieldHolder> {

	}

	final ModelKeyProvider<StreamClientWrapper<MLiveStreamWrapper>> keyProvider = new ModelKeyProvider<StreamClientWrapper<MLiveStreamWrapper>>() {

		@Override
		public String getKey(final StreamClientWrapper<MLiveStreamWrapper> item) {
			return item.getWrapper().getStream().getUrl();
		}
	};

	private final static ViewUiBinder UI_BINDER = GWT
			.create(ViewUiBinder.class);

	protected QoSMessages messages;

	private final VBoxLayoutContainer leftContainer;

	private final UIFieldHolder uiFieldHolder = new UIFieldHolder();

	@Inject
	public AddLiveVideoView(final EventBus eventBus,
			final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final AgentSelectorWidget agentSelectorWidget,
			final AgentGisWidget agentGisWidget) {
		super(eventBus, appearanceFactoryProvider, agentSelectorWidget,
				agentGisWidget);
		this.messages = messages;
		initializeGrid(appearanceFactoryProvider.get(), messages);

		uiFieldHolder.okButton = new TextButton(new TextButtonCell(
				appearanceFactoryProvider.get()
						.<String> buttonCellHugeAppearance()));
		uiFieldHolder.okButton.setText(messages.actionDone());

		leftContainer = UI_BINDER.createAndBindUi(uiFieldHolder);
		setLeftContent(leftContainer);
		initializeListeners();
	}

	@Override
	public Dialog asWidget() {
		return getDialog();
	}

	private ColumnModel<StreamClientWrapper<MLiveStreamWrapper>> createColumnModel(
			final AppearanceFactory af,
			final QoSMessages messages,
			final CheckBoxSelectionModel<StreamClientWrapper<MLiveStreamWrapper>> sm) {
		final List<ColumnConfig<StreamClientWrapper<MLiveStreamWrapper>, ?>> list = new ArrayList<ColumnConfig<StreamClientWrapper<MLiveStreamWrapper>, ?>>();

		final ColumnConfig<StreamClientWrapper<MLiveStreamWrapper>, Map<String, String>> info = new ColumnConfig<StreamClientWrapper<MLiveStreamWrapper>, Map<String, String>>(
				new StreamBaseValueProvider(messages));
		final LiveStreamBasicCell<Map<String, String>> broadcastCell = new LiveStreamBasicCell<Map<String, String>>(
				af.liveStreamBasicCellAppearance(),
				LiveStreamBasicCell.PROGRAM_NAME_PROPERTY);
		info.setCell(broadcastCell);
		list.add(sm.getColumn());
		list.add(info);

		return new ColumnModel<StreamClientWrapper<MLiveStreamWrapper>>(list);
	}

	private void initializeGrid(final AppearanceFactory af,
			final QoSMessages messages) {
		final IdentityValueProvider<StreamClientWrapper<MLiveStreamWrapper>> identity = new IdentityValueProvider<StreamClientWrapper<MLiveStreamWrapper>>();
		final CheckBoxSelectionModel<StreamClientWrapper<MLiveStreamWrapper>> selectionModel = new CustomCheckBoxSelectionModel<StreamClientWrapper<MLiveStreamWrapper>>(
				identity,
				af.<StreamClientWrapper<MLiveStreamWrapper>> checkBoxColumnAppearance());
		selectionModel.setSelectionMode(SelectionMode.SIMPLE);
		uiFieldHolder.grid = new Grid<StreamClientWrapper<MLiveStreamWrapper>>(
				new ListStore<StreamClientWrapper<MLiveStreamWrapper>>(
						keyProvider), createColumnModel(af, messages,
						selectionModel),
				new GridView<StreamClientWrapper<MLiveStreamWrapper>>(af
						.gridAppearance()));
		uiFieldHolder.grid.addStyleName(ClientConstants.QOS_GRID_STYLE);
		uiFieldHolder.grid.addStyleName(ClientConstants.QOS_GRID_VIDEO_STYLE);
		uiFieldHolder.grid.setSelectionModel(selectionModel);
		uiFieldHolder.grid.setHideHeaders(true);
		uiFieldHolder.grid.getView().setStripeRows(true);
		uiFieldHolder.grid.setWidth(ClientConstants.DEFAULT_FIELD_WIDTH);
	}

	private void initializeListeners() {
		uiFieldHolder.okButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(final SelectEvent event) {
				getUiHandlers().<AddLiveVideoPresenter> cast().addStreams(
						uiFieldHolder.grid.getSelectionModel()
								.getSelectedItems());
				hide();
			}
		});
	}

	@Override
	public void select(final MAgent agent) {
		super.select(agent);
		getUiHandlers().agentSelected(agent);
	}

	@Override
	public void setStreams(
			final List<StreamClientWrapper<MLiveStreamWrapper>> streams) {
		uiFieldHolder.grid.getStore().clear();
		uiFieldHolder.grid.getStore().addAll(streams);
	}

}
