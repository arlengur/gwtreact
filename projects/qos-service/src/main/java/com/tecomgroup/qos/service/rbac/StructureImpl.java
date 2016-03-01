package com.tecomgroup.qos.service.rbac;

import com.tecomgroup.qos.domain.rbac.Node;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by uvarov.m on 31.01.2016.
 */
public class StructureImpl implements Structure{
    private String fileName;
    private FileUtils utils;
    //The follow fields are unused now but will be for ldap feature
    private String name;
    private Node tree;
    private Map<String, Node> fast = new TreeMap<String, Node>();

    public StructureImpl(String name) {
        this.name = name;
    }

    public void setUtils(FileUtils utils) {
        this.utils = utils;
    }

    public void init() throws IOException {
        String json = utils.getResourceAsString(fileName);
        ObjectMapper mapper = new ObjectMapper();
        tree = mapper.readValue(json, Node.class);
        fast = tree.buildFastAccessMap();
    }

    public Node getNodeByPath(String path) {
        return fast.get(path);
    }

    public boolean isParent(Node parent, Node child) {
        return child.isParent(parent);
    }

    public boolean isParent(String parent, String child) {
        Node parentNode = fast.get(parent);
        if(parentNode == null) return false;

        Node childNode = fast.get(child);
        if(childNode == null) return false;

        if(parentNode.equals(childNode)) return false;

        return isParent(parentNode, childNode);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
