/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.tecomgroup.qos.CrudOperations;

/**
 * Provides a basic dialog with common entity editor style and logic. <br/>
 * Supports two modes: {@link CrudOperations#UPDATE} and
 * {@link CrudOperations#CREATE}.
 * 
 * @author sviyazov.a
 * 
 */
public class AbstractEntityEditorDialogPresenter<T, V extends AbstractEntityEditorDialogPresenter.MyView<T, ?>>
		extends
			PresenterWidget<V> implements UiHandlers {

	public static interface MyView<T, P extends AbstractEntityEditorDialogPresenter<T, ?>>
			extends
				PopupView,
				HasUiHandlers<P> {

		void reset();

		void setMode(CrudOperations mode);

		boolean validate();
	}

	private CrudOperations mode = CrudOperations.CREATE;

	protected T editableEntity = null;

	@Inject
	public AbstractEntityEditorDialogPresenter(final EventBus eventBus,
			final V view) {
		super(eventBus, view);
	}

	public CrudOperations getCurrentMode() {
		return mode;
	}

	protected void reset() {
		getView().reset();
	}

	public void setCreateMode() {
		this.mode = CrudOperations.CREATE;
		this.editableEntity = null;
		reset();
		getView().setMode(CrudOperations.CREATE);
	}

	public void setUpdateMode(final T editableEntity) {
		this.mode = CrudOperations.UPDATE;
		reset();
		this.editableEntity = editableEntity;
		getView().setMode(CrudOperations.UPDATE);
	}
}
