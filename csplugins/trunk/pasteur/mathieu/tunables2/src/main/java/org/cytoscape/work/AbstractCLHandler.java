
package org.cytoscape.work;

import java.lang.reflect.*;
import org.apache.commons.cli.*;
import org.cytoscape.work.internal.cl.CLHandler;


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
			return ns.substring( ns.lastIndexOf(".")+1) + ":" + f.getName();
		} else if ( m != null ) {
			String ns = m.getDeclaringClass().toString();
			return ns.substring( ns.lastIndexOf(".")+1) + ":" + m.getName();
		} else 
			return "";
	}

	public Option getOption() {
		String n = getName();
		System.out.println("creating option for: " + n);
		int ind = n.lastIndexOf(":")+1;
		return new Option(n.substring(ind,ind+1), n, true, t.description());
	}

	public abstract void handleLine( CommandLine line );
}
