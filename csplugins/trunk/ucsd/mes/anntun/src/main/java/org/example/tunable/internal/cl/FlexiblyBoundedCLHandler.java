package org.example.tunable.internal.cl;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.*;
import org.example.tunable.*;
import org.example.tunable.util.AbstractFlexiblyBounded;

public class FlexiblyBoundedCLHandler<T extends AbstractFlexiblyBounded<?>> extends AbstractCLHandler {

	T fbo;
	Map<String,String> argsMap;

	public FlexiblyBoundedCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			fbo = (T) f.get(o);
		}catch (Exception e){e.printStackTrace();}
	}
	
	public FlexiblyBoundedCLHandler(Method gmethod, Method smethod, Object o, Tunable tg, Tunable ts){
		super(gmethod,smethod,o,tg,ts);
	}

	
	public void handleLine( CommandLine line ) {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc = n.substring(ind);
		
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
		
		int ind = n.lastIndexOf(".")+1;
		String fc = n.substring(ind);

		T currentValue = null;
		
		if( f!=null){
			if(fbo.isLowerBoundStrict())lbound="<";
			if(fbo.isUpperBoundStrict())ubound="<";
			return new Option(fc, true,"-- "+ t.description() +" --\n  current value : "+fbo.getValue()+ "\n  possible value : (" + fbo.getLowerBound()+ " " + lbound + " x " + ubound + " " + fbo.getUpperBound() + ")");//+"\n                                      --cmd : display informations and available commands");		
		}
		else if(gmethod!=null){
			try{
				currentValue = (T)gmethod.invoke(o);
			}catch(Exception e){e.printStackTrace();}
			return new Option(fc, true,"-- " +tg.description() +" --\n  current value : "+currentValue.getValue());
		}
		else return null;
	}
	
	
	
	private void applyArgsValues(Map<String,String> map){
		try{
			if( f!= null ){
				if(map.containsKey("val"))fbo.setValue(map.get("val"));
				if(map.containsKey("up"))fbo.setUpperBound(map.get("up"));
				if(map.containsKey("low"))fbo.setLowerBound(map.get("low"));
				if(map.containsKey("lowstrict"))fbo.setLowerBoundStrict(Boolean.parseBoolean(map.get("lowstrict")));
				if(map.containsKey("upstrict"))fbo.setUpperBoundStrict(Boolean.parseBoolean(map.get("upstrict")));
				f.set(o, fbo);
			}
			else if(smethod!= null && gmethod!=null) {
				fbo = (T)gmethod.invoke(o);
				if(map.containsKey("val"))fbo.setValue(map.get("val"));
				if(map.containsKey("up"))fbo.setUpperBound(map.get("up"));
				if(map.containsKey("low"))fbo.setLowerBound(map.get("low"));
				if(map.containsKey("lowstrict"))fbo.setLowerBoundStrict(Boolean.parseBoolean(map.get("lowstrict")));
				if(map.containsKey("upstrict"))fbo.setUpperBoundStrict(Boolean.parseBoolean(map.get("upstrict")));
				smethod.invoke(o, fbo);
			}
			else throw new Exception("no Field or Method to set!");
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	@SuppressWarnings("unchecked")
	public Option getDetailedOption() {
		String n = getName();
		String lbound="\u2264";
		String ubound="\u2264";
		
		int ind = n.lastIndexOf(".")+1;
		String fc = n.substring(ind);
		
		T currentValue = null;
		
		if( f!=null){
			if(fbo.isLowerBoundStrict())lbound="<";
			if(fbo.isUpperBoundStrict())ubound="<";
			return new Option(fc, true,"-- "+ t.description() +" --\n  current value : "+fbo.getValue()+ "\n  possible value : (" + fbo.getLowerBound()+ " " + lbound + " x " + ubound + " " + fbo.getUpperBound() + ")\nCommands Options for -"+ fc +" :\n (multiple commands can be coupled by inserting \":\")\n  example : -"+fc+" val.x:up.y:upstrict.true \n-"+fc+" val.x : setValue \n-"+fc+" up.x : setUpperBound \n-"+fc+" low.x : setLowerBound \n-"+fc+" lowstrict.Boolean : setLowerBoundStrict \n-"+fc+" upstrict.Boolean : setUpperBoundStrict\n");
		}
		else if(gmethod!=null){
			try{
				currentValue = (T)gmethod.invoke(o);
			}catch(Exception e){e.printStackTrace();}
			return new Option(fc, true,"-- " +tg.description() +" --\n  current value : "+currentValue.getValue()+ "\n  possible value : (" + currentValue.getLowerBound()+ " " + lbound + " x " + ubound + " " + currentValue.getUpperBound() + ")\nCommands Options for -"+ fc +" :\n (multiple commands can be coupled by inserting \":\")\n  example : -"+fc+" val.x:up.y:upstrict.true \n-"+fc+" val.x : setValue \n-"+fc+" up.x : setUpperBound \n-"+fc+" low.x : setLowerBound \n-"+fc+" lowstrict.Boolean : setLowerBoundStrict \n-"+fc+" upstrict.Boolean : setUpperBoundStrict\n");
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
	}
}
