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
	
	public static KEGGEntryType getType(final String tag) {
		for(KEGGEntryType entry: KEGGEntryType.values()) {
			if(entry.getTag().equals(tag))
				return entry;
		}
		
		return null;
	}
}
