
package org.example.tunable.cl;

import java.lang.reflect.*;
import java.util.*;
import org.apache.commons.cli.*;
import org.example.tunable.*;

public class StringCLHandler implements CLHandler {

	Field f;
	Object o;
	Tunable t;

	public StringCLHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t = t;
	}

	
	public Option getOption() {
		String n = f.getName();

		return new Option(n.substring(0,1), n, true, t.description());
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
