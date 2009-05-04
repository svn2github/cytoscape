package org.example.tunable.internal.cl;

import java.lang.reflect.*;
import org.apache.commons.cli.*;
import org.example.tunable.*;

public class IntCLHandler extends AbstractCLHandler {


	public IntCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}

	public IntCLHandler(Method m, Object o, Tunable t) {
		super(m,o,t);
	}

	public void handleLine( CommandLine line ) {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;		
		String fc = n.substring(ind,ind+1);

		try {
		if ( line.hasOption( fc ) ) {
			if ( f != null )
				f.set(o,Integer.parseInt(line.getOptionValue(fc)) );
			else if ( m != null )
				m.invoke(o,Integer.parseInt(line.getOptionValue(fc)) );
			else 
				throw new Exception("no Field or Method to set!");
		}
		} catch(Exception e) {e.printStackTrace();}
	}
	
	
	public Option getOption() {
		String n = getName();
		System.out.println("creating option for:    " + n);
		int ind = n.lastIndexOf(".")+1;
		//If arguments
		return new Option(n.substring(ind,ind+1), n, true, t.description());
		//If not
//		return new Option(n.substring(ind,ind+1), n, false, t.description());		
		
	}
}
