package com.tecomgroup.qos.domain.rbac;

/**
 * Created by uvarov.m on 31.01.2016.
 */
public class User extends Subject{
    private boolean isLdap = false;
    private String ldap;

    public boolean isLdap() {
        return isLdap;
    }

    public void setLdap(boolean isLdap) {
        this.isLdap = isLdap;
    }

    public String getLdap() {
        return ldap;
    }

    public void setLdap(String ldap) {
        this.ldap = ldap;
    }
}
