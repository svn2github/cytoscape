package wi.bioc.blastpathway;

/**
 * <p>Title: pathblast</p>
 * <p>Description: pathblast</p>
 * <p>Copyright: Copyright (c) 2002 -- 2005 </p>
 * <p>Company: Whitehead Institute</p>
 * @author Bingbing Yuan
 * @author Michael Smoot
 * @version 1.1
 */

import java.io.*;
import java.util.*;

import nct.service.sequences.SequenceDatabase;
import nct.service.synonyms.SynonymMapper;

public class Protein implements java.io.Serializable {

	private String proteinId;
	private String org;
	private String seq;
	private String proteinError;
	private List<String> potentialIds;

	public static Protein createProtein(String name) {
		Protein p = null; 
		try {
		SynonymMapper syn = Config.getSynonymMapper();
		String uid = syn.getSynonym(name,"name");
		System.out.println("create protein name " + uid);
		SequenceDatabase seqs = Config.getSeqDB();
		String seq = seqs.getSequence(uid);
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
		//remove white spaces
		this.seq = this.seq.trim();
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
				if ( potentialIds.size() == 0 )
					proteinError = "Id not found in the database";
				if ( potentialIds.size() > 1 )
					proteinError = "The id specified is not an exact match to anything in the database, however there are potential matches.";
				if ( potentialIds.size() == 1 )
					proteinId = potentialIds.get(0);
				return potentialIds.size();

			} else {
				// if we have id and sequence, just validate the sequence because
				// we assume that the id could be anything
				return validateSeq();
			}
		}
	}

	private int validateSeq() {
		SequenceValidator validator = new SequenceValidator(seq);
		if ( validator.validate() ) {
			seq = validator.getSequence();
			return 1;
		} else {
			proteinError = "Your sequence is not properly formatted. See link above for details.";
			return 0;
		}
	}
}
