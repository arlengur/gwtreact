/*
 * Copyright (C) 2015 Tecomgroup.
 * All Rights Reserved.
 */


package com.tecomgroup.qos.modelspace.jdbc.dao.recording;

import org.hsqldb.types.Types;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.Map;

/**
 * Created by uvarov.m on 07.11.2015.
 */
public class JdbcRecordingSchedulerDaoBase {

    protected JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall getNextVal;

    public JdbcRecordingSchedulerDaoBase() {
    }

    public JdbcRecordingSchedulerDaoBase(JdbcTemplate jdbcTemplate) {
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
