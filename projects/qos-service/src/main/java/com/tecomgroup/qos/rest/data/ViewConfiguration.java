package com.tecomgroup.qos.rest.data;

import com.tecomgroup.qos.TimeInterval;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author stroganov.d
 */
@XmlType
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ViewConfiguration {
    public String username;
}
