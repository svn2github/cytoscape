package org.cytoscape.work.internal.tunables;

import java.lang.reflect.*;
import org.apache.commons.cli.*;
import org.cytoscape.work.Tunable;

public class BooleanCLHandler extends AbstractCLHandler {


	public BooleanCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}

	public BooleanCLHandler(Method m, Object o, Tunable t) {
		super(m,o,t);
	}

	public void handleLine( CommandLine line ) {
		String n = getName(); 
		int ind = n.lastIndexOf(".")+1;
		String fc = n.substring(ind, ind+1);		
		try {
			if ( line.hasOption( fc ) ) {
				if ( f != null )
					f.set(o,Boolean.parseBoolean(line.getOptionValue(fc)));
				else if ( m != null )
					m.invoke(o,Boolean.parseBoolean(line.getOptionValue(fc)));
				else 
					throw new Exception("no Field or Method to set!");
			}
		} catch(Exception e) {e.printStackTrace();}
	}

    public Option getOption() {
        String n = getName();
        int ind = n.lastIndexOf(".")+1;
        return new Option(n.substring(ind,ind+1), n, true, t.description());
    }
}