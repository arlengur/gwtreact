package com.tecomgroup.qos.service.rbac;

import com.tecomgroup.qos.modelspace.jdbc.dao.MRoleServiceDao;
import com.tecomgroup.qos.domain.rbac.MRole;
import com.tecomgroup.qos.domain.rbac.PredefinedRoles;
import com.tecomgroup.qos.domain.rbac.MRoleInfoDTO;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.service.UserManagerService;

import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by kiselev.a on Tue Mar 15 10:13:02 2016.
 */

public class RolesServiceImpl implements RolesService {
	@Autowired
	private MRoleServiceDao roleDao;

	@Autowired
	private UserManagerService ums;
	
	private int countUsersHasRole(List<MUser> users, MRole role) {
		int counter = 0;
		for(MUser usr : users) {
			if(usr.hasRole(role)) {
				counter++;
			}
		}		

		return counter;
	}

	public List<MRoleInfoDTO> getRolesInfo() {
		List<MRoleInfoDTO> result = new ArrayList<MRoleInfoDTO>();
		List<MUser> users = ums.getAllUsersNotFiltered();
		
		for(MRole role : roleDao.getRolesList()) {
			MRoleInfoDTO mrid = MRoleInfoDTO.fromEntity(role);
			mrid.number_of_users = countUsersHasRole(users, role);
			result.add(mrid);
		}
		return result;
	}

	public List<MRole> getAllRoles() {
		List<MRole> allRoles =  roleDao.getRolesList();
		allRoles.remove(PredefinedRoles.ROLE_SUPER_ADMIN);
		return allRoles;
	}

	public MRole getRole(String name) {
		return roleDao.getRole(name);
	}

	public void saveRole(MRole src) throws SQLException {
		roleDao.updateRole(src);
	}

	public void deleteRoles(String ... names) throws SQLException {
		roleDao.deleteRoles(names);
	}
}
