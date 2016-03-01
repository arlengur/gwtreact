package com.tecomgroup.qos.rest.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author stroganov.d
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public enum ParameterGroup {

    VIDEO("VIDEO", "Video signal"),
    AUDIO("AUDIO", "Audio signal"),
    RF("RF", "RF signal"),
    IP("IP", "IP Statistics"),
    TS("TS", "TS data"),
    CC("CC", "Captions"),
    DATA("DATA", "Data signal"),
    EPG("EPG", "EPG data");

    private ParameterGroup(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public String name;
    public String displayName;
}
