package nct.service.sequences;


import java.io.FileReader;
import java.util.*;

import nct.graph.Graph;
import nct.parsers.FastaProteinParser;
import nct.service.synonyms.SynonymMapper;

import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;

/** 
 * An implementation of the {@link SequenceDatabase} interface that is 
 * constructed from a DIP fasta file. The assumption is that at least all
 * of the sequences found in the DIP XIN file will be found in the fasta file.
 */
public class DIPSequenceDatabase  implements SequenceDatabase { 

	protected Map<String,String> sequenceMap;

	protected SynonymMapper synonyms;

	/**
	 * @param fastaFile The DIP fastaFile with which to create the database.
	 */
	public DIPSequenceDatabase( String fastaFile, SynonymMapper synonyms ) {

		this.synonyms = synonyms;
		sequenceMap = new HashMap<String,String>();
		try { 
			SequenceIterator seqs = FastaProteinParser.parseFile(fastaFile);
			while (seqs.hasNext()) {
				Sequence s = seqs.nextSequence();
				String[] names = s.getName().split("\\|");
				sequenceMap.put(names[0],s.seqString());
			}
		} catch (Exception e) { e.printStackTrace(); }
	}

        /**
         * Determines whether or not the specified id points to a
         * sequence in the database.
         * @param id The id to search for in the database.
         * @return True if the id is found in the database, false otherwise.
         */
	public boolean contains(String id) { 
		String uid = synonyms.getSynonym(id,"uid");	
		if ( uid == null )
			return false;
		
		return sequenceMap.containsKey(uid);
	}

        /**
         * Returns the sequence associated with the specified id from
         * the database.
         * @param id The id of the sequence to retrieve from the database.
         * @return A String representation of the sequence found in the database.
         */
	public String getSequence(String id) {
		String uid = synonyms.getSynonym(id,"uid");	
		if ( uid == null )
			return null;
	
		return sequenceMap.get(uid);
	}

}



