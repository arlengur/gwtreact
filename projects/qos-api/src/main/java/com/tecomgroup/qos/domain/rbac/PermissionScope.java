package com.tecomgroup.qos.domain.rbac;

import java.math.BigDecimal;

/**
 * Created by kiselev.a on Sat Feb 20 15:41:15 2016.
 */

public class PermissionScope { //subjects
	public static UISubject		MAIN			   = new UISubject(1L, "MAIN");
	public static UISubject		PROBE_CONFIG	   = new UISubject(2L, "PROBE_CONFIG");
	public static UISubject		MAP				   = new UISubject(3L, "MAP");
	public static UISubject		ALERTS			   = new UISubject(4L, "ALERTS");
	public static UISubject		CHARTS			   = new UISubject(5L, "CHARTS");
	public static UISubject		RECORDED_VIDEO	   = new UISubject(6L, "RECORDED_VIDEO");
	public static UISubject		LIVE_VIDEO		   = new UISubject(7L, "LIVE_VIDEO");
	public static UISubject		REPORTS			   = new UISubject(8L, "REPORTS");
	public static UISubject		POLICIES		   = new UISubject(9L, "POLICIES");
	public static UISubject		POLICIES_ADVANCED  = new UISubject(10L, "POLICIES_ADVANCED");
	public static UISubject		USER_MANAGER_ROLES = new UISubject(11L, "USER_MANAGER_ROLES");
	public static UISubject		RECORDING_SCHEDULE = new UISubject(12L, "RECORDING_SCHEDULE");
	public static UISubject		CHANNEL_VIEW	   = new UISubject(13L, "CHANNEL_VIEW");

	public static UISubject[] asArray() {
		return new UISubject[]{MAIN,
							   PROBE_CONFIG,
							   MAP,
							   ALERTS,
							   CHARTS,
							   RECORDED_VIDEO,
							   LIVE_VIDEO,
							   REPORTS,
							   POLICIES,
							   POLICIES_ADVANCED,
							   USER_MANAGER_ROLES,
							   RECORDING_SCHEDULE,
							   CHANNEL_VIEW};
	}

	public static UISubject findByName(String name) {
		for (UISubject uis : asArray()) {
			if(uis.getName().equals(name)) {
				return uis;
			}
		}
		return null;
	}
}
