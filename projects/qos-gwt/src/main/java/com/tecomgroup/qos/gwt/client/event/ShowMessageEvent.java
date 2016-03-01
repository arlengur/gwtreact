/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.ShowMessageEvent.ShowMessageEventHandler;

/**
 * Событие для показа информационного сообщения или сообщения об ошибке
 * 
 * @author abondin
 * 
 */
public class ShowMessageEvent extends GwtEvent<ShowMessageEventHandler> {

	/**
	 * Тип сообщения
	 * 
	 * @author abondin
	 * 
	 */
	public static enum MessageType {
		INFO, INFO_WITH_CONFIRM, ERROR
	}
	public static interface ShowMessageEventHandler extends EventHandler {
		void onMessageEvent(final ShowMessageEvent event);
	}
	public static ShowMessageEvent error(final String errorMessage) {
		return error(errorMessage, null);
	}

	public static ShowMessageEvent error(final String errorMessage,
			final Throwable ex) {
		final ShowMessageEvent event = new ShowMessageEvent();
		event.setMessage(errorMessage);
		event.setException(ex);
		event.setMessageType(MessageType.ERROR);
		return event;
	}

	public static ShowMessageEvent info(final String message) {
		final ShowMessageEvent event = new ShowMessageEvent();
		event.setMessage(message);
		event.setMessageType(MessageType.INFO);
		return event;
	}

	public static ShowMessageEvent infoWithConfirm(final String message) {
		final ShowMessageEvent event = new ShowMessageEvent();
		event.setMessage(message);
		event.setMessageType(MessageType.INFO_WITH_CONFIRM);
		return event;
	}
	private String message;
	private Throwable exception;
	private MessageType messageType;

	public final static Type<ShowMessageEventHandler> TYPE = new Type<ShowMessageEventHandler>();

	@Override
	protected void dispatch(final ShowMessageEventHandler handler) {
		handler.onMessageEvent(this);
	}

	@Override
	public GwtEvent.Type<ShowMessageEventHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * @return the exception
	 */
	public Throwable getException() {
		return exception;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the messageType
	 */
	public MessageType getMessageType() {
		return messageType;
	}

	/**
	 * @param exception
	 *            the exception to set
	 */
	public void setException(final Throwable exception) {
		this.exception = exception;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(final String message) {
		this.message = message;
	}

	/**
	 * @param messageType
	 *            the messageType to set
	 */
	public void setMessageType(final MessageType messageType) {
		this.messageType = messageType;
	}

}
