package org.cytoscape.data.reader.kgml;

public enum KEGGRelationType {
	EC_REL("ECrel"), PP_REL("PPrel"), GE_REL("GErel"), 
	PC_REL("PCrel"), MAPLINK("maplink");

	private final String tag;
	
	private KEGGRelationType(final String tag) {
		this.tag = tag;
	}
	
	public String getTag() {
		return this.tag;
	}
}
