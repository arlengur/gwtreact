package com.tecomgroup.qos.gwt.client.style.theme.dark.cell;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.theme.base.client.field.TriggerFieldDefaultAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.field.DarkTriggerFieldAppearance;

/**
 * @author smyshlyaev.s
 */
public class DarkSearchFieldAppearance extends TriggerFieldDefaultAppearance{
    public interface DarkSearchFieldResources extends TriggerFieldResources{
        @Override
        @Source({"com/tecomgroup/qos/gwt/client/style/theme/dark/field/DarkTriggerField.css",
                 "com/sencha/gxt/theme/base/client/field/ValueBaseField.css",
                 "com/sencha/gxt/theme/base/client/field/TextField.css",
                 "com/sencha/gxt/theme/base/client/field/TriggerField.css"})
        DarkSearchFieldStyle css();

        @Override
        @Source("searchButton.png")
        ImageResource triggerArrow();

        @Override
        @Source("searchButtonClick.png")
        ImageResource triggerArrowClick();

        @Override
        @Source("searchButtonOver.png")
        ImageResource triggerArrowOver();

        @Override
        @Source("searchButton.png")
        ImageResource triggerArrowFocus();

        @Override
        @Source("searchButtonClick.png")
        ImageResource triggerArrowFocusClick();

        @Override
        @Source("searchButtonOver.png")
        ImageResource triggerArrowFocusOver();
    }

    public interface DarkSearchFieldStyle extends TriggerFieldStyle {
    }

    public DarkSearchFieldAppearance() {
        super(GWT.<DarkSearchFieldResources>create(DarkSearchFieldResources.class));
    }
}
