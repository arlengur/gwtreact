package com.tecomgroup.qos.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by uvarov.m on 06.07.2015.
 */
public class AlertUpdateDTO {

    private BigDecimal id;
    private String comment;
    private Date datetime;
    private String field;
    private Object newvalue;
    private Object oldvalue;
    private MAlertType.UpdateType updatetype;
    private String user_name;
    private BigDecimal alert_id;


    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getNewvalue() {
        return newvalue;
    }

    public void setNewvalue(Object newvalue) {
        this.newvalue = newvalue;
    }

    public Object getOldvalue() {
        return oldvalue;
    }

    public void setOldvalue(Object oldvalue) {
        this.oldvalue = oldvalue;
    }

    public MAlertType.UpdateType getUpdatetype() {
        return updatetype;
    }

    public void setUpdatetype(MAlertType.UpdateType updatetype) {
        this.updatetype = updatetype;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public BigDecimal getAlert_id() {
        return alert_id;
    }

    public void setAlert_id(BigDecimal alert_id) {
        this.alert_id = alert_id;
    }
}
