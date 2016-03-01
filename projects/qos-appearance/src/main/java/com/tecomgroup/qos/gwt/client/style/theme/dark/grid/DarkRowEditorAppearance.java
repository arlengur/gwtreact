package com.tecomgroup.qos.gwt.client.style.theme.dark.grid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.theme.base.client.grid.RowEditorDefaultAppearance;

/**
 * @author meleshin.o
 *
 */
public class DarkRowEditorAppearance extends RowEditorDefaultAppearance {

    public interface DarkRowEditorResources extends RowEditorResources {

        @Source("DarkRowEditor.css")
        @Override
        DarkRowEditorStyle css();

        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
        ImageResource editorButtonLeft();

        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
        ImageResource editorButtonRight();

        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.Horizontal)
        ImageResource editorButtonBackground();
    }

    public interface DarkRowEditorStyle extends RowEditorStyle {

    }

    public DarkRowEditorAppearance(){
        this(GWT.<DarkRowEditorResources> create(DarkRowEditorResources.class));
    }

    public DarkRowEditorAppearance(final RowEditorResources resources){
        super(resources);
    }
}
