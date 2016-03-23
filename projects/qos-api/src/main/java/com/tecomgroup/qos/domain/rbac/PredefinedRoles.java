package com.tecomgroup.qos.domain.rbac;

/**
 * Created by kiselev.a on Sat Feb 20 15:56:32 2016.
 */

public class PredefinedRoles {
	public static MRole ROLE_SUPER_ADMIN;
	//all other roles shouldnt be used anywhere except tests or db migration
	public static MRole ROLE_ADMIN;
	public static MRole ROLE_USER;
	public static MRole ROLE_CONFIGURATOR;

	static {
		ROLE_SUPER_ADMIN = new MRole(123456L,
									 "SuperAdmin", 
									 PermissionScope.asArray());

		ROLE_ADMIN = new MRole("Admin",
							   PermissionScope.MAIN,
							   PermissionScope.USER_MANAGER_ROLES);
		ROLE_USER = new MRole("User",
							  PermissionScope.CHANNEL_VIEW,
							  PermissionScope.MAIN,
							  PermissionScope.MAP,
							  PermissionScope.ALERTS,
							  PermissionScope.CHARTS,
							  PermissionScope.RECORDED_VIDEO,
							  PermissionScope.LIVE_VIDEO,
							  PermissionScope.REPORTS);
		ROLE_CONFIGURATOR = new MRole("Configurator",
									  PermissionScope.CHANNEL_VIEW,
									  PermissionScope.MAIN,
									  PermissionScope.PROBE_CONFIG,
									  PermissionScope.MAP,
									  PermissionScope.ALERTS,
									  PermissionScope.CHARTS,
									  PermissionScope.RECORDED_VIDEO,
									  PermissionScope.LIVE_VIDEO,
									  PermissionScope.REPORTS,
									  PermissionScope.POLICIES,
									  PermissionScope.POLICIES_ADVANCED,
									  PermissionScope.RECORDING_SCHEDULE);
	}
	
	public static MRole[] asArray() {
		return new MRole[]{ROLE_SUPER_ADMIN,
						   ROLE_ADMIN,
						   ROLE_USER,
						   ROLE_CONFIGURATOR};
	}

	public static MRole findByNameIgnoreCase(String name) {
		for(MRole role : asArray()) {
			if(role.getName().equalsIgnoreCase(name)) {
				return role;
			}
		}
		return null;
	}
}
