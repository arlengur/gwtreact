package com.tecomgroup.qos.domain.probestatus;

import com.tecomgroup.qos.util.SimpleUtils;

import javax.persistence.Transient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by uvarov.m on 03.03.2016.
 */
public class MPropertyMap {

    @Transient
    private Map<String, MEventProperty> propertyMap = new HashMap<String, MEventProperty>();

    public MPropertyMap(){}

    public MPropertyMap(List<MEventProperty> list) {
        if(SimpleUtils.isNotNullAndNotEmpty(list)) {
            for (MEventProperty p : list){
                propertyMap.put(p.getKey(), p);
            }
        }
    }

    public Map<String, MEventProperty> getPropertyMap() {
        return propertyMap;
    }

    public MEventProperty getProperty(String key) {
        return getPropertyMap().get(key);
    }

    public String getPropertyValue(String key) {
        MEventProperty result = getProperty(key);
        if (result != null) {
            return getProperty(key).getValue();
        }
        return null;
    }
}
