/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.FramedPanel.FramedPanelAppearance;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HasLayout;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.rbac.MRole;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.model.TreeGridRow;
import com.tecomgroup.qos.gwt.client.presenter.UserProfilePresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.RoleLabelProvider;

/**
 * @author ivlev.e
 * 
 */
public class UserProfileView
		extends
			ViewWithUiHandlers<UserProfilePresenter<?, ?>>
		implements
			UserProfilePresenter.MyView {

	interface ViewUiBinder extends UiBinder<Widget, UserProfileView> {
	}

	private static final ViewUiBinder UI_BINDER = GWT
			.create(ViewUiBinder.class);

	private Widget widget;

	private MUser currentUser;

	private final AppearanceFactory appearanceFactory;

	@UiField(provided = true)
	protected BorderLayoutContainer container;

	@UiField(provided = true)
	protected FramedPanel westPanel;

	@UiField(provided = true)
	protected FramedPanel userInformationPanel;

	@UiField(provided = true)
	protected VBoxLayoutContainer userInformationContainer;

	@UiField(provided = true)
	protected TabPanel tabContainer;

	@UiField(provided = true)
	protected FramedPanel centerFramePanel;

	private TreeGrid<TreeGridRow> templatesGrid;

	@UiField(provided = true)
	protected TextButton editProfile;

	private final QoSMessages messages;

	private final RoleLabelProvider roleLabelProvider;

	@Inject
	public UserProfileView(
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final QoSMessages messages) {
		this.appearanceFactory = appearanceFactoryProvider.get();
		this.messages = messages;
		this.roleLabelProvider = new RoleLabelProvider(messages);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	private void configureCenter() {
		centerFramePanel = createFramedPanel(appearanceFactory
				.framedPanelAppearance());
		tabContainer = new TabPanel(appearanceFactory.tabPanelAppearance());
		tabContainer.setBorders(false);
		tabContainer.addSelectionHandler(new SelectionHandler<Widget>() {

			@Override
			public void onSelection(final SelectionEvent<Widget> event) {
				// FIXME: HACK: fix for user settings layout bug (at rare
				// occasion elements overlap occurs)
				if (event.getSelectedItem() instanceof HasLayout) {
					((HasLayout) event.getSelectedItem()).forceLayout();
				}
			}
		});
	}

	private void configureRootContainer() {
		container = new BorderLayoutContainer(
				appearanceFactory.borderLayoutAppearance());
		container.setBorders(false);
	}

	private void configureWest() {
		final MRole userRole = currentUser.getRoles().get(0);

		westPanel = createFramedPanel(appearanceFactory.framedPanelAppearance());

		userInformationPanel = createFramedPanel(appearanceFactory
				.lightFramedPanelAppearance());

		userInformationContainer = new VBoxLayoutContainer();
		userInformationContainer.add(createHeader(currentUser.getLogin()),
				new BoxLayoutData(new Margins(0, 0, 8, 0)));
		userInformationContainer.add(new Label(messages.role() + ":"
				+ roleLabelProvider.getLabel(userRole)), new BoxLayoutData(
				new Margins(8, 0, 8, 0)));

		userInformationContainer.add(new Label(messages.contactInfo()),
				new BoxLayoutData(new Margins(8, 0, 8, 0)));

		userInformationContainer.add(new Label(messages.personalInformation()));

		editProfile = new TextButton(new TextButtonCell(
				appearanceFactory.<String> buttonCellHugeAppearance()));
		editProfile.setText(messages.editProfile());

		// FIXME : implement user profile editing
		editProfile.setVisible(false);

		final BoxLayoutData layoutData = new BoxLayoutData(new Margins(8, 0, 8,
				0));
		if (!AppUtils.getCurrentUser().getUser().isLdapAuthenticated()) {

			final Label changePasswordButton = new Label(
					messages.changePassword());
			changePasswordButton.addStyleName(appearanceFactory.resources()
					.css().clickableText());
			changePasswordButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(final ClickEvent event) {
					openChangeUserPasswordDialog();
				}
			});

			userInformationContainer.add(changePasswordButton, layoutData);
		}
	}

	private FramedPanel createFramedPanel(final FramedPanelAppearance appearance) {
		final FramedPanel panel = new FramedPanel(appearance);
		panel.setHeaderVisible(false);
		panel.setBorders(false);
		panel.setBodyBorder(false);

		return panel;
	}

	private Label createHeader(final String message) {
		final Label label = new Label(message);
		label.addStyleName(appearanceFactory.resources().css().text18px());
		label.addStyleName(appearanceFactory.resources().css().textMainColor());

		return label;
	}

	/**
	 * @return the appearanceFactory
	 */
	public AppearanceFactory getAppearanceFactory() {
		return appearanceFactory;
	}

	public TreeGrid<TreeGridRow> getTemplatesGrid() {
		return templatesGrid;
	}

	@Inject
	public void initialize() {
		currentUser = AppUtils.getCurrentUser().getUser();
		configureRootContainer();
		configureWest();
		configureCenter();

		widget = UI_BINDER.createAndBindUi(this);
	}

	private void openChangeUserPasswordDialog() {
		getUiHandlers().openChangeUserPasswordDialog();
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		if (tabContainer.getConfig(content.asWidget()) == null) {
			tabContainer.add(content.asWidget(),
					new TabItemConfig(slot.toString()));
		}
	}
}
