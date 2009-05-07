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
		String fc;
		if(n.substring(ind).length()<3)fc = n.substring(ind); 
		else fc = n.substring(ind,ind+3);

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
		String fc;
		if(n.substring(ind).length()<3)fc = n.substring(ind); 
		else fc = n.substring(ind,ind+3);
		Integer val = null;		
		if( f!=null){
			try{
				val = (Integer)f.get(o);
			}catch(Exception e){e.printStackTrace();}
			return new Option(fc, n, true,"-- "+ t.description()+" --\n  current value : "+val);
		}
		
		else if(m!=null){
			Type[] types = m.getParameterTypes();
			java.util.List list = new java.util.ArrayList();
			for(int i=0;i<types.length;i++) list.add(i,types[i]);
			return new Option(fc, n, true,"-- "+ t.description()+" --\n Method's parameter : "+list);
		}
		else return null;
	}
}
