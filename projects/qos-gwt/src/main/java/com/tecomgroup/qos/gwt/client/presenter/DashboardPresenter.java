/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.ProxyEvent;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.domain.MDashboard;
import com.tecomgroup.qos.exception.DuplicateException;
import com.tecomgroup.qos.exception.LimitExceededException;
import com.tecomgroup.qos.gwt.client.DashboardWidgetFactory;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.RequestParams;
import com.tecomgroup.qos.gwt.client.event.BeforeLogoutEvent;
import com.tecomgroup.qos.gwt.client.event.BeforeLogoutEvent.BeforeLogoutEventHandler;
import com.tecomgroup.qos.gwt.client.event.ClearContentOnMainPageEvent;
import com.tecomgroup.qos.gwt.client.event.RevealContentInMainPageEvent;
import com.tecomgroup.qos.gwt.client.event.dashboard.*;
import com.tecomgroup.qos.gwt.client.event.dashboard.AddWidgetToDashboardEvent.AddWidgetToDashboardHandler;
import com.tecomgroup.qos.gwt.client.event.dashboard.ChangeDashboardPageEvent.ChangeDashboardPageEventHandler;
import com.tecomgroup.qos.gwt.client.event.dashboard.RemoveWidgetFromDashboardEvent.RemoveWidgetFromDashboardEventHandler;
import com.tecomgroup.qos.gwt.client.event.dashboard.SaveDashboardWidgetEvent.SaveDashboardWidgetEventHandler;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.DashboardPagerPresenterWidget;
import com.tecomgroup.qos.gwt.client.secutiry.CurrentUser;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.gwt.client.utils.ChartResultUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.service.UserServiceAsync;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ivlev.e
 *
 */
