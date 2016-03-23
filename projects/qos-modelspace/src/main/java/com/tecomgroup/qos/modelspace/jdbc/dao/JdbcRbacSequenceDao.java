package com.tecomgroup.qos.modelspace.jdbc.dao;

import org.hsqldb.types.Types;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.Map;
import java.sql.*;

/**
 * Created by kiselev.a on Fri Mar 11 18:23:35 2016.
 */

public class JdbcRbacSequenceDao {
	protected JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall getNextVal;

    public void initStoredProcedure(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
        this.getNextVal = new SimpleJdbcCall(jdbcTemplate)
			.withProcedureName("rbac_seq_nextval")
			.withoutProcedureColumnMetaDataAccess()
			.declareParameters(new SqlOutParameter("out_id", Types.BIGINT));
    }

    public Long getNextSeqId() {
        Map out = this.getNextVal.execute();
        return  (Long) out.get("out_id");
    }
}
