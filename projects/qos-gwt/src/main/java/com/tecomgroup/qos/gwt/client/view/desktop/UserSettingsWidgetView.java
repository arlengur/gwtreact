/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import java.util.Arrays;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.tecomgroup.qos.domain.UserSettings.NotificationLanguage;
import com.tecomgroup.qos.domain.UserSettings.AudibleAlertFeatureMode;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.UserSettingsWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;

/**
 * @author sviyazov.a
 * 
 */
public class UserSettingsWidgetView
		extends
			ViewWithUiHandlers<UserSettingsWidgetPresenter>
		implements
			UserSettingsWidgetPresenter.MyView {

	private final QoSMessages messages;

	private final AppearanceFactory appearanceFactory;

	private ComboBox<AudibleAlertFeatureMode> audibleAlertModeComboBox;

    private ComboBox<NotificationLanguage> notificationLanguageComboBox;

	private final VBoxLayoutContainer innerContainer;

	private final BorderLayoutContainer outerContainer;

	private final FramedPanel framedPanel;

	@Inject
	public UserSettingsWidgetView(
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final QoSMessages messages) {
		this.appearanceFactory = appearanceFactoryProvider.get();
		this.messages = messages;

		outerContainer = new BorderLayoutContainer(
				appearanceFactory.borderLayoutAppearance());

		framedPanel = new FramedPanel(
				appearanceFactory.lightFramedPanelAppearance());

		framedPanel.setHeaderVisible(false);
		framedPanel.setBorders(false);
		framedPanel.setBodyBorder(false);

		innerContainer = new VBoxLayoutContainer();

		configure();
	}

	@Override
	public Widget asWidget() {
		return outerContainer;
	}

	private void configure() {
        setupAudioAlertModeCombo();
        setupNotificationLanguageCombo();

		final BoxLayoutData layoutData = new BoxLayoutData(new Margins(8, 0, 8, 0));
		innerContainer.add(new Label(messages.audibleAlertNotificationMode() + ":"), layoutData);
		innerContainer.add(audibleAlertModeComboBox);
        innerContainer.add(new Label(messages.notificationLanguage() + ":"), layoutData);
        innerContainer.add(notificationLanguageComboBox);

		framedPanel.add(innerContainer);
		outerContainer.add(framedPanel);
	}

    private void setupNotificationLanguageCombo() {
        final ListStore<NotificationLanguage> notificationLanguageStore = new ListStore<NotificationLanguage>(
                new ModelKeyProvider<NotificationLanguage>() {
                    @Override
                    public String getKey(final NotificationLanguage item) {
                        return item.name();
                    }
                });

        notificationLanguageStore.addAll(Arrays.asList(NotificationLanguage.values()));

        notificationLanguageComboBox = new ComboBox<NotificationLanguage>(
                notificationLanguageStore,
                new LabelProvider<NotificationLanguage>() {
                    @Override
                    public String getLabel(final NotificationLanguage item) {
                        final String label;
                        switch (item) {
                            case EN:
                                label = messages.english();
                                break;
                            case RU:
                                label = messages.russian();
                                break;
                            case RU_TRANSLIT:
                                label = messages.translit();
                                break;
                            default :
                                label = item.toString();
                        }
                        return label;
                    }
                });

        setupCombo(notificationLanguageComboBox);

        notificationLanguageComboBox
                .addSelectionHandler(new SelectionHandler<NotificationLanguage>() {
                    @Override
                    public void onSelection(
                            final SelectionEvent<NotificationLanguage> event) {
                        getUiHandlers().updateNotificationLanguage(event.getSelectedItem());
                    }
                });

        notificationLanguageComboBox.setValue(AppUtils.getNotificationLanguage());
    }

    private void setupAudioAlertModeCombo() {
        final ListStore<AudibleAlertFeatureMode> audibleAlertModeStore = new ListStore<AudibleAlertFeatureMode>(
                new ModelKeyProvider<AudibleAlertFeatureMode>() {
                    @Override
                    public String getKey(final AudibleAlertFeatureMode item) {
                        return item.name();
                    }
                });

        audibleAlertModeStore.addAll(Arrays.asList(AudibleAlertFeatureMode
                .values()));

        audibleAlertModeComboBox = new ComboBox<AudibleAlertFeatureMode>(
                audibleAlertModeStore,
                new LabelProvider<AudibleAlertFeatureMode>() {

                    @Override
                    public String getLabel(final AudibleAlertFeatureMode item) {
                        final String label;
                        switch (item) {
                            case ON :
                                label = messages.audibleAlertModeOn();
                                break;
                            case OFF :
                                label = messages.audibleAlertModeOff();
                                break;
                            case MUTE :
                                label = messages.audibleAlertModeMute();
                                break;
                            default :
                                label = item.toString();
                        }
                        return label;
                    }
                });

        setupCombo(audibleAlertModeComboBox);

        audibleAlertModeComboBox
                .addSelectionHandler(new SelectionHandler<AudibleAlertFeatureMode>() {
                    @Override
                    public void onSelection(
                            final SelectionEvent<AudibleAlertFeatureMode> event) {
                        getUiHandlers().updateAudibleAlertMode(
                                event.getSelectedItem());
                    }
                });

        audibleAlertModeComboBox.setValue(AppUtils.getAudibleAlertMode());
    }

    private void setupCombo(final ComboBox comboBox) {
        comboBox.setAllowBlank(false);
        comboBox.setForceSelection(true);
        comboBox.setValidateOnBlur(false);
        comboBox.setTypeAhead(true);
        comboBox.setTriggerAction(TriggerAction.ALL);
        comboBox.setEditable(false);
        comboBox.setWidth(170);
    }
}
