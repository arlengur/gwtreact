/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.tecomgroup.qos.gwt.client.event.dashboard.ChangeDashboardPageEvent;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;

/**
 * This component controls number of page of any arbitrary list of data. Use
 * {@link DashboardPagerPresenterWidget#setPageCount(int)} to define page count
 * and render view. View consists of circles for each page and left/right arrows
 * to navigate previous/next page by user's click. The component fires
 * {@link ChangeDashboardPageEvent} on the {@link EventBus} when user clicks
 * specified page.
 * 
 * @author kshnyakin.m
 */
public class DashboardPagerPresenterWidget
		extends
			PresenterWidget<DashboardPagerPresenterWidget.MyView>
		implements
			UiHandlers {

	public static interface MyView
			extends
				View,
				HasUiHandlers<DashboardPagerPresenterWidget> {

		void clear();

		void drawNavigateWidget(int pageCount);

		void redrawNavigateWidget(int previousPage, int nextPage);

	}

	public static int validatePageNumber(final int pageNumber,
			final int pageCount) {
		int validatedPageNumber = pageNumber;

		if (pageNumber >= pageCount) {
			validatedPageNumber = pageCount > 0 ? pageCount - 1 : 0;
		}

		return validatedPageNumber;
	}

	private int pageCount;

	private int currentPage;

	@Inject
	public DashboardPagerPresenterWidget(final EventBus eventBus,
			final MyView view) {
		super(eventBus, view);
		getView().setUiHandlers(this);
	}

	public void actionChangePage(final int pageNumber) {
		if (pageNumber != currentPage && pageNumber >= 0
				&& pageNumber < pageCount) {
			AppUtils.getEventBus().fireEvent(
					new ChangeDashboardPageEvent(pageNumber));
			getView().redrawNavigateWidget(currentPage, pageNumber);
			currentPage = pageNumber;
		}
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getPageCount() {
		return pageCount;
	}

	@Override
	protected void onBind() {
		currentPage = 0;
	}

	public void setCurrentPage(final int currentPage) {
		this.currentPage = currentPage;
	}

	public void setPageCount(final int pageCount) {
		this.pageCount = pageCount;
		// it is necessary to validate currentPage in case if it is greater than
		// new pageCount
		this.currentPage = validatePageNumber(currentPage, pageCount);
		getView().clear();
		if (pageCount > 1) {
			getView().drawNavigateWidget(pageCount);
		}
	}
}
