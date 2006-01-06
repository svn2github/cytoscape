package wi.bioc.blastpathway;

/**
 * <p>Title: pathblast</p>
 * <p>Description: pathblast</p>
 * <p>Copyright: Copyright (c) 2002 -- 2005 </p>
 * <p>Company: Whitehead Institute</p>
 * <p>Company: University of California San Diego</p>
 * @author Bingbing Yuan
 * @author Michael Smoot 
 * @version 1.1
 */

import java.io.*;
import java.util.*;

public class EValue {

	public static final double DEFAULT_EVALUE = 1E-5;
	private double numEvalue = DEFAULT_EVALUE;
	private String stringEvalue;
 
	public EValue(double nEvalue) throws Exception {
		numEvalue = nEvalue;
		stringEvalue = Double.toString(numEvalue);
		validate(numEvalue);
	}

	public EValue(String sEvalue) throws Exception {
		stringEvalue = sEvalue;
		if (stringEvalue != null) {
			stringEvalue = stringEvalue.trim();
			numEvalue = Double.parseDouble(stringEvalue);
			validate(numEvalue);
		} else {
			throw new Exception("evalue is null");
		}
				
	}

	private void validate(double eval) throws Exception {
		if ( eval < 0 )
			throw new Exception("evalue can't be less than 0");
	}

	public double getDouble() {
		return numEvalue;
	}
	public String getString() {
		return stringEvalue;
	}
}
