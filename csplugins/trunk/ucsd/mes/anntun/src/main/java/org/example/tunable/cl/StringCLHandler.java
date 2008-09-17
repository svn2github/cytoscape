
package org.example.tunable.cl;

import java.lang.reflect.*;
import java.util.*;
import org.apache.commons.cli.*;
import org.example.tunable.*;

public class StringCLHandler implements CLHandler {

	Field f;
	Object o;
	Tunable t;
	Method m;

	public StringCLHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t = t;
	}

	public StringCLHandler(Method m, Object o, Tunable t) {
		this.m = m;
		this.o = o;
		this.t = t;
	}

	private String getName() {
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
