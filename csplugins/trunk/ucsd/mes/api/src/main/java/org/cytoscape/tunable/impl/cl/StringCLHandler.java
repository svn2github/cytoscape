
package org.cytoscape.tunable.impl.cl;

import java.lang.reflect.*;
import java.util.*;
import org.apache.commons.cli.*;
import org.cytoscape.tunable.*;

public class StringCLHandler extends AbstractCLHandler {

	public StringCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}

	public void handleLine( CommandLine line ) {
		String n = f.getName();
		String fc = n.substring(0,1);
		try {
		if ( line.hasOption( fc ) ) {
			f.set(o,line.getOptionValue(fc) );
		}
		} catch(Exception e) {e.printStackTrace();}
	}
}
