package com.tecomgroup.qos.modelspace.jdbc.dao;

import com.tecomgroup.qos.domain.rbac.UISubject;
import java.util.List;
import java.sql.*;
/**
 * Created by kiselev.a on Fri Feb 26 16:55:15 2016.
 */

public interface RbacUISubjectServiceDao { //non need allmost all
	public UISubject getSubject(String name);
	public List<UISubject> getSubjectById(Long ... id);
	public Long insertSubject(UISubject uis);
	public void updateSubject(UISubject uis);
	// TODO: 
	//public void addExtension(String name, String ... extSrc);
}
