
package org.cytoscape.work.internal.cl;

import java.lang.reflect.*;
import java.util.*;
import org.apache.commons.cli.*;
import org.cytoscape.work.Tunable;


public class StringCLHandler extends AbstractCLHandler {


	public StringCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}

	public StringCLHandler(Method m, Object o, Tunable t) {
		super(m,o,t);
	}

	public void handleLine( CommandLine line ) {
		String n = getName();
		String fc = n.substring(0,1);
		try {
		if ( line.hasOption( fc ) ) {
			if ( f != null )
				f.set(o,line.getOptionValue(fc) );
			else if ( m != null )
				m.invoke(o,Integer.parseInt(line.getOptionValue(fc)) );
			else 
				throw new Exception("no Field or Method to set!");
		}
		} catch(Exception e) {e.printStackTrace();}
	}
}
