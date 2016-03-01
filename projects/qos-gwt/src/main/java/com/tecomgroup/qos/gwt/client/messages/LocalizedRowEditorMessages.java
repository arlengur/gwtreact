package com.tecomgroup.qos.gwt.client.messages;

import com.sencha.gxt.widget.core.client.grid.editing.GridRowEditing;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author meleshin.o
 *
 */
public class LocalizedRowEditorMessages extends AbstractLocalizedMessages implements GridRowEditing.RowEditorMessages {

    public LocalizedRowEditorMessages(QoSMessages messages) {
        super(messages);
    }

    @Override
    public String cancelText() {
        return messages.actionCancel();
    }

    @Override
    public String dirtyText() {
        return messages.rowEditorDirtyText();
    }

    @Override
    public String errorTipTitleText() {
        return messages.error();
    }

    @Override
    public String saveText() {
        return messages.actionOk();
    }
}
