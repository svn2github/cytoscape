
//============================================================================
// 
//  file: DIPSequenceDatabase.java
// 
//  Copyright (c) 2006, University of California San Diego 
// 
//  This program is free software; you can redistribute it and/or modify it 
//  under the terms of the GNU General Public License as published by the 
//  Free Software Foundation; either version 2 of the License, or (at your 
//  option) any later version.
//  
//  This program is distributed in the hope that it will be useful, but 
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
//  for more details.
//  
//  You should have received a copy of the GNU General Public License along 
//  with this program; if not, write to the Free Software Foundation, Inc., 
//  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// 
//============================================================================



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



