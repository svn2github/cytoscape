package org.biyoenformatik.cytoscape_merge.util;

public class Criteria {
    public Double importance;
    public String attribute1, attribute2;
    public ComponentType type;

    public Criteria(ComponentType type, String attribute1, String attribute2, double importance) {
        this.type = type;
        this.attribute1 = attribute1;
        this.attribute2 = attribute2;
        this.importance = importance;
    }
}
