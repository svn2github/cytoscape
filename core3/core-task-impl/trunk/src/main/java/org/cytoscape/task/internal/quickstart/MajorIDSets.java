package org.cytoscape.task.internal.quickstart;

/**
 * List of major ID types.
 */
public enum MajorIDSets {
	ENSEMBL("Ensembl Gene ID"), ENTREZ_GENE("NCBI Entrez Gene ID"), UNIPROT("UniProt ID");
	
	private final String displayName;
	
	private MajorIDSets(final String displayName) {
		this.displayName = displayName;
	}
	
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	
}