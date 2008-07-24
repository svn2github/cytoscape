
package org.cytoscape.work.tunable.impl.cl;

import java.lang.reflect.*;
import java.util.*;
import org.apache.commons.cli.*;
import org.cytoscape.work.tunable.*;
import org.cytoscape.work.Tunable;

public abstract class AbstractCLHandler implements CLHandler {

	Field f;
	Object o;
	Tunable t;

	public AbstractCLHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t = t;
	}

	
	public Option getOption() {
		String n = f.getName();

		return new Option(n.substring(0,1), n, true, t.description());
	}

	public abstract void handleLine( CommandLine line ); 
}
