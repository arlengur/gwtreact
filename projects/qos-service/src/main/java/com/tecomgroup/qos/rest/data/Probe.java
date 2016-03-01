package com.tecomgroup.qos.rest.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author stroganov.d
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Probe extends ProbeBase {
    public final List<QoSTask> tasks=new ArrayList<>();
}
