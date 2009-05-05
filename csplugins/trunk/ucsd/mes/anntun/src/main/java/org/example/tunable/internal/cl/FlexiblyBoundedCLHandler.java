package org.example.tunable.internal.cl;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.*;
import org.example.tunable.*;
import org.example.tunable.util.AbstractFlexiblyBounded;

public class FlexiblyBoundedCLHandler<T extends AbstractFlexiblyBounded> extends AbstractCLHandler {

	T fbo;
	Map<String,String> argsMap;
	public FlexiblyBoundedCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			fbo = (T) f.get(o);
		}catch (Exception e){e.printStackTrace();}
	}

	
	public FlexiblyBoundedCLHandler(Method m, Object o, Tunable t) {
		super(m,o,t);
		try{
			fbo = (T) f.get(o);
		}catch (Exception e){e.printStackTrace();}	
	}

	
	public void handleLine( CommandLine line ) {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc;
		if(n.substring(ind).length()<3)fc = n.substring(ind); 
		else fc = n.substring(ind,ind+3);
		
		argsMap = new HashMap<String,String>();
		try {
			if ( line.hasOption( fc ) ) {
				String lineArgs = line.getOptionValue(fc);
				String[] argsTab = lineArgs.split(":");
				for(int i = 0;i<argsTab.length;i++){
					int val = argsTab[i].lastIndexOf(".");
					argsMap.put(argsTab[i].substring(0, val),argsTab[i].substring(val+1));
				}			
				applyArgsValues(argsMap);
			}
		} catch(Exception e) {e.printStackTrace();}
	}
	
	
	public Option getOption() {
		String n = getName();
		String lbound="\u2264";
		if(fbo.isLowerBoundStrict())lbound="<";
		String ubound="\u2264";
		if(fbo.isUpperBoundStrict())ubound="<";

		System.out.println("creating option for:    " + n);
		int ind = n.lastIndexOf(".")+1;
		String fc;
		if(n.substring(ind).length()<3)fc = n.substring(ind); 
		else fc = n.substring(ind,ind+3);
		return new Option(fc, n, true, t.description() + " (" + fbo.getLowerBound()+ " " + lbound + " x " + ubound + " " + fbo.getUpperBound() + " )");		
	}
	
	
	
	private void applyArgsValues(Map<String,String> map){
		try{
			if(map.containsKey("val"))fbo.setValue(map.get("val"));
			if(map.containsKey("up"))fbo.setUpperBound(map.get("up"));
			if(map.containsKey("low"))fbo.setLowerBound(map.get("low"));
			if(map.containsKey("lowstrict"))fbo.setLowerBoundStrict(Boolean.parseBoolean(map.get("lowstrict")));
			if(map.containsKey("upstrict"))fbo.setUpperBoundStrict(Boolean.parseBoolean(map.get("upstrict")));

			if( f!= null ) f.set(o, fbo);
			else if( m!= null) m.invoke(o, fbo);
			else throw new Exception("no Field or Method to set!");
		}catch(Exception e){e.printStackTrace();}
	}
}
