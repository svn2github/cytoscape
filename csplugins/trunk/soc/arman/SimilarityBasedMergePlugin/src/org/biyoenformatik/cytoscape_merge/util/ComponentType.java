package org.biyoenformatik.cytoscape_merge.util;

import cytoscape.data.CyAttributes;
import cytoscape.Cytoscape;

public enum ComponentType {
    NODE("Node", Cytoscape.getNodeAttributes()),
    EDGE("Edge", Cytoscape.getEdgeAttributes());

    private String name;
    private CyAttributes attributes;

    ComponentType(String s, CyAttributes attr) {
        this.name = s;
        this.attributes = attr;
    }

    public String toString() {
        return name;
    }

    public CyAttributes getAttributes() {
        return attributes;
    }
}
