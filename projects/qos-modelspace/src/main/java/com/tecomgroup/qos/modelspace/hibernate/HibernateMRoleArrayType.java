package com.tecomgroup.qos.modelspace.hibernate;

import java.util.*;

import java.io.Serializable;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import  com.tecomgroup.qos.domain.rbac.MRole;

/**
 * Created by kiselev.a on Thu Mar  3 10:38:44 2016.
 */

public class HibernateMRoleArrayType implements UserType {
	private final int[] arrayTypes = new int[] { Types.ARRAY };

	@Override
	public int[] sqlTypes() {
		return arrayTypes;
	}

	@Override
	public Class<MRole[]> returnedClass() {
		return MRole[].class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return x == null ? y == null : x.equals(y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x == null ? 0 : x.hashCode();
	}

	@Override
	public Object nullSafeGet(ResultSet rs, 
							  String[] names,
							  SessionImplementor session,
							  Object owner) throws HibernateException, 
												   SQLException {

		if (names != null && names.length > 0 && rs != null) {
			if(rs.getArray(names[0]) != null) {
				Long[] ids = HibernateArrayTypeUtils
					.castObjectToLongArray((Object[])rs.getArray(names[0]).getArray());
				
				return HibernateArrayTypeUtils
					.loadRolesById(session.connection(), ids);
			} else {
				return new ArrayList<MRole>();
			}
		}
		return null;
	}

	@Override
	public void nullSafeSet(PreparedStatement st, 
							Object value,
							int index,
							SessionImplementor session) throws HibernateException,
															   SQLException {
		if (value != null && st != null) {
			List<MRole> castObject = (List<MRole>) value;
			List<Long> castLongObject = new ArrayList<Long>();

			for(MRole item : castObject) {
				castLongObject.add(item.getId());
			}

			Array array = session
				.connection()
				.createArrayOf("bigint", 
							   castLongObject.toArray(new Long[castLongObject.size()]));
			st.setArray(index, array);

		} else {
			st.setNull(index, arrayTypes[0]);
		}
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value == null ? null : new ArrayList<MRole>((List<MRole>) value);
	}

	@Override
	public boolean isMutable() {
		return false;
	}
	
	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}
	
	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}
	
	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}
}
