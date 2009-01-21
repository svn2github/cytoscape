
package org.example.tunable.cl;

import java.lang.reflect.*;
import java.util.*;
import org.apache.commons.cli.*;
import org.example.tunable.*;

public abstract class AbstractCLHandler extends AbstractHandler implements CLHandler {

	public AbstractCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}

	public AbstractCLHandler(Method m, Object o, Tunable t) {
		super(m,o,t);
	}

	protected String getName() {
		if ( f != null )
			return f.getName();
		else if ( m != null )
			return m.getName();
		else
			return "";
	}
	
	public Option getOption() {
		String n = getName();
		return new Option(n.substring(0,1), n, true, t.description());
	}

	public abstract void handleLine( CommandLine line );
}