public class DashboardPresenter
		extends
			Presenter<DashboardPresenter.MyView, DashboardPresenter.MyProxy>
		implements
			UiHandlers,
			AddWidgetToDashboardHandler,
			RemoveWidgetFromDashboardEventHandler,
			ChangeDashboardPageEventHandler,
			SaveDashboardWidgetEventHandler,
			BeforeLogoutEventHandler{

	private static interface DashboardLoadAction {
		void onDashboardLoaded(MDashboard dashboard);
	}

	@ProxyCodeSplit
	@NameToken(QoSNameTokens.dashboard)
	public static interface MyProxy extends ProxyPlace<DashboardPresenter> {

	}

	public static interface MyView
			extends
				View,
				HasUiHandlers<DashboardPresenter> {

		/**
		 * Forces view to update
		 */
		void dashboardReloaded(MDashboard dashboard);

		void destroyWidgetsCompletely();

		void destroyWidgetsWithSavingState();

		/**
		 * Get count of page depending on {@link DashboardWidget} count and its
		 * size
		 */
		int getPageCount();

		void onRemoveWidgetFromDashboard(String widgetKey);

		void showPage(int pageNumber);
	}

	private final UserServiceAsync userService;

	private final CurrentUser user;

	private final QoSMessages messages;

	private final DashboardWidgetFactory widgetFactory;

	private final DashboardPagerPresenterWidget pager;

	private final DialogFactory dialogFactory;

	@Inject
	public DashboardPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final UserServiceAsync userService,
			final CurrentUser user, final QoSMessages messages,
			final DashboardWidgetFactory widgetFactory,
			final DashboardPagerPresenterWidget pager, final DialogFactory dialogFactory) {
		super(eventBus, view, proxy);
		this.userService = userService;
		this.user = user;
		this.messages = messages;
		this.widgetFactory = widgetFactory;
		this.pager = pager;
		this.dialogFactory = dialogFactory;
		getView().setUiHandlers(this);
		eventBus.addHandler(RemoveWidgetFromDashboardEvent.TYPE, this);
		eventBus.addHandler(ChangeDashboardPageEvent.TYPE, this);
		eventBus.addHandler(SaveDashboardWidgetEvent.TYPE, this);
		eventBus.addHandler(BeforeLogoutEvent.TYPE, this);
	}

	public DashboardWidgetFactory getWidgetFactory() {
		return widgetFactory;
	}

	private void loadDashboard(final DashboardLoadAction loaderAction) {
		userService.getDashboard(
				user.getUser().getLogin(),
				new AutoNotifyingAsyncLogoutOnFailureCallback<MDashboard>(messages
						.loadDashbordFail(), true) {

					@Override
					protected void success(final MDashboard result) {
						final MDashboard dashboard;
						if (result == null) {
							dashboard = new MDashboard();
							dashboard.setUsername(user.getUser().getLogin());
						} else {
							dashboard = result;
						}
						if (loaderAction != null) {
							loaderAction.onDashboardLoaded(dashboard);
						}
					}
				});
	}

	@Override
	@ProxyEvent
	public void onAddWidgetToDashboard(final AddWidgetToDashboardEvent event) {
        final DashboardWidget widget = event.getWidget();
        userService.addWidgetToDashboard(
                widget,
                new AsyncCallback<Void>() {
            @Override
            public void onFailure(final Throwable caught) {
                if(caught instanceof LimitExceededException) {
                    final LimitExceededException e = (LimitExceededException) caught;
                    AppUtils.showErrorMessage(
                            messages.dashboardWidgetLimitExceed(e.getLimitValue()),
                            caught);
                } else if(caught instanceof DuplicateException) {
                    AppUtils.showInfoWithConfirmMessage(messages.widgetAlreadyExists());
                } else {
                    AppUtils.showErrorMessage(messages.dashboardUpdateFail());
                }
            }

            @Override
            public void onSuccess(final Void result) {
                AppUtils.getEventBus().fireEvent(
                        new DashboardWidgetAddedEvent(widget));
                AppUtils.showInfoMessage(
                        messages.widgetAddedToDashboard());
            }
        });
	}

	@Override
	public void onBeforeLogout(final BeforeLogoutEvent event) {
		getView().destroyWidgetsCompletely();
		removePagerFromMainPage();
	}

    @Override
    public void onBind() {
        super.onBind();
        ChartResultUtils.initGeneralChartParameters(messages);
    }

	@Override
	public void onChangePage(final ChangeDashboardPageEvent event) {
		showPage(event.getPageNumber());
	}

	@Override
	protected void onHide() {
		super.onHide();
		getView().destroyWidgetsWithSavingState();
		removePagerFromMainPage();
	}

	@Override
	public void onRemoveWidgetFromDashboard(
			final RemoveWidgetFromDashboardEvent event) {
		final String widgetKey = event.getWidgetKey();

		dialogFactory.createConfirmationDialog(
				new ConfirmationDialog.ConfirmationHandler() {

					@Override
					public void onCancel() {

					}

					@Override
					public void onConfirm(
							final String comment) {
						loadDashboard(new DashboardLoadAction() {
							@Override
							public void onDashboardLoaded(final MDashboard dashboard) {
								final DashboardWidget removed = dashboard.removeWidget(widgetKey);
								if (removed != null) {
									userService.removeWidgetFromDashboard(
											widgetKey,
											new AutoNotifyingAsyncLogoutOnFailureCallback<Void>(
													"Unable to remove widget", true) {

												@Override
												protected void success(final Void result) {
													getView().onRemoveWidgetFromDashboard(
															widgetKey);
													updateView(dashboard);
													getEventBus().fireEvent(
															new DashboardWidgetRemovedEvent(
																	widgetKey));
												}
											});
								}
							}
						});
					}
				}, messages.deleteWidget(),
				messages.deleteWidgetConfirmation(),
				ConfirmationDialog.CommentMode.DISABLED).show();
	}

	@Override
	public void onSaveWidget(final SaveDashboardWidgetEvent event) {
		loadDashboard(new DashboardLoadAction() {
			@Override
			public void onDashboardLoaded(final MDashboard dashboard) {
				saveWidget(dashboard, event.getWidget());
			}
		});
	}

	@Override
	public void prepareFromRequest(final PlaceRequest request) {
		super.prepareFromRequest(request);
		final String page = request.getParameter(RequestParams.page, null);
		if (page != null) {
			try {
				final int parsedPageNumber = Integer.parseInt(page);
				if (parsedPageNumber >= 0) {
					pager.setCurrentPage(parsedPageNumber);
				}
			} catch (final Exception e) {
			}
		} else if (isVisible()) {
			pager.setCurrentPage(0);
			reloadDashboard();
		}
	}

	private void reloadDashboard() {
		loadDashboard(new DashboardLoadAction() {
			@Override
			public void onDashboardLoaded(final MDashboard dashboard) {
				updateView(dashboard);
			}
		});
	}

	private void removePagerFromMainPage() {
		getEventBus().fireEvent(
				new ClearContentOnMainPageEvent(
						MainPagePresenter.TYPE_SetBottomContent));
	}

	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetMainContent,
				this);
		reloadDashboard();
	}

	private void saveWidget(final MDashboard dashboard,
			final DashboardWidget widget) {
		if (dashboard.hasWidget(widget)) {
			dashboard.updateWidget(widget);
			userService.updateDashboard(
					dashboard,
					new AutoNotifyingAsyncLogoutOnFailureCallback<Void>(messages
							.dashboardUpdateFail(), true) {
						@Override
						protected void success(final Void result) {
							AppUtils.showInfoMessage(messages
									.widgetSuccessfullySaved());
						}
					});
		} else {
			AppUtils.showInfoWithConfirmMessage(messages.widgetDoesNotExist());
		}
	}

	private void showPage(final int pageNumber) {
		updateBrowserUrlWithPage(pageNumber);
		getView().showPage(pageNumber);
	}

	private void updateBrowserUrlWithPage(final Integer pageNumber) {
		final Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(RequestParams.page, pageNumber.toString());
		AppUtils.replaceHistoryUrl(QoSNameTokens.dashboard, urlParameters);
	}

	private void updatePager() {
		final int pageCount = getView().getPageCount();
		pager.setPageCount(pageCount);
		// don't show pager if there is only one page.
		if (pageCount > 1) {
			// we must send event only after pager is drawn.
			getEventBus().fireEvent(
					new RevealContentInMainPageEvent(
							MainPagePresenter.TYPE_SetBottomContent, pager));
		} else {
			// remove pager from main page if there is only one page.
			removePagerFromMainPage();
		}
	}

	private void updateView(final MDashboard dashboard) {
		getView().dashboardReloaded(dashboard);
		updatePager();
		showPage(pager.getCurrentPage());
	}
}