package com.tecomgroup.qos.domain.rbac;

import com.tecomgroup.qos.util.SimpleUtils;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Node {
    private static String NODES_DELIMETER = "->";

    private String name;
    private String fullPath;
    private Set<String> ldap;
    private Set<Node> branches ;

    private void iterate(Node node, String prefix, Map<String, Node> fast) {
        node.setFullPath(assembleFullPath(prefix, node.getName()));
        fast.put(node.getFullPath(), node);

        if(!SimpleUtils.isNotNullAndNotEmpty(node.getBranches())) return;

        for(Node child: node.getBranches()) {
            iterate(child, node.getFullPath(), fast );
        }
    }

    public Map<String, Node> buildFastAccessMap() {
        Map<String, Node> fast = new TreeMap<>();
        iterate(this, "", fast);
        return fast;
    }

    private String assembleFullPath(String prefix, String name) {
        if(prefix == null || prefix.isEmpty()) {
            return name;
        } else {
            StringBuffer result = new StringBuffer(prefix).append(NODES_DELIMETER).append(name);
            return result.toString();
        }
    }

    public boolean isParent(Node parent) {
        return this.fullPath.contains(parent.getFullPath());
    }

    public static String preparePath(String path) {
        return path.replaceAll("(\\s*" + Node.NODES_DELIMETER +"\\s*)", Node.NODES_DELIMETER);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getLdap() {
        return ldap;
    }

    public void setLdap(Set<String> ldap) {
        this.ldap = ldap;
    }

    public Set<Node> getBranches() {
        return branches;
    }

    public void setBranches(Set<Node> branches) {
        this.branches = branches;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Node) {
            return this.fullPath.equals(((Node)o).getFullPath());
        }
        return false;
    }
}