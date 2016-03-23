package com.tecomgroup.qos.service.rbac;

import java.util.Map;
import java.util.List;
import java.sql.SQLException;

import com.tecomgroup.qos.domain.rbac.MRole;
import com.tecomgroup.qos.domain.rbac.MRoleInfoDTO;

/**
 * Created by kiselev.a on Mon Mar 14 18:12:46 2016.
 */

public interface RolesService {
	public List<MRoleInfoDTO> getRolesInfo();
	public List<MRole> getAllRoles();
	public MRole getRole(String name);
	public void saveRole(MRole src) throws SQLException;
	public void deleteRoles(String ... names) throws SQLException;
}
