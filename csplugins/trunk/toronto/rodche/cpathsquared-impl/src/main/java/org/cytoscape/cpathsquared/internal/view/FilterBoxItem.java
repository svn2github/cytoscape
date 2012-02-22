package org.cytoscape.cpathsquared.internal.view;


public class FilterBoxItem {
    private String name;
    private String value;


    public FilterBoxItem (String name, String value) {
        this.name = name;
        this.value = value;
    }


    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return name;
    }
}
