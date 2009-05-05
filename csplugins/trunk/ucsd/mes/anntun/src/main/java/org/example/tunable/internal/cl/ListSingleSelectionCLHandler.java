package org.example.tunable.internal.cl;

import java.lang.reflect.*;

import org.apache.commons.cli.*;
import org.example.tunable.Tunable;
import org.example.tunable.util.ListSingleSelection;

public class ListSingleSelectionCLHandler<T> extends AbstractCLHandler {

	ListSingleSelection<T> lss;
	
	public ListSingleSelectionCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			lss = (ListSingleSelection<T>) f.get(o);
		}catch (Exception e){e.printStackTrace();}
	}

	
	public ListSingleSelectionCLHandler(Method m, Object o, Tunable t) {
		super(m,o,t);
		try{
			lss = (ListSingleSelection<T>)f.get(o);
		}catch (Exception e){e.printStackTrace();}
	}

	
	public void handleLine( CommandLine line ) {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc;
		if(n.substring(ind).length()<3)fc = n.substring(ind); 
		else fc = n.substring(ind,ind+3);
		
		try {
			if ( line.hasOption( fc ) ) {
				if( f!= null){
					lss.setSelectedValue((T)line.getOptionValue(fc));
					f.set(o, lss);
				}
				else if( m!= null){
					lss.setSelectedValue((T)line.getOptionValue(fc));
					m.invoke(o, lss);
				}
				else throw new Exception("no Field or Method to set!");
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