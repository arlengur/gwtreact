/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PopupViewCloseHandler;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.gwtplatform.mvp.client.proxy.NavigationEvent;
import com.gwtplatform.mvp.client.proxy.NavigationHandler;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

/**
 * 
 * Анолог {@link PopupViewWithUiHandlers} только не для {@link PopupPanel}, а
 * для sencha {@link Window}
 * 
 * @author abondin
 * 
 */
public abstract class SenchaPopupView<C extends UiHandlers>
		extends
			ViewWithUiHandlers<C> implements PopupView {

	private HandlerRegistration autoHideHandler;

	private HandlerRegistration closeHandlerRegistration;

	private final EventBus eventBus;

	/**
	 * 
	 */
	public SenchaPopupView(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public Window asWindow() {
		return (Window) asWidget();
	}

	@Override
	public void center() {
		asWindow().center();
	}
	@Override
	public void hide() {
		asWindow().hide();
	}
	@Override
	public void setAutoHideOnNavigationEventEnabled(final boolean autoHide) {
		if (autoHide) {
			if (autoHideHandler != null) {
				return;
			}
			autoHideHandler = eventBus.addHandler(NavigationEvent.getType(),
					new NavigationHandler() {
						@Override
						public void onNavigation(
								final NavigationEvent navigationEvent) {
							hide();
						}
					});
		} else {
			if (autoHideHandler != null) {
				autoHideHandler.removeHandler();
			}
		}
	}
	@Override
	public void setCloseHandler(
			final PopupViewCloseHandler popupViewCloseHandler) {
		if (closeHandlerRegistration != null) {
			closeHandlerRegistration.removeHandler();
		}
		if (popupViewCloseHandler == null) {
			closeHandlerRegistration = null;
		} else {
			closeHandlerRegistration = asWindow().addHideHandler(
					new HideHandler() {
						@Override
						public void onHide(final HideEvent event) {
							popupViewCloseHandler.onClose();
						}
					});
		}
	}

	@Override
	public void setPosition(final int left, final int top) {
		asWindow().setPosition(left, top);
	}

	@Override
	public void show() {
		asWindow().show();
	}
}
