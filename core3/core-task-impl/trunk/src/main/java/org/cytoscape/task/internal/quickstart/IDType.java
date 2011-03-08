package org.cytoscape.task.internal.quickstart;

/**
 * List of major ID types.
 */
public enum IDType {
	ENSEMBL("Ensembl Gene ID"), ENTREZ_GENE("Entrez Gene ID"), UNIPROT("UniProt ID");
	
	private final String displayName;
	
	private IDType(final String displayName) {
		this.displayName = displayName;
	}
	
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	
}