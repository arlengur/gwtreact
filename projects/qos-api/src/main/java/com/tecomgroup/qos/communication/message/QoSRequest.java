/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.message;

import com.tecomgroup.qos.communication.response.RequestResponse;

/**
 * 
 * Сообщение запрос. Т.е. любое сообщение на которое требуется ответ
 * 
 * @author abondin
 * 
 */
public abstract class QoSRequest extends QoSMessage {
	/**
	 * 
	 * @return
	 */
	public abstract RequestResponse responseError(Throwable throwable);

	/**
	 * 
	 * @return
	 */
	public abstract RequestResponse responseOk();
}
