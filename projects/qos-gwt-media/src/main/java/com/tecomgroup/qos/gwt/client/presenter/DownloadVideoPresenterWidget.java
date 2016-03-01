/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.proxy.AsyncCallFailEvent;
import com.gwtplatform.mvp.client.proxy.AsyncCallStartEvent;
import com.gwtplatform.mvp.client.proxy.AsyncCallSucceedEvent;
import com.sencha.gxt.data.client.loader.JsoReader;
import com.sencha.gxt.data.client.loader.ScriptTagProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadExceptionEvent;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.LoaderHandler;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.model.PlaylistItem;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.UserAgentUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.PlaylistItemProperties;

/**
 * Presenter for download video dialog window
 * 
 * @author meleshin.o
 */
public class DownloadVideoPresenterWidget
		extends
			PresenterWidget<DownloadVideoPresenterWidget.MyView>
		implements
			UiHandlers {

	public interface MyView
			extends
				PopupView,
				HasUiHandlers<DownloadVideoPresenterWidget> {
		public void copyToClipboard(List<PlaylistItem> items);

		public void initClipboard(String message);

		public void initialize();

		public void setLoader(
				ListLoader<ListLoadConfig, ListLoadResult<PlaylistItem>> loader);

		public void setUrl(String url);

		public void showDialog();
	}

	public interface PlayListItemAutoBeanFactory extends AutoBeanFactory {
		AutoBean<RecordResult> items();

		AutoBean<ListLoadConfig> loadConfig();
	}

	public interface RecordResult {
		List<PlaylistItem> getItems();
	}

	private final QoSMessages messages;

	private final PlayListItemAutoBeanFactory factory = GWT
			.create(PlayListItemAutoBeanFactory.class);

	private final PlaylistItemProperties properties = GWT
			.create(PlaylistItemProperties.class);

	private ListStore<PlaylistItem> store;

	private ListLoadConfig loadConfig;

	private LoaderHandler<ListLoadConfig, ListLoadResult<PlaylistItem>> loaderHandler;

	private LoadResultListStoreBinding<ListLoadConfig, PlaylistItem, ListLoadResult<PlaylistItem>> bindingHandler;

	private JsoReader<ListLoadResult<PlaylistItem>, RecordResult> reader;

	private ListLoader<ListLoadConfig, ListLoadResult<PlaylistItem>> loader;

	@Inject
	public DownloadVideoPresenterWidget(final EventBus eventBus,
			final MyView view, final QoSMessages messages) {
		super(eventBus, view);
		this.messages = messages;

		initializeStore();
		initializeLoadConfig();
		initializeReader();
		initializeLoadHandlers();
		getView().setUiHandlers(this);
	}

	private ListLoader<ListLoadConfig, ListLoadResult<PlaylistItem>> createLoader(
			final String url) {
		final ScriptTagProxy<ListLoadConfig> proxy = new ScriptTagProxy<ListLoadConfig>(
				url);
		final ListLoader<ListLoadConfig, ListLoadResult<PlaylistItem>> loader;
		loader = new ListLoader<ListLoadConfig, ListLoadResult<PlaylistItem>>(
				proxy, reader);
		loader.useLoadConfig(loadConfig);
		loader.addLoadHandler(bindingHandler);
		loader.addLoaderHandler(loaderHandler);
		return loader;
	}

	public ListLoader<ListLoadConfig, ListLoadResult<PlaylistItem>> getLoader() {
		return loader;
	}

	public PlaylistItemProperties getPlaylistItemProperties() {
		return properties;
	}

	public ListStore<PlaylistItem> getStore() {
		return store;
	}

	private void initializeLoadConfig() {
		loadConfig = factory.create(ListLoadConfig.class).as();
	}

	private void initializeLoadHandlers() {
		final EventBus eventBus = getEventBus();

		loaderHandler = new LoaderHandler<ListLoadConfig, ListLoadResult<PlaylistItem>>() {

			@Override
			public void onBeforeLoad(final BeforeLoadEvent<ListLoadConfig> event) {
				getStore().clear();
				AsyncCallStartEvent.fire(eventBus);
			}

			@Override
			public void onLoad(
					final LoadEvent<ListLoadConfig, ListLoadResult<PlaylistItem>> event) {
				AsyncCallSucceedEvent.fire(eventBus);
				getView().showDialog();
			}

			@Override
			public void onLoadException(
					final LoadExceptionEvent<ListLoadConfig> event) {
				AsyncCallFailEvent.fire(eventBus, null);
				AppUtils.showErrorMessage(messages.playlistDownloadFail());
			}

		};
	}

	private void initializeReader() {
		reader = new JsoReader<ListLoadResult<PlaylistItem>, RecordResult>(
				factory, RecordResult.class) {

			@Override
			protected ListLoadResult<PlaylistItem> createReturnData(
					final Object loadConfig, final RecordResult records) {
				final List<PlaylistItem> items = records.getItems();
				// copy string representation of list to invisible element which
				// is source for copy to clipboard
				if (UserAgentUtils.isDesktop()) {
					getView().copyToClipboard(items);
				}

				return new ListLoadResultBean<PlaylistItem>(items);
			}
		};
	}

	private void initializeStore() {
		store = new ListStore<PlaylistItem>(properties.key());
	}

	@Override
	protected void onBind() {
		final MyView view = getView();
		view.initialize();

		bindingHandler = new LoadResultListStoreBinding<ListLoadConfig, PlaylistItem, ListLoadResult<PlaylistItem>>(
				store);
	}

	@Override
	protected void onReveal() {
		super.onReveal();

		loader.load();
	}

	public void setUrl(final String url) {
		final MyView view = getView();

		loader = createLoader(url);
		view.setLoader(loader);
		view.setUrl(url);
	}
}
