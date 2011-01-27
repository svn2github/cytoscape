package edu.ucsd.bioeng.idekerlab.ncbiclient.util;

import java.util.Set;
import java.util.TreeSet;

public class BioGRIDUtil {
	
	private static final String[] PHYSICAL = {
		"Affinity Capture-Luminescence",
		"Affinity Capture-MS",
		"Affinity Capture-RNA",
		"Affinity Capture-Western",
		"Biochemical Activity",
		"Co-crystal Structure",
		"Co-fractionation",
		"Co-localization",
		"Co-purification",
		"Far Western",
		"FRET",
		"PCA",
		"Protein-peptide",
		"Protein-RNA",
		"Reconstituted Complex",
		"Two-hybrid" };
	
	private static final String[] GENETIC = {
		"Dosage Growth Defect",
		"Dosage Lethality",
		"Dosage Rescue",
		"Phenotypic Enhancement",
		"Phenotypic Suppression",
		"Synthetic Growth Defect",
		"Synthetic Haploinsufficiency",
		"Synthetic Lethality",
		"Synthetic Rescue"
	};
	
	private static final Set<String> P_LIST = new TreeSet<String>();
	private static final Set<String> G_LIST = new TreeSet<String>();
	
	static {
		for(String p:PHYSICAL)
			P_LIST.add(p);
		for(String g:GENETIC)
			G_LIST.add(g);
	}
	
	public static String getInteractionType(String typeString) {
		if(P_LIST.contains(typeString))
			return "physical";
		else if(G_LIST.contains(typeString))
			return "genetic";
		else
			return "unknown";
	}
	
}
