package com.tecomgroup.qos.gwt.client.view.desktop.widget.search;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent;
import com.sencha.gxt.widget.core.client.form.TriggerField;

import static com.sencha.gxt.widget.core.client.event.TriggerClickEvent.TriggerClickHandler;

/**
 * @author smyshlyaev.s
 */
public class SearchField extends TriggerField<String> {

    public SearchField(final TriggerClickHandler handler, final String emptyText) {
        super(new SearchFieldCell());
        addTriggerClickHandler(handler);
        addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(final KeyDownEvent event) {
                if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    handler.onTriggerClick(new TriggerClickEvent());
                }
            }
        });
        setEmptyText(emptyText);
    }
}
