package org.cytoscape.data.reader.kgml;

public enum KEGGEntryType {
	ORTHOLOG("ortholog"), ENZYME("enzyme"), GENE("gene"), GROUP("group"), 
	COMPOUND("compound"), MAP("map");

	private String tag;
	
	private KEGGEntryType(final String tag) {
		this.tag = tag;
	}
	
	public String getTag() {
		return this.tag;
	}
}
