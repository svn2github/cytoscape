
package org.example.tunable.internal.cl;

import java.lang.reflect.*;
import java.util.*;
import org.apache.commons.cli.*;
import org.example.tunable.*;

public class BooleanCLHandler extends AbstractCLHandler {


	public BooleanCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}

	public BooleanCLHandler(Method m, Object o, Tunable t) {
		super(m,o,t);
	}

	public void handleLine( CommandLine line ) {
		String n = getName(); 
		String fc = n.substring(0,1);
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
        System.out.println("creating option for: " + n);
        int ind = n.lastIndexOf(":")+1;
        return new Option(n.substring(ind,ind+1), n, false, t.description());
    }

}
