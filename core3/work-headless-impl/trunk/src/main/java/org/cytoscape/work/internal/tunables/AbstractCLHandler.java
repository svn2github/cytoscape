package org.cytoscape.work.internal.tunables;

import java.lang.reflect.*;


import org.cytoscape.work.AbstractHandler;
import org.cytoscape.work.Tunable;
import org.apache.commons.cli.*;


public abstract class AbstractCLHandler extends AbstractHandler implements CLHandler {

	public AbstractCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}

	public AbstractCLHandler(Method m, Object o, Tunable t) {
		super(m,o,t);
	}

	protected String getName() {
		if ( f != null ) {
			String ns = f.getDeclaringClass().toString();
			return ns.substring( ns.lastIndexOf(".")+1) + "." + f.getName();
		} else if ( m != null ) {
			String ns = m.getDeclaringClass().toString();
			return ns.substring( ns.lastIndexOf(".")+1) + "." + m.getName();
		} else 
			return "";
	}
	
	public abstract Option getOption();
	public abstract void handleLine( CommandLine line );
}
