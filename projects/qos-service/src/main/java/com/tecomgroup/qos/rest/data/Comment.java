package com.tecomgroup.qos.rest.data;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * @author galin.a
 */
public class Comment {
    public long id;
    public String comment;
    public long datetime;
    public String updateType;
    public String userName;

    public Comment(Object [] objects) {
        this.setId(((BigInteger) objects[0]).longValue());
        this.setComment((String) objects[1]);
        this.setDatetime(((Timestamp) objects[2]).getTime());
        this.setUpdateType(((String) objects[3]));
        this.setUserName((String) objects[4]);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
