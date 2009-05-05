package org.cytoscape.work.internal.tunables;

import java.lang.reflect.*;
import org.apache.commons.cli.*;
import org.cytoscape.work.Tunable;


public class FloatCLHandler extends AbstractCLHandler {


	public FloatCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}

	public FloatCLHandler(Method m, Object o, Tunable t) {
		super(m,o,t);
	}

	public void handleLine( CommandLine line ) {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;		
		String fc;
		if(n.substring(ind).length()<3)fc = n.substring(ind); 
		else fc = n.substring(ind,ind+3);

		try {
		if ( line.hasOption( fc ) ) {
			if ( f != null )
				f.set(o,Float.parseFloat(line.getOptionValue(fc)) );
			else if ( m != null )
				m.invoke(o,Float.parseFloat(line.getOptionValue(fc)) );
			else 
				throw new Exception("no Field or Method to set!");
		}
		} catch(Exception e) {e.printStackTrace();}
	}
	
	
	public Option getOption() {
		String n = getName();
		System.out.println("creating option for:    " + n);
		int ind = n.lastIndexOf(".")+1;
		String fc;
		if(n.substring(ind).length()<3)fc = n.substring(ind); 
		else fc = n.substring(ind,ind+3);
		return new Option(fc, n, true, t.description());
	}
}
