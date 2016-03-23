package com.tecomgroup.qos.modelspace.jdbc.dao;

import com.tecomgroup.qos.domain.rbac.MRole;
import java.util.List;

/**
 * Created by kiselev.a on Mon Feb 29 10:49:27 2016.
 */

import java.sql.SQLException;

public interface MRoleServiceDao {
	public MRole getRole(String name);
	public List<MRole> getRolesList();
	public Long insertRole(MRole role) throws SQLException;
	public void updateRole(MRole role) throws SQLException;
	public void deleteRoles(String ... names) throws SQLException;
}
