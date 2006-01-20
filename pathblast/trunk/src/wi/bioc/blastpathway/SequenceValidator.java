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

import java.util.*;

public class SequenceValidator {
	
	public static boolean validate(String seq) {

		if ( seq.toUpperCase().matches("[ABCDEFGHIKLMNPQRSTUVWYZX\\*\\-]+") )
			return true;
		else 
			return false;
	}
}
