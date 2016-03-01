/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.media.client.Audio;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.Viewport;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.gwt.client.QoSIcons;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.event.alert.RefreshAlertGridViewEvent;
import com.tecomgroup.qos.gwt.client.event.alert.StopAudibleAlertEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.MainPagePresenter;
import com.tecomgroup.qos.gwt.client.sound.AudibleAlert;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AppUtils.ApplicationMode;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.CommentMode;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.ConfirmationHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.Anchor;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.FlickeringAnchor;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.menu.XToolBar;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.menu.XtoolBarIconItem;

import static com.google.gwt.user.client.Window.Location.*;


/**
 * @author abondin
 *
 */

public class MainPageView extends ViewWithUiHandlers<MainPagePresenter>
		implements
			MainPagePresenter.MyView {

	public static final int MENU_HEIGHT = 40;

	private static final int USER_BUTTONS_COUNT = 3;

	private final Viewport viewport;

	private final Set<String> navigationPaths = new HashSet<String>();

	private final BorderLayoutContainer borderLayoutContainer;

	private final ContentPanel mainContent;

	private final XToolBar navigationLinks;

	private final AutoProgressMessageBox loadingMask;

	private final QoSMessages messages;

	private Dialog systemInformationWindow;

	private final AppearanceFactory appearanceFactory;

	private final DialogFactory dialogFactory;

	@Inject
	public MainPageView(final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory) {
		this.messages = messages;
		this.appearanceFactory = appearanceFactoryProvider.get();
		this.dialogFactory = dialogFactory;
		viewport = new Viewport();
		borderLayoutContainer = new BorderLayoutContainer(
				appearanceFactory.borderLayoutAppearance());
		mainContent = new ContentPanel();
		mainContent.setHeaderVisible(false);
		mainContent.setBorders(false);
		mainContent.setBodyBorder(false);

		navigationLinks = new XToolBar(appearanceFactoryProvider.get()
				.toolBarAppearance(), appearanceFactoryProvider.get()
                .<String> buttonCellLightAppearance(), messages.more());

		final ImageResource image = (AppUtils.getApplicationMode() == ApplicationMode.POINT)
				? appearanceFactory.resources().baseLogoPoint()
				: appearanceFactory.resources().baseLogoVision();
		final Image logo = new Image(image);

		final XtoolBarIconItem logoItem = new XtoolBarIconItem.Builder()
				.icon(logo).title(messages.dashboard())
                .placeToken(QoSNameTokens.dashboard).build();
		logoItem.setStyleName(appearanceFactory.resources().css().baseLogo());
		navigationLinks.add(logoItem);

		navigationLinks.add(new FillToolItem());
		final ContentPanel topMenu = new ContentPanel();
		topMenu.setHeaderVisible(false);
		topMenu.setBorders(false);
		topMenu.setBodyBorder(false);
		topMenu.setWidget(navigationLinks);

		borderLayoutContainer.setNorthWidget(topMenu, new BorderLayoutData(0));
		borderLayoutContainer.setCenterWidget(mainContent);
		viewport.add(borderLayoutContainer);

		loadingMask = new AutoProgressMessageBox(messages.loading());
	}
	private void addLogoutMenuItem() {
		final Image logout = new Image(appearanceFactory.resources().logout());

		final ConfirmationDialog confirmationDialog = dialogFactory
                .createConfirmationDialog(new ConfirmationHandler() {

                                              @Override
                                              public void onCancel() {
                                                  // Do nothing
                                              }

                                              @Override
                                              public void onConfirm(final String comment) {
                                                  getUiHandlers().actionLogout();
                                              }
                                          }, messages.logout(), messages.logoutMessage(),
						CommentMode.DISABLED);

		logout.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				confirmationDialog.show();
			}
		});
		final XtoolBarIconItem logoutPanel = new XtoolBarIconItem.Builder()
				.icon(logout).title(messages.logout())
				.popup(confirmationDialog).build();
		logoutPanel.addStyleName(appearanceFactory.resources().css()
				.userLogoutButton());

		navigationLinks.add(logoutPanel);
	}

	@Override
	public void addNavigationLink(final String path, final String displayName,
			final QoSIcons icon, final Integer index,
			final Integer minScreenWidth) {
		if (navigationPaths.add(path)) {
			final FlickeringAnchor link;
			if (displayName.equals(messages.navigationChannelView())
					|| displayName.equals(messages.navigationRemoteProbeConfig())
					|| displayName.equals(messages.navigationRecordSchedule())) {
				StringBuilder customHref = new StringBuilder();
				customHref.append(getProtocol()).append("//")
						  .append(getHost())
						  .append(getPath().substring(0, getPath().lastIndexOf("/") + 1) + path.substring(0, path.lastIndexOf("#")))
						  .append(getQueryString())
						  .append(path.substring(path.lastIndexOf("#")));
				link = new FlickeringAnchor(customHref.toString(),
						displayName, appearanceFactory.flickeringAnchorAppearance());
				link.setItemId(path);
			} else{
				link = new FlickeringAnchor("#" + path,
						displayName, appearanceFactory.flickeringAnchorAppearance());
				link.setItemId(path);
			}

			if (path.equals(QoSNameTokens.alerts)) {
				link.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(final ClickEvent event) {

						// if link wrapper was clicked, not a nested html link
						if (event.getNativeEvent().getCurrentEventTarget() == event
								.getNativeEvent().getEventTarget()) {
							return;
						}

						if (getUiHandlers().audibleAlertIsActive()) {
							link.stopFlickering();
							AppUtils.showInfoMessage(messages
                                    .someAlertsMayBeInvisible());
							AppUtils.getEventBus().fireEvent(
									new StopAudibleAlertEvent());
							AppUtils.getEventBus().fireEvent(
									new RefreshAlertGridViewEvent());
						}
					}
				});
			}
			navigationLinks.insert(link, 1);
		}
	}

	private void addUserProfileMenuItem() {
		final MUser user = AppUtils.getCurrentUser().getUser();
		final Anchor userProfileButton = new Anchor("#"
				+ QoSNameTokens.userProfile, user.getLogin(),
				appearanceFactory.anchorAppearance());
		userProfileButton.setItemId(QoSNameTokens.userProfile);
		navigationPaths.add(QoSNameTokens.userProfile);
		navigationLinks.add(userProfileButton);
	}

	private void addUserSettingsMenuItem() {
		final Image userSettings = new Image(appearanceFactory.resources()
				.userSettings());

		final XtoolBarIconItem userSettingsPanel = new XtoolBarIconItem.Builder()
				.icon(userSettings).title(messages.settings())
                .popup(systemInformationWindow).build();
		userSettingsPanel.addStyleName(appearanceFactory.resources().css()
				.userSettingsButton());
		navigationLinks.add(userSettingsPanel);
	}

	@Override
	public void agentChanged(final String agentName) {
	}

	@Override
	public Widget asWidget() {
		return viewport;
	}

	@Override
	public void attachAudibleAlerts(final Collection<AudibleAlert> audibleAlerts) {
		for (final Audio audio : audibleAlerts) {
			audio.setVisible(false);
			navigationLinks.add(audio);
		}
	}

	private void deselectAll() {
		final String selected = appearanceFactory.resources().css().selected();
		for (final Iterator<String> iterator = navigationPaths.iterator(); iterator
				.hasNext();) {
			navigationLinks.getItemByItemId(iterator.next()).removeStyleName(
					selected);
		}
	}

	@Override
	public Set<String> getNavigationPaths() {
		return navigationPaths;
	}

	private BorderLayoutData getNorthLayoutData() {
		return new BorderLayoutData(MENU_HEIGHT);
	}

	@Override
	public void load(final String navigationToken) {
		if (navigationPaths.contains(navigationToken)) {
			deselectAll();
			final Anchor link = (Anchor) navigationLinks
                    .getItemByItemId(navigationToken);
			link.addStyleName(appearanceFactory.resources().css().selected());
		}

	}

	protected void onAgentSelect(final SelectEvent event) {
		getUiHandlers().actionNavigate(QoSNameTokens.probesAndTasks);
	}

	private void setBottomContent(final IsWidget content) {
		final FramedPanel southPanel = new FramedPanel(
				appearanceFactory.framedPanelAppearance());
		southPanel.setHeaderVisible(false);
		southPanel.setBorders(false);
		southPanel.setBodyBorder(false);
		southPanel.setHeight(50);
		borderLayoutContainer
                .setSouthWidget(southPanel, new BorderLayoutData());
		southPanel.setWidget(content);
		borderLayoutContainer.forceLayout();
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		if (slot == MainPagePresenter.TYPE_SetMainContent) {
			setMainContent(content);
		} else if (slot == MainPagePresenter.TYPE_SetBottomContent
				&& content == null) {
			borderLayoutContainer.setSouthWidget(null);
		} else if (slot == MainPagePresenter.TYPE_SetBottomContent) {
			setBottomContent(content);
		} else {
			super.setInSlot(slot, content);
		}
	}

	private void setMainContent(final IsWidget content) {
		mainContent.clear();
		if (content != null) {
			mainContent.setWidget(content);
			borderLayoutContainer.forceLayout();
		}
	}

	@Override
	public void setSystemInformation(final Widget content) {
		if (content instanceof Dialog) {
			systemInformationWindow = (Dialog) content;
		} else {
			systemInformationWindow = new Dialog(
					appearanceFactory.dialogAppearanceWithHeader());
			final SimplePanel panel = new SimplePanel();
			panel.setWidget(content);
			systemInformationWindow.add(panel);
			systemInformationWindow.hide();
		}
	}

	@Override
	public void setTitle(final String title) {
	}

	@Override
	public void showErrorDialog(final String message, final Throwable e) {
		dialogFactory.createErrorDialog(message, e).show();
	}

	@Override
	public void showInfoDialog(final String message) {
		Info.display(messages.message(), message);
	}

	@Override
	public void showInfoWithConfirmDialog(final String message) {
		dialogFactory.createInformationDialog(message).show();
	}

	@Override
	public void showLoading(final boolean loading, String text) {
		if (text == null) {
			text = messages.loading();
		}
		loadingMask.setProgressText(text);
		if (loading) {
			loadingMask.show();
			loadingMask.center();
		} else {
			loadingMask.hide();
		}
	}

	@Override
	public void startStopFlickering(final PerceivedSeverity severity) {
		final Anchor anchor = (Anchor) navigationLinks
                .getItemByItemId(QoSNameTokens.alerts);
		if (anchor instanceof FlickeringAnchor) {
			((FlickeringAnchor) anchor).handleAlertEvent(severity);
		}
	}

	@Override
	public void userLoggedIn(final MUser user) {
		addUserProfileMenuItem();
		addUserSettingsMenuItem();
		addLogoutMenuItem();

		// show top menu
		borderLayoutContainer.setNorthWidget(
				borderLayoutContainer.getNorthWidget(), getNorthLayoutData());
		viewport.forceLayout();
	}

	@Override
	public void userLoggedOut() {
		for (int i = 0; i < USER_BUTTONS_COUNT; i++) {
			navigationLinks.remove(navigationLinks.getWidgetCount() - 1);
		}
		// hide top menu
		borderLayoutContainer
                .setNorthWidget(borderLayoutContainer.getNorthWidget(),
						new BorderLayoutData(0));
		viewport.forceLayout();
	}

}
