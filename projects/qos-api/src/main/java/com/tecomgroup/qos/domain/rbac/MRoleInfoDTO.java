package com.tecomgroup.qos.domain.rbac;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiselev.a on Thu Mar 17 12:40:13 2016.
 */

public class MRoleInfoDTO extends MRoleDTO {
	public Long id;
	public Integer number_of_users;

	public static MRoleInfoDTO fromEntity(MRole src) {
		MRoleInfoDTO dto = new MRoleInfoDTO();
		dto.id = src.getId();
		dto.name = src.getName();
		dto.number_of_users = -1;
		dto.comment = src.getComment();

		if(src.getSubjects() != null) {
			List<String> uisStr = new ArrayList<String>();

			for(UISubject uis : src.getSubjects()) {
				uisStr.add(uis.getName());
			}
			dto.subjects = uisStr.toArray(new String[uisStr.size()]);
		}
		return dto;
	}
}
