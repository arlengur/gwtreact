/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools.impl;

import org.hsqldb.server.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.tools.QoSTool;

/**
 * @author abondin
 * 
 */
@Component
public class HSQLServer implements QoSTool {

	@Autowired
	private Server hsqlServer;

	@Override
	public void execute() {
		hsqlServer.start();
	}

	@Override
	public String getDescription() {
		return "Start HSQLDB server on localhost" + "\nSupported VM arguments:"
				+ "\n\thsql.dbhome - default ./target/qosdb"
				+ "\n\thsql.dbname - default qosdb"
				+ "\n\thsql.port - default 9001"
				+ "\n\thsql.username - default qos"
				+ "\n\thsql.password - default qos";
	}
}
