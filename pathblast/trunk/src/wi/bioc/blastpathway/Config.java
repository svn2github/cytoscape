package wi.bioc.blastpathway;

import java.util.*;

import nct.service.sequences.SequenceDatabase;
import nct.service.sequences.DIPSequenceDatabase;
import nct.service.interactions.DIPInteractionNetwork;
import nct.service.synonyms.DIPSynonyms;
import nct.service.synonyms.SynonymMapper;
import nct.graph.SequenceGraph;
import nct.graph.basic.BlastGraph;
import java.io.*;

/**
 * <p>Title: pathblast</p>
 * <p>Description: pathblast</p>
 * <p>Copyright: Copyright (c) 2002 -- 2005 </p>
 * <p>Company: Whitehead Institute</p>
 * @author Bingbing Yuan
 * @version 1.1
 */

public class Config {
	public static final String[] T_ORG_VALUES = {
		"Saccharomyces_cerevisiae.fa", 
		"Helicobacter_pylori_26695.fa", 
		"Escherichia_coli.fa", 
		"Caenorhabditis_elegans.fa", 
		"Drosophila_melanogaster.fa", 
		"Mus_musculus.fa", 
		"Homo_sapiens.fa"
	};
	public static final String[] T_ORG_NAMES = {
		"Saccharomyces cerevisiae",
		"Helicobacter pylori",
		"Escherichia coli",
		"Caenorhabditis elegans",
		"Drosophila melanogaster",
		"Mus musculus",
		"Homo sapiens"
	};
	public static final String PROTEINS_SESSION_KEY = "blastpathway.proteins";
	public static final String EVALUE_SESSION_KEY = "blastpathway.evalue";
	public static final String TORG_SESSION_KEY = "blastpathway.torg";
	public static final String USE_ZERO_SESSION_KEY = "blastpathway.usezero";
	public static final String[] PROTEIN_NAMES = { "A", "B", "C", "D", "E"};
	public static final String TMP_DIR_NAME;
	public static final String TMP_URL_BASE;
	protected static Map<String,SequenceGraph<String,Double>> speciesGraphs;
	protected static SequenceDatabase seqDB; 
	protected static SynonymMapper synMap; 
	protected static Properties props; 

	static {
		props = System.getProperties(); 
		ResourceBundle bun = ResourceBundle.getBundle("wi.bioc.blastpathway.pathblast");
		Enumeration<String> en = bun.getKeys();
		while(en.hasMoreElements()) {
			String key = en.nextElement();
			props.put(key,bun.getString(key));
		}
		TMP_DIR_NAME = props.getProperty("pathblast.tmpdir");
		File tmpDir = new File(TMP_DIR_NAME);
		if ( !tmpDir.isDirectory() )
			tmpDir.mkdir();
		TMP_URL_BASE = "tmp";
		System.out.println("user.dir=" + System.getProperty("user.dir"));
		System.out.println("pathblast.tmpdir=" + TMP_DIR_NAME);
		System.out.println("TMP_URL_BASE=" + TMP_URL_BASE);

		speciesGraphs = new HashMap<String,SequenceGraph<String,Double>>();

		System.out.println("creating synonym mapper");
		synMap = new DIPSynonyms(System.getProperty("dip.xin.filename"));

		System.out.println("creating sequence db");
		seqDB = new DIPSequenceDatabase(System.getProperty("dip.fasta.filename"), synMap);

		// create all of the species graphs
		for ( int i = 0; i < T_ORG_VALUES.length; i++ ) {
			String speciesDb = T_ORG_VALUES[i];
			String species = T_ORG_NAMES[i];
			System.out.println("creating species: " + species);
			createSpeciesGraph(species,speciesDb);
		}
		System.out.println("finished static initialization");
	}

	public static SequenceGraph<String,Double> getSpeciesGraph(String speciesDb) {
		
		// check to see if the name is a name or a value
		String species = "";
		for ( int i = 0; i < T_ORG_VALUES.length; i++ )
			if ( speciesDb.equals(T_ORG_VALUES[i]) ) {
				species = T_ORG_NAMES[i];
				break;
			}

		if ( species.equals("") )
			return null;
		
		// just in case
		if ( !speciesGraphs.containsKey(species) ) 
			createSpeciesGraph(species,speciesDb);

		return speciesGraphs.get(species);
	}

	public static SequenceDatabase getSeqDB() {
		return seqDB;
	}
	public static SynonymMapper getSynonymMapper() {
		return synMap;
	}

	public static Properties getProperties() {
		return props;
	}

	private static void createSpeciesGraph(String species, String speciesDb) {
		DIPInteractionNetwork din = new DIPInteractionNetwork(props.getProperty("dip.xin.filename"), species);
		BlastGraph<String,Double> bg = new BlastGraph<String,Double>(speciesDb,props.getProperty("blast.db.location"));	
		din.updateGraph(bg);

		speciesGraphs.put(species,bg);
	}
}

