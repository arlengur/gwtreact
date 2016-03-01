package com.tecomgroup.qos.gwt.client.view.desktop.widget.search;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.cell.core.client.form.TriggerFieldCell;
import com.tecomgroup.qos.gwt.client.style.theme.dark.cell.DarkSearchFieldAppearance;

/**
 * @author smyshlyaev.s
 */
public class SearchFieldCell extends TriggerFieldCell<String> {
    public SearchFieldCell() {
        super(GWT.<DarkSearchFieldAppearance>create(DarkSearchFieldAppearance.class));
    }
}
