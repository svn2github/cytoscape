package org.example.tunable.internal.cl;

import java.lang.reflect.*;
import org.apache.commons.cli.*;
import org.example.tunable.*;
import org.example.tunable.util.AbstractBounded;

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
		String fc;
		if(n.substring(ind).length()<3)fc = n.substring(ind); 
		else fc = n.substring(ind,ind+3);
		
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
		String lbound="\u2264";
		if(bo.isLowerBoundStrict())lbound="<";
		String ubound="\u2264";
		if(bo.isUpperBoundStrict())ubound="<";
		
		System.out.println("creating option for:    " + n);
		int ind = n.lastIndexOf(".")+1;
		String fc;
		if(n.substring(ind).length()<3)fc = n.substring(ind); 
		else fc = n.substring(ind,ind+3);
		return new Option(fc, n, true, t.description() + " (" + bo.getLowerBound()+ " " + lbound + " x " + ubound + " " + bo.getUpperBound() + " )");		
	}
}
