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
				if(line.getOptionValue(fc).equals("--cmd")){displayCmds(fc);System.exit(1);}
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
		String ubound="\u2264";
		
		//System.out.println("creating option for:    " + n);
		int ind = n.lastIndexOf(".")+1;
		String fc;
		if(n.substring(ind).length()<3)fc = n.substring(ind); 
		else fc = n.substring(ind,ind+3);
		
		if( f!=null){
			if(fbo.isLowerBoundStrict())lbound="<";
			if(fbo.isUpperBoundStrict())ubound="<";
			return new Option(fc, n, true,"-- "+ t.description() +" --\n  current value : "+fbo.getValue()+ "\n  possible value : (" + fbo.getLowerBound()+ " " + lbound + " x " + ubound + " " + fbo.getUpperBound() + ")");//+"\n                                      --cmd : display informations and available commands");		
		}
		else if(m!=null){
			Type[] types = m.getParameterTypes();
			java.util.List list = new java.util.ArrayList();
			for(int i=0;i<types.length;i++) list.add(i,types[i]);
			return new Option(fc, n, true,"-- "+ t.description()+" --\n  Method's parameters : "+list);
		}
		else return null;
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
	
	
	public Option getDetailedOption() {
		String n = getName();
		String lbound="\u2264";
		String ubound="\u2264";
		
		int ind = n.lastIndexOf(".")+1;
		String fc;
		if(n.substring(ind).length()<3)fc = n.substring(ind); 
		else fc = n.substring(ind,ind+3);
		if( f!=null){
			if(fbo.isLowerBoundStrict())lbound="<";
			if(fbo.isUpperBoundStrict())ubound="<";
			return new Option(fc, n, true,"-- "+ t.description() +" --\n  current value : "+fbo.getValue()+ "\n  possible value : (" + fbo.getLowerBound()+ " " + lbound + " x " + ubound + " " + fbo.getUpperBound() + ")\nCommands Options for -"+ fc +" :\n (multiple commands can be coupled by inserting \":\")\n  example : -"+fc+" val.x:up.y:upstrict.true \n-"+fc+" val.x : setValue \n-"+fc+" up.x : setUpperBound \n-"+fc+" low.x : setLowerBound \n-"+fc+" lowstrict.Boolean : setLowerBoundStrict \n-"+fc+" upstrict.Boolean : setUpperBoundStrict\n");
		}
		else if(m!=null){
			Type[] types = m.getParameterTypes();
			java.util.List list = new java.util.ArrayList();
			for(int i=0;i<types.length;i++) list.add(i,types[i]);
			return new Option(fc, n, true,"-- "+ t.description()+" --\n  Method's parameters : "+list);
		}
		else return null;
	}
	
	
	private void displayCmds(String fc){
		HelpFormatter formatter = new HelpFormatter();
		Options options = new Options();
		options.addOption(this.getDetailedOption());
		formatter.setWidth(100);
		System.out.println("\n");
		formatter.printHelp("Detailed informations/commands for " + fc + " :", options);
		//System.out.println("\nCommands Options for -"+ fc +"\n (multiple commands can be coupled by inserting \" : \" ) example : -"+fc+" val.x:up.y:upstrict.true\n\t-"+fc+" val.x : setValue\n\t-"+fc+" up.x : setUpperBound\n\t-"+fc+" low.x : setLowerBound\n\t-"+fc+" lowstrict.Boolean : setLowerBoundStrict\n\t-"+fc+" upstrict.Boolean : setUpperBoundStrict\n");
	}
}
