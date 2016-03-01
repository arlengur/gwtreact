package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.tecomgroup.qos.CrudOperations;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractEntityEditorDialogPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.SenchaPopupView;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.QoSDialog;

/**
 * @author sviyazov.a
 * @see AbstractEntityEditorDialogPresenter
 */
public abstract class AbstractEntityEditorDialogView<T, P extends AbstractEntityEditorDialogPresenter<T, ?>>
        extends
        SenchaPopupView<P>
        implements
        AbstractEntityEditorDialogPresenter.MyView<T, P> {

    protected QoSDialog dialog;

    protected final QoSMessages messages;

    protected final AppearanceFactory appearanceFactory;

    private CrudOperations mode = CrudOperations.CREATE;

    private Widget dialogContent;

    public AbstractEntityEditorDialogView(final EventBus eventBus,
                                          final QoSMessages messages) {
        super(eventBus);
        this.messages = messages;
        appearanceFactory = AppearanceFactoryProvider.instance();
    }

    abstract protected void actionOkButtonPressed();

    @Override
    public Widget asWidget() {
        return dialog;
    }

    protected QoSDialog createDialog() {
        final QoSDialog dialog = new QoSDialog(appearanceFactory, messages) {

            @Override
            protected TextButton createDialogButton(final String text,
                                                    final String itemId) {
                final TextButton textButton = new TextButton(
                        new TextButtonCell(
                                appearanceFactory
                                        .<String>buttonCellHugeAppearance()),
                        text);
                textButton.setWidth(130);
                textButton.setItemId(itemId);
                return textButton;
            }

            @Override
            protected TextButton getButtonPressedOnEnter() {
                return getSaveButton();
            }

            protected TextButton getSaveButton() {
                return (TextButton) getButtonBar().getItemByItemId(
                        PredefinedButton.OK.name());
            }

            @Override
            protected String getTitleText(final QoSMessages messages) {
                return CrudOperations.CREATE.equals(mode)
                        ? getCreationDialogTitle()
                        : getUpdateDialogTitle();
            }

            @Override
            protected void initializeComponents() {
            }

            @Override
            protected void onButtonPressed(final TextButton button) {
                if (button == getCancelButton() || button == getCloseButton()) {
                    hide();
                } else if (button == getSaveButton()) {
                    actionOkButtonPressed();
                }
            }
        };

        if (dialogContent == null) {
            this.dialogContent = getDialogContent();
        }

        dialog.add(dialogContent);


        dialog.setPredefinedButtons(PredefinedButton.OK,
                PredefinedButton.CANCEL);
        dialog.setButtonAlign(BoxLayoutPack.CENTER);

        return dialog;
    }

    protected String getCreationDialogOkButtonText() {
        return messages.create();
    }

    protected String getCreationDialogTitle() {
        return messages.create();
    }

    /**
     * Returns a dialog content widget. If returns null, no content for the
     * dialog will be set.
     */
    abstract protected Widget getDialogContent();

    protected String getUpdateDialogOkButtonText() {
        return messages.actionSave();
    }

    protected String getUpdateDialogTitle() {
        return messages.edit();
    }

    @Inject
    public void initialize() {
        dialog = createDialog();

        // to fix reduce of height after every showing
        dialog.addHideHandler(new HideEvent.HideHandler() {
            @Override
            public void onHide(final HideEvent event) {
                initialize();
            }
        });
    }

    @Override
    public void setMode(final CrudOperations mode) {
        this.mode = mode;
        if (CrudOperations.CREATE.equals(mode)) {
            updateOkButtonText(getCreationDialogOkButtonText());
        } else if (CrudOperations.UPDATE.equals(mode)) {
            updateOkButtonText(getUpdateDialogOkButtonText());
        }
    }

    private void updateOkButtonText(final String text) {
        final TextButton button = (TextButton) dialog.getButtonBar()
                .getItemByItemId(PredefinedButton.OK.name());
        button.setText(text);
    }

    @Override
    abstract public boolean validate();
}
