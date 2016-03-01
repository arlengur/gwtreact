/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.message;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 
 * Любое комуникационное сообщение между компонентами QoS системы
 * 
 * @author abondin
 * 
 */
public abstract class QoSMessage {

	@JsonIgnore
	private byte[] correlationId;
	@JsonIgnore
	private String replyTo;
	@JsonIgnore
	private String messageId;

	private String originName;
	@JsonIgnore
	private String version;

	/**
	 * @return the correlationId
	 */
	public byte[] getCorrelationId() {
		return correlationId;
	}

	/**
	 * @return the messageId
	 */
	public String getMessageId() {
		return messageId;
	}

	/**
	 * Имя отправителя сообщения
	 * 
	 * @return the originName
	 */
	public String getOriginName() {
		return originName;
	}

	/**
	 * @return the replyTo
	 */
	public String getReplyTo() {
		return replyTo;
	}

	/**
	 * Версия формата сообщения
	 * 
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param correlationId
	 *            the correlationId to set
	 */
	public void setCorrelationId(final byte[] correlationId) {
		this.correlationId = correlationId;
	}

	/**
	 * @param messageId
	 *            the messageId to set
	 */
	public void setMessageId(final String messageId) {
		this.messageId = messageId;
	}

	/**
	 * @param originName
	 *            the originName to set
	 */
	public void setOriginName(final String originName) {
		this.originName = originName;
	}

	/**
	 * @param replyTo
	 *            the replyTo to set
	 */
	public void setReplyTo(final String replyTo) {
		this.replyTo = replyTo;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(final String version) {
		this.version = version;
	}
}
