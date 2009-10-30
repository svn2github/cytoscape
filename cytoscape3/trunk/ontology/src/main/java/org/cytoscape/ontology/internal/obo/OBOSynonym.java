package org.cytoscape.ontology.internal.obo;

/*
 * Type of Synonyms used in OBO
 */
public enum OBOSynonym {
	NORMAL("synonym"), RELATED("related_synonym"), EXACT("exact_synonym"), BROAD(
			"broad_synonym"), NARROW("narrow_synonym");

	private String typeText;

	private OBOSynonym(String type) {
		this.typeText = type;
	}

	public String toString() {
		return typeText;
	}
}