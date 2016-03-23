package com.tecomgroup.qos.modelspace.hibernate;

import java.sql.Types;
import org.hibernate.dialect.PostgreSQLDialect;

/**
 * Created by kiselev.a on Fri Mar  4 10:37:06 2016.
 */

public class PostgresArrayDialect extends PostgreSQLDialect {
	public PostgresArrayDialect() {
		super();
		registerHibernateType(Types.ARRAY, "array");
		registerColumnType(Types.ARRAY, "bigint[$l]"); 
		registerColumnType(Types.ARRAY, "string[$l]"); 
	}
}
