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

import java.util.*;

public class SequenceValidator {
	private String m_raw_sequence;
	private String m_defline;
	private String m_seq;
	
	public SequenceValidator(String sequence) {
		m_raw_sequence = sequence;
		m_seq = sequence; // TODO
	}
	
	public boolean validate() {
		return true; // TODO  
	}
	
	public String getSequence() {
		return m_seq;
	}
}
