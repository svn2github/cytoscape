package org.cytoscape.work.internal.tunables;

import java.lang.reflect.*;
import org.apache.commons.cli.*;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.AbstractBounded;


public class BoundedCLHandler<T extends AbstractBounded> extends AbstractCLHandler {

	T bo;

	public BoundedCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			bo = (T) f.get(o);
		}catch (Exception e){e.printStackTrace();}
	}

	
	public BoundedCLHandler(Method m, Object o, Tunable t) {
		super(m,o,t);
	}

	
	public void handleLine( CommandLine line ) {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;		
		String fc = n.substring(ind,ind+1);

		try {
		if ( line.hasOption( fc ) ) {
			if ( f != null ){
				bo.setValue(line.getOptionValue(fc));
				f.set(o,bo);}
			else if ( m != null ){
				bo.setValue(line.getOptionValue(fc));
				m.invoke(o,bo);
			}
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
		return new Option(n.substring(ind,ind+1), n, true, t.description() + " (" + bo.getLowerBound()+" < " + "x" + " < " + bo.getUpperBound() + " )");
		//If not
//		return new Option(n.substring(ind,ind+1), n, false, t.description());		
		
	}
}
