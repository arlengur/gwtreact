/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.dashboard;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.tecomgroup.qos.Statefull;
import com.tecomgroup.qos.domain.MDashboard;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.event.chart.NavigateToChartEvent;
import com.tecomgroup.qos.gwt.client.event.dashboard.RemoveWidgetFromDashboardEvent;
import com.tecomgroup.qos.gwt.client.event.dashboard.SaveDashboardWidgetEvent;
import com.tecomgroup.qos.gwt.client.style.common.TileAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.base.panel.TileBaseAppearance;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.chart.DashboardChartClientWidget;

/**
 * It represents panel with header, which contains title and remove button.
 * Content of panel is rendered by corresponding module. Instance of
 * {@link Tile} holds link to its model object {@link Tile#tileContentElement}.
 * See {@link MDashboard} to details about this feature. The
 * {@link Tile#tileContentElement} is a delegate of appropriate entity.
 * 
 * @author ivlev.e
 */
public class Tile extends ComplexPanel {

	private final TileAppearance appearance;

	private final Widget content;

	private final TileContentElement tileContentElement;

    public Tile(final TileContentElement tileContentElement) {
		this(tileContentElement,
            GWT.<TileAppearance> create(TileBaseAppearance.class)
        );
	}

	public Tile(final TileContentElement tileContentElement,
                final TileAppearance appearance) {
		super();
		this.appearance = appearance;
		this.tileContentElement = tileContentElement;
		this.content = tileContentElement.getContentElement();
		content.addStyleName(appearance.style().innerContent());

		final SafeHtmlBuilder sb = new SafeHtmlBuilder();
		appearance.render(sb, tileContentElement.getModel().getTitle());
		setElement(XDOM.create(sb.toSafeHtml()));
		add(content, getContentElement());

		initializeListeners();
	}

	private Element getContentElement() {
		return appearance.getContentElement(getElement().<XElement> cast());
	}

	private Element getHeaderElement() {
		return appearance.getHeaderElement(getElement().<XElement> cast());
	}

	private void initializeListeners() {
		final Element header = getHeaderElement();
		DOM.sinkEvents(header, Event.ONCLICK);
		DOM.setEventListener(header, new EventListener() {

			@Override
			public void onBrowserEvent(final Event event) {
				final XElement target = Element.as(event.getEventTarget())
						.<XElement> cast();
				if (appearance.isRemoveButtonPressed(target)) {
					AppUtils.getEventBus().fireEvent(
							new RemoveWidgetFromDashboardEvent(
									tileContentElement.getModel().getKey()));
				}
				if (appearance.isSaveButtonPressed(target)
						&& tileContentElement instanceof Statefull) {
					((Statefull) tileContentElement).saveState();
					AppUtils.getEventBus().fireEvent(
							new SaveDashboardWidgetEvent(tileContentElement
									.getModel()));
				}
                if (appearance.isChartButtonPressed(target)
                        && tileContentElement instanceof DashboardChartClientWidget) {
                    final PlaceRequest request = (new PlaceRequest.Builder())
                            .nameToken(QoSNameTokens.chartResults)
                            .build();
                    AppUtils.getPlaceManager().revealPlace(request);
                    final DashboardChartClientWidget chartWidget =
                            (DashboardChartClientWidget) tileContentElement;
                    final NavigateToChartEvent focusEvent
                            = new NavigateToChartEvent(chartWidget.getModel());
                    AppUtils.getEventBus().fireEvent(focusEvent);
                }
			}
		});
	}
}
