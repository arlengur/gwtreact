package com.tecomgroup.qos.gwt.client.presenter.widget;

import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.BaseDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.Anchor;

/**
 * @author galin.a on 12.01.2016.
 */
public class CopyToClipboardDialog extends BaseDialog<QoSMessages> {
    private final String title;
    private final String link;
    private final String encodeLink;
    private final static int MIN_WIDTH = 280;
    private final static String CLIPBOARD_BUTTON_ID = "copyToClipboardButton";
    private final static String CLIPBOARD_SOURCE_ID = "clipboardSourceElement";

    /**
     * Element with this id should contain data which will be copied to
     * clipboard
     */
    private final static String DATA_CLIPBOARD_TARGET = "data-clipboard-target";

    public CopyToClipboardDialog(final AppearanceFactory appearanceFactory, final QoSMessages messages, final String title, String encodeLink, String link) {
        super(appearanceFactory, messages);
        this.title = title;
        this.encodeLink = encodeLink;
        this.link = link;
        setMinWidth(MIN_WIDTH);
    }

    @Override
    protected String getTitleText(QoSMessages messages) {
        return title;
    }

    @Override
    protected void createButtons() {
        super.createButtons();
        TextButton copyToClipboardButton = createDialogButton(messages.copyToClipboard(), CLIPBOARD_BUTTON_ID);
        copyToClipboardButton.getElement().setId(CLIPBOARD_BUTTON_ID);
        copyToClipboardButton.getElement().setAttribute(DATA_CLIPBOARD_TARGET, CLIPBOARD_SOURCE_ID);
        copyToClipboardButton.setWidth(150);
        buttonBar.insert(copyToClipboardButton, 0);
    }

    @Override
    protected void initializeComponents() {
        final VerticalLayoutContainer container = new VerticalLayoutContainer();
        final VerticalLayoutContainer.VerticalLayoutData margings = new VerticalLayoutContainer.VerticalLayoutData();
        margings.setMargins(new Margins(5, 5, 15, 5));
        if (link != null) {
            Label label = new Label(messages.downloadVideoWarning());
            container.add(label, margings);
            
            final Anchor anchor = new Anchor(encodeLink, link, "_blank");
            anchor.addStyleName(appearanceFactory.resources().css().textMainColor());
            container.add(anchor, margings);
        }
        add(container);
    }

    @Override
    protected void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        AppUtils.initClipboard(CLIPBOARD_BUTTON_ID, CLIPBOARD_SOURCE_ID, messages.copiedToClipboard());
        AppUtils.setElementValue(CLIPBOARD_SOURCE_ID, encodeLink);
    }
}
