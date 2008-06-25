
package org.cytoscape.tunable.impl.cl;

import java.lang.reflect.*;
import java.util.*;
import org.apache.commons.cli.*;
import org.cytoscape.tunable.*;

public class IntCLHandler extends AbstractCLHandler {

	public IntCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}
	
	public void handleLine( CommandLine line ) {
		String n = f.getName();
		String fc = n.substring(0,1);
		try {
		if ( line.hasOption( fc ) ) {
			f.set(o,Integer.parseInt(line.getOptionValue(fc)) );
		}
		} catch(Exception e) {e.printStackTrace();}
	}
}
