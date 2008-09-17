
package org.cytoscape.work.tunable.impl.cl;

import java.lang.reflect.*;
import java.util.*;
import org.apache.commons.cli.*;
import org.cytoscape.work.tunable.*;
import org.cytoscape.work.Tunable;

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
		} catch(IllegalAccessException e) {e.printStackTrace();}
	}
}
