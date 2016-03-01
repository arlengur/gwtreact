package com.tecomgroup.qos.service.rbac;

import com.tecomgroup.qos.domain.rbac.Node;

/**
 * Created by uvarov.m on 31.01.2016.
 */
public interface Structure {
    public Node getNodeByPath(String path);
    public boolean isParent(Node parent, Node child);
    public boolean isParent(String parent, String child);
}
