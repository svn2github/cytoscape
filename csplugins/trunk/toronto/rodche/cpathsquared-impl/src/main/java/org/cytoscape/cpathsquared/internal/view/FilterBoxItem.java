package org.cytoscape.cpathsquared.internal.view;


public class FilterBoxItem implements Comparable<FilterBoxItem> {
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


    //in order to order by name
	@Override
	public int compareTo(FilterBoxItem o) {
		return this.name.compareTo(o.getName());
	}
}
