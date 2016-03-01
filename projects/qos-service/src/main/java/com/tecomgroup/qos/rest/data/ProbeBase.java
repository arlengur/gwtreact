package com.tecomgroup.qos.rest.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by uvarov.m on 26.10.2015.
 */

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class ProbeBase implements Serializable{
    public Long id;
    public String name;
    public String entityKey;
    public String country;
    public String state;
    public String city;
    public String street;

    public static Comparator<ProbeBase> ProbeComparator =  new Comparator<ProbeBase>() {
        @Override
        public int compare(ProbeBase o1, ProbeBase o2) {
            return o1.name.compareTo(o2.name);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProbeBase probeBase = (ProbeBase) o;

        if (id != null ? !id.equals(probeBase.id) : probeBase.id != null) return false;
        if (name != null ? !name.equals(probeBase.name) : probeBase.name != null) return false;
        if (entityKey != null ? !entityKey.equals(probeBase.entityKey) : probeBase.entityKey != null) return false;
        if (country != null ? !country.equals(probeBase.country) : probeBase.country != null) return false;
        if (state != null ? !state.equals(probeBase.state) : probeBase.state != null) return false;
        if (city != null ? !city.equals(probeBase.city) : probeBase.city != null) return false;
        return !(street != null ? !street.equals(probeBase.street) : probeBase.street != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (entityKey != null ? entityKey.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (street != null ? street.hashCode() : 0);
        return result;
    }

    public static ProbeBase populateProbeInfo(Map<String, String> agentProperty) {
        ProbeBase probe = new ProbeBase();
        probe.name = agentProperty.get("name");
        probe.entityKey = agentProperty.get("key");
        return probe;
    }
}
