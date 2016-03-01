/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.amqp;

import com.tecomgroup.qos.communication.message.HeartbeatMessage;


/**
 * 
 * Listen heart beats from the given agent
 * 
 * @author abondin
 *
 */
public interface AgentHeartbeatListener {

	/**
	 * 
	 * @param message
	 */
	void onHeartbeat(HeartbeatMessage message);
	
}
