/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.response;

import com.tecomgroup.qos.communication.message.QoSMessage;

/**
 * 
 * Класс для JSON описания ответа на сообщение-запрос
 * 
 * @author abondin
 * 
 */
public class RequestResponse extends QoSMessage {

	/**
	 * Статус обработки запроса
	 * 
	 * @author abondin
	 * 
	 */
	public static enum Status {
		OK, ERROR;
	}

	private Status status;

	private String reason;

	/**
	 * 
	 */
	public RequestResponse() {
		super();
		status = Status.OK;
	}

	public RequestResponse(final Throwable ex) {
		status = Status.ERROR;
		reason = ex.toString();
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param reason
	 *            the reason to set
	 */
	public void setReason(final String reason) {
		this.reason = reason;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(final Status status) {
		this.status = status;
	}
}
