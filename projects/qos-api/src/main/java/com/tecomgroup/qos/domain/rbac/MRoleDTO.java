package com.tecomgroup.qos.domain.rbac;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kiselev.a on Thu Mar 17 12:40:13 2016.
 */

public class MRoleDTO implements Serializable {
	public String name;
	public String[] subjects;
	public String comment = "";

	public static MRole toEntity(MRoleDTO src) {
		MRole tmp = new MRole(src.name);
		tmp.setComment(src.comment);
		Set<UISubject> sbjs = new HashSet<UISubject>();
		for(String item : src.subjects) {
			UISubject founded = PermissionScope.findByName(item);
			if(founded != null) {
				sbjs.add(founded);
			}
		}
		tmp.setSubjects(sbjs);
		return tmp;
	}
}
