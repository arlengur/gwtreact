package com.tecomgroup.qos.modelspace.hibernate;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import com.tecomgroup.qos.domain.rbac.UISubject;
import com.tecomgroup.qos.domain.rbac.MRole;

/**
 * Created by kiselev.a on Thu Mar  3 10:31:58 2016.
 */

public class HibernateArrayTypeUtils {
	public static String makePlaceHolders(Long ... ids) {
		String[] placeHoldersA = new String[ids.length];
		Arrays.fill(placeHoldersA, "?");
		String placeHoldersStr = Arrays.toString(placeHoldersA);
		return placeHoldersStr.substring(1, placeHoldersStr.length() - 1);
	}

	public static void setLongValues(PreparedStatement ps, Long ... ids) throws SQLException {
		for(int i = 0; i < ids.length; i++) {
			ps.setLong(i + 1, ids[i]);
		}
	}

	public static List<MRole> loadRolesById(Connection tmpCon, Long ... ids) throws SQLException {
		ArrayList<MRole> roles = new ArrayList<MRole>();
		ArrayList<Long[]> idsList = new ArrayList<Long[]>();
		String placeHoldersStr = makePlaceHolders(ids);
			
		try(PreparedStatement ps = tmpCon.prepareStatement("SELECT *  FROM mrole WHERE id IN (" 
														   + placeHoldersStr 
														   + ")")) {
				setLongValues(ps, ids);
				try(ResultSet rs = ps.executeQuery()) {
						while(rs.next()) {
							MRole tmpRole = new MRole(rs.getString("name"));
							tmpRole.setId(rs.getLong("id"));
							Long[] tmpSubjO = castObjectToLongArray((Object[])rs.getArray("subjects").getArray());
							idsList.add(tmpSubjO);
							roles.add(tmpRole);
						}
					}
			}
		
		for(int i = 0; i < roles.size(); i++) {
			roles.get(i).setSubjects(loadSubjectsById(tmpCon, idsList.get(i)));
		}

		return roles;
	}

	public static Set<UISubject> loadSubjectsById(Connection tmpCon, Long ... ids) throws SQLException {
		Set<UISubject> sbjs = new HashSet<UISubject>();
		String placeHoldersStr = makePlaceHolders(ids);
		
		try(PreparedStatement ps = tmpCon.prepareStatement("SELECT *  FROM  uisubject WHERE id IN (" 
														   + placeHoldersStr 
														   + ")")) {
				setLongValues(ps, ids);
				try(ResultSet rs = ps.executeQuery()) {
						while(rs.next()) {
							UISubject tmpUis = new UISubject(rs.getString("name"));
							tmpUis.setId(rs.getLong("id"));
							// TODO: need to handle array of extension
							sbjs.add(tmpUis);
						}
					}
			}
		
		return sbjs;//.toArray(new UISubject[sbjs.size()]);
	}

	public static Long[] castObjectToLongArray(Object[] src) {
		List<Long> ids = new ArrayList<Long>();

		for(Object o: src) {
			ids.add((Long)o);
		}
		return ids.toArray(new Long[ids.size()]);
	}
}
