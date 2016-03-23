package com.tecomgroup.qos.domain.probestatus;

import com.google.gwt.user.client.rpc.IsSerializable;

import javax.persistence.*;

/**
 * Created by uvarov.m on 11.01.2016.
 */

@Entity
@Table(name = "mprobe_event_properties")
public class MEventProperty implements IsSerializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "id")
    private Long id;

    @Column(name = "propertylist_id")
    private Long parentId;

    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "value", nullable = false)
    private String value;

    @Override
    public boolean equals(Object object)
    {
        if (object != null && object instanceof MEventProperty)
        {
            MEventProperty o = (MEventProperty) object;
            return (this.getKey() == null && o.getKey() == null) || (this.getKey() != null && this.getKey().equals(o.getKey()) &&
                   (this.getValue() == null && o.getValue() == null) || (this.getValue() != null && this.getValue().equals(o.getValue())));
        }

        return false;
    }

    public static MEventProperty copy(MEventProperty from) {
        MEventProperty a = new MEventProperty();
        a.setId(from.getId());
        a.setKey(from.getKey());
        a.setValue(from.getValue());
        a.setParentId(from.getParentId());
        return a;
    }

    public MEventProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public MEventProperty() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
