package com.tecomgroup.qos.modelspace.hibernate;

import java.sql.Types;
import org.hibernate.dialect.HSQLDialect;

/**
 * Created by kiselev.a on Fri Mar  4 11:50:56 2016.
 */

public class HSQLArrayDialect extends HSQLDialect {
    public HSQLArrayDialect() {
		super();
		registerHibernateType(Types.ARRAY, "array");
		registerColumnType(Types.ARRAY, "array"); 
		// registerColumnType(Types.ARRAY, "varchar[$l]");
		
    }
}
