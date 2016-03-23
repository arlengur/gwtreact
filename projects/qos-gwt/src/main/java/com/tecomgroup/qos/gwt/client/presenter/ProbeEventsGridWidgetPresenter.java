/*
 * Copyright (C) 2016 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.data.shared.TreeStore;
import com.tecomgroup.qos.domain.probestatus.MProbeEvent;
import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.AgentActionStatusEvent;
import com.tecomgroup.qos.event.QoSEventListener;
import com.tecomgroup.qos.gwt.client.model.TreeGridRow;
import com.tecomgroup.qos.gwt.client.model.events.EventGroupRow;
import com.tecomgroup.qos.gwt.client.model.events.EventRow;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractLocalDataTreeGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.gwt.shared.event.QoSEventService;
import com.tecomgroup.qos.gwt.shared.event.filter.AgentActionStatusEventFilter;
import com.tecomgroup.qos.service.ProbeEventServiceAsync;
import com.tecomgroup.qos.util.SimpleUtils;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ProbeEventsGridWidgetPresenter
		extends
			AbstractLocalDataTreeGridWidgetPresenter<TreeGridRow, ProbeEventsGridWidgetPresenter.MyView> implements QoSEventListener {

	public interface MyView
			extends
				AbstractLocalDataTreeGridWidgetPresenter.MyView<TreeGridRow, ProbeEventsGridWidgetPresenter> {

		TreeStore<TreeGridRow> getStore();

		void refreshGridView();

		void updateEvents(List<? extends MProbeEvent> events);
	}

	public static Logger LOGGER = Logger.getLogger(ProbeEventsGridWidgetPresenter.class
			.getName());

	private Map<String, String> eventHrefMap;

	private Map<MProbeEvent.EventType, String> eventLabels;

	private List<MProbeEvent.EventType> eventTypes;

	private ProbeEventServiceAsync probeEventService;

	private final QoSEventService eventService;

	@Inject
	public ProbeEventsGridWidgetPresenter(final EventBus eventBus,
										  final MyView view,
										  final ProbeEventServiceAsync probeEventService,
										  final QoSEventService eventService) {
		super(eventBus, view);
		this.probeEventService = probeEventService;
		this.eventService = eventService;
		getView().setUiHandlers(this);
	}

	private void loadEvents() {
		final String currentUserLogin = AppUtils.getCurrentUser().getUser().getLogin();

		for(final MProbeEvent.EventType type: eventTypes) {
		probeEventService
					.getLastEventsByUserAndType(
							currentUserLogin,
							type.getEventClassName(),
							new AutoNotifyingAsyncLogoutOnFailureCallback<List<MProbeEvent>>() {
								@Override
								protected void success(List<MProbeEvent> result) {
									final TreeStore<TreeGridRow> store = getView()
											.getStore();

									store.add(new EventGroupRow(
											eventLabels.get(type), type));
									getView().updateEvents(result);
									if(SimpleUtils.isNotNullAndNotEmpty(result)) {
										getView().refreshGridView();
									}
								}
							});
		}
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		getView().getStore().clear();
		loadEvents();
		subscribe(this);
	}

	private void subscribe(final ProbeEventsGridWidgetPresenter listener) {
		eventService.subscribe(
			AgentActionStatusEvent.class,
			listener,
			new AgentActionStatusEventFilter(
					AppUtils.getCurrentUser().getUser().getLogin()));
	}

	@Override
	public void onServerEvent(AbstractEvent event) {
		if(event instanceof AgentActionStatusEvent) {
			MProbeEvent probeEvent = ((AgentActionStatusEvent) event).getEvent();
			updateEventInGrid(probeEvent);
		}
	}

	private void updateEventInGrid(MProbeEvent probeEvent) {
		final TreeStore<TreeGridRow> store = getView()
				.getStore();

		List<TreeGridRow> rows = store.getAll();
		for(TreeGridRow row: rows) {
			if(row instanceof EventRow) {

				MProbeEvent rowProbeEvent = ((EventRow) row).getEvent();
				if (probeEvent.getKey().equals(rowProbeEvent.getKey())) {
					store.remove(row);
					EventGroupRow group = (EventGroupRow) store.findModelWithKey(probeEvent.getClass().getName());

					if (group != null) {
						store.add(group, new EventRow(probeEvent));
					}
				}
			}
		}
		getView().refreshGridView();

	}

	@Override
	public void reload(final boolean force) {
		// Do nothing
	}

	public void removeEvents(final TreeGridRow row) {
		if (row instanceof EventRow) {
			MProbeEvent event = ((EventRow) row).getEvent();
			probeEventService.removeProbeEventByKey(event.getKey(),
					new AutoNotifyingAsyncLogoutOnFailureCallback<Void>(
							messages.probeEventRemoveFail(),
							true) {

						@Override
						protected void success(final Void result) {
							getView().getStore().remove(row);
							AppUtils.showInfoMessage(messages
									.probeEventRemoveSuccess());
						}
					});
		}
	}

	public void setEventHrefMap(Map<String, String> eventHrefMap) {
		this.eventHrefMap = eventHrefMap;
	}

	public void setEventLabels(Map<MProbeEvent.EventType, String> eventLabels) {
		this.eventLabels = eventLabels;
	}

	public void setEventTypes(List<MProbeEvent.EventType> eventTypes) {
		this.eventTypes = eventTypes;
	}

	public String getEventHref(final String templateClass) {
		return eventHrefMap.get(templateClass);
	}
}
