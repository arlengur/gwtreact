/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.tools.QoSTool;

/**
 * @author abondin
 * 
 */
@Component
public class HSQLManager implements QoSTool {

	@Value("${hsql.host}")
	private String host;
	@Value("${hsql.port}")
	private String port;
	@Value("${hsql.dbname}")
	private String dbname;
	@Value("${hsql.username}")
	private String user;
	@Value("${hsql.password}")
	private String password;

	@Override
	public void execute() {
		org.hsqldb.util.DatabaseManagerSwing.main(new String[]{"--url",
				"jdbc:hsqldb:hsql://" + host + ":" + port + "/" + dbname,
				"--user", user, "--password", password});
	}

	@Override
	public String getDescription() {
		return "Start HSQLDB manager to the DB on localhost. See HSQLServer"
				+ "\nSupported VM arguments:"
				+ "\n\thsql.dbname - default qosdb"
				+ "\n\thsql.host - default localhost"
				+ "\n\thsql.port - default 9001"
				+ "\n\thsql.username - default qos"
				+ "\n\thsql.password - default qos";
	}
}
