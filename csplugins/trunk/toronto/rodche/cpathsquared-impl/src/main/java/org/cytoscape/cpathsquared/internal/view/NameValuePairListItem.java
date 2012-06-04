package org.cytoscape.cpathsquared.internal.view;


final class NameValuePairListItem implements Comparable<NameValuePairListItem> {
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
        return (name != null) ? name : value;
    }

	@Override
	public int compareTo(NameValuePairListItem o) {
		return (name != null) 
			? this.name.compareTo(o.getName())
			: this.value.compareTo(o.getValue());
	}
}
