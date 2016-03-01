/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.snmp;

/**
 * @author novohatskiy.r
 * 
 */
public interface SnmpConstants {

	String VACM_FULL_NOTIFY_VIEW = "fullNotifyView";
	String VACM_FULL_READ_VIEW = "fullReadView";
	String VACM_DISALLOWED_WRITE_VIEW = "disallowedWriteView";
	String VACM_OID_SUBTREE = "1.3";

	String ACCESS_GROUP_NAME = "group.name";
	String ACCESS_CONTEXT_PREFIX = "context.prefix";
	String ACCESS_SECURITY_MODEL = "security.model";
	String ACCESS_SECURITY_LEVEL = "security.level";

	String GROUP_NAME = "name";
	String GROUP_SECURITY_MODEL = "security.model";
	String GROUP_SECURITY_NAME = "security.name";

	String USER_SECURITY_NAME = "security.name";
	String USER_SECURITY_LEVEL = "security.level";
	String USER_AUTH_PROTOCOL = "auth.protocol";
	String USER_AUTH_PASS = "auth.pass";
	String USER_PRIV_PROTOCOL = "priv.protocol";
	String USER_PRIV_PASS = "priv.pass";

	String TRAP_RECEIVER_NAME = "name";
	String TRAP_RECEIVER_TDOMAIN = "tdomain";
	String TRAP_RECEIVER_TADDRESS = "taddress";
	String TRAP_RECEIVER_TIMEOUT = "timeout";
	String TRAP_RECEIVER_RETRY_COUNT = "retry.count";
	String TRAP_RECEIVER_TAG = "tag";
	String TRAP_RECEIVER_PARAMETERS = "parameters";

	String TRAP_PARAMETERS_NAME = "name";
	String TRAP_PARAMETERS_MPMODEL = "mpmodel";
	String TRAP_PARAMETERS_SECURITY_MODEL = "security.model";
	String TRAP_PARAMETERS_SECURITY_NAME = "security.name";
	String TRAP_PARAMETERS_SECURITY_LEVEL = "security.level";

	String TRAP = "trap";
	String INFORM = "inform";

}
