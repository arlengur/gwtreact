/*
 * Copyright (C) 2015 Tecomgroup.
 * All Rights Reserved.
 */


package com.tecomgroup.qos.modelspace.jdbc.dao.probestatus;

import org.hsqldb.types.Types;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.Map;

public class JdbcProbeEventDaoBase {

    protected JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall getNextVal;

    public JdbcProbeEventDaoBase() {
    }

    public JdbcProbeEventDaoBase(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        initStoredProcedure();
    }

    public void initStoredProcedure() {
        this.getNextVal = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("recording_scheduler_seq_nextval")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlOutParameter("out_id", Types.BIGINT));
    }

    public Long getNextSeqId() {
        Map out = this.getNextVal.execute();
        return  (Long) out.get("out_id");
    }
}
