package edu.ucsd.bioeng.idekerlab.PathwayWalkingPlugin;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNetworkData;

public class Translate {
	public enum IDType {
		INTACT_ID,
		ENTREZ_GENEID,
		UNIPROT_ID,
		GENE_NAME;
	}
	
	// This method will take a string ID of type and translate it to a new ID type
	public static String getTranslation(String ID, IDType type, IDType newType) {
		
		return ID;
	}
}