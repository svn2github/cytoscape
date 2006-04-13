package wi.bioc.blastpathway;

/**
 * <p>Title: pathblast</p>
 * <p>Description: pathblast</p>
 * <p>Copyright: Copyright (c) 2002 -- 2006 </p>
 * <p>Company: Whitehead Institute</p>
 * <p>Company: University of California, San Diego</p>
 * @author Bingbing Yuan
 * @author Michael Smoot
 * @version 1.2
 */

import java.io.*;
import java.util.*;

import org.biojava.bio.seq.SequenceIterator;

import nct.service.sequences.SequenceDatabase;
import nct.service.synonyms.SynonymMapper;
import nct.parsers.FastaProteinParser;

public class Protein { 

	private String proteinId;
	private String org;
	private String seq;
	private String proteinError;
	private List<String> potentialIds;

	public static Protein createProtein(String name) {
		Protein p = null; 
		try {
		String uid = Config.getSynonymMapper().getSynonym(name,"name");
		System.out.println("create protein name " + uid);
		String seq = fetchSequence( uid ); 
		p =  new Protein(uid,seq,null);
		} catch (Exception e) { e.printStackTrace(); }

		return p;
	}

	public Protein() {
		this("","","");
	}

	public Protein(String proteinId, String seq, String org) {
		setProteinId(proteinId);
		setSeq(seq);
		this.setOrg(org);
		proteinError = null;
		potentialIds = new ArrayList<String>();
	}

	public String getProteinId() { return proteinId; }
	public void setProteinId(String proteinId) {
		this.proteinId = (proteinId == null? "" : proteinId);
		//remove white spaces
		this.proteinId = this.proteinId.trim();
	}

	public String getSeq() { return seq; }
	public void setSeq(String seq) {
		this.seq = (seq == null? "" : seq);
/*
		if ( seq.matches(">.+(\n??|\r??){1,}\\w+((\n??|\r??){1,}\\w*)*") ) {
			try {
			System.out.print("got fasta seq ... ");
			SequenceIterator si = FastaProteinParser.parseString(seq);
			if ( si.hasNext() ) {
				System.out.print("successfully parsed");
				seq = si.nextSequence().seqString();
				System.out.print("   '"+seq+"'");
			}
			System.out.println(" ");
			} catch ( Exception e ) { e.printStackTrace(); }
		}
		*/
	}

	public void setOrg(String org) { this.org = org; }
	public String getOrg() { return org; }


	public List<String> getPotentialIds() { return potentialIds; }

	public String getProteinError() { return proteinError; }

	public int validate() throws FileNotFoundException, IOException {
		proteinError = null; //reset the error message

		if ( (seq == null || seq.length() == 0) &&  
		     (proteinId == null || proteinId.length() == 0 ) ) {
			proteinError = "You must fill in either the proteinID or the sequence";
			return 0;
		}

		if ( (null == proteinId) || (proteinId.length()==0) ) {
			// no id, just sequence
			return validateSeq();
		} else {
			// id exists, but only validate it if no sequence is present
			if ( (null == seq) || (seq.length()==0) ) {

				// only validate id if seq is blank, meaning we need to find
				// the id in our database to get a sequence
				IdValidator validator = new IdValidator(this, Config.getSeqDB(),Config.getSynonymMapper()); 
				potentialIds = validator.validate();
				int size = potentialIds.size();
				if ( size <= 0 ) {
					proteinError = "Id not found in the database";
					return size;
				} else if ( size > 1 ) {
					//proteinError = "The id specified is not an exact match to anything in the database, however there are potential matches.";
					return size;
				} else { // exactly 1 id found
					proteinId = potentialIds.get(0);
					seq = fetchSequence( proteinId ); 
					return validateSeq();
				}
			} else {
				// if we have id and sequence, just validate the sequence because
				// we assume that the id could be anything
				return validateSeq();
			}
		}
	}

	private int validateSeq() {
		if ( SequenceValidator.validate(seq) ) {
			return 1;
		} else {
			proteinError = "Your sequence is not properly formatted. See link above for details.";
			return 0;
		}
	}

	private static String fetchSequence(String id) {
		String name = Config.getSynonymMapper().getSynonym(id,"name");
		return Config.getSeqDB().getSequence(name);
	}

	public static String checkUniqueness(Protein[] proteins) {
		String errorMsg = "";
		HashSet<String> pids = new HashSet<String>();
		HashSet<String> sids = new HashSet<String>();
		for (int k = 0; k < proteins.length; k++) {

			String proteinId = proteins[k].getProteinId();
			if (proteinId!=null) {
				if ( pids.contains(proteinId) ) 
					errorMsg += "  ERROR: Protein ID " + proteinId + " is a duplicate.";
				else 
					pids.add(proteinId);
			}

			String seq = proteins[k].getSeq();
			if ( seq != null && seq.length() > 0 ) {
				if ( sids.contains(seq) ) 
					errorMsg += " ERROR: Protein seq for ID " + proteinId + " is a duplicate ";
				else 
					sids.add(seq);
			}
		}
		return errorMsg;
	}
}
