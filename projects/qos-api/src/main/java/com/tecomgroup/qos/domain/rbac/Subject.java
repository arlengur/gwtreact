package com.tecomgroup.qos.domain.rbac;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by uvarov.m on 31.01.2016.
 */
public class Subject {
    private String name;
    private List<Node> nodes = new ArrayList<Node>();

    public Subject() {}

    public List<Node> getNodes() {
        return nodes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Subject) {
            return this.name.equals(((Subject)o).getName());
        }
        return false;
    }
}
