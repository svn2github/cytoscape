package org.cytoscape.cpathsquared.internal.view;


public class NameValuePairListItem implements Comparable<NameValuePairListItem> {
    private String name;
    private String value;


    public NameValuePairListItem (String name, String value) {
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
	public int compareTo(NameValuePairListItem o) {
		return this.name.compareTo(o.getName());
	}
}
