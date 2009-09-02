package org.cytoscape.work.internal.tunables;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.AbstractFlexiblyBounded;


/**
 * Commandline handler for the type <i>FlexiblyBounded</i> of <code>Tunable</code>
 * 
 * @author pasteur
 */
public class FlexiblyBoundedCLHandler<T extends AbstractFlexiblyBounded<?>> extends AbstractCLHandler {

	/**
	 * An abstract flexiblybounded object
	 */
	private T fbo;
	
	/**
	 * all the arguments that can be applied to a flexiblybounded : modify the value, bounds, and strict bounds aspect
	 */
	private Map<String,String> argsMap;

	
	/**
	 * Constructs the <code>CLHandler</code> for the <code>FlexiblyBounded</code> type of a Field <code>f</code>
	 * 
	 * @param f field that has been annotated
	 * @param o object contained in <code>f</code>
	 * @param t tunable associated to <code>f</code>
	 */
	@SuppressWarnings("unchecked")
	public FlexiblyBoundedCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			fbo = (T) f.get(o);
		}catch (Exception e){e.printStackTrace();}
	}
	
	
	/**
	 * Constructs the <code>CLHandler</code> for the <code>FlexiblyBounded</code> type of an Object managed by <i>get</i> and <i>set</i> methods
	 * @param gmethod method that returns the value of the Object <code>o</code> annotated as a <code>Tunable</code>
	 * @param smethod method that sets a value to the Object <code>o</code> annotated as a <code>Tunable</code>
	 * @param o Object whose value will be set and get by the methods
	 * @param tg <code>Tunable</code> annotations of the Method <code>gmethod</code> annotated as <code>Tunable</code>
	 * @param ts <code>Tunable</code> annotations of the Method <code>smethod</code> annotated as <code>Tunable</code>
	 */
	public FlexiblyBoundedCLHandler(Method gmethod, Method smethod, Object o, Tunable tg, Tunable ts){
		super(gmethod,smethod,o,tg,ts);
	}

	
	/**
	 * 	 * If options/arguments are detected for this handler, it applies the argument : 
	 * <p><pre>
	 * <ul>
	 * <li> display some specific informations if the argument is <code>--cmd</code> ,or</li>
	 * <li> split all the arguments between the ":" , assign values to commands in a map, and applies the new values to the <code>FlexiblyBounded</code>'s parameters</li>
	 * </ul>
	 * </pre></p>
	 * 
	 * @param commandline with arguments
	 */
	public void handleLine( CommandLine line ) {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc = n.substring(ind);
		//create a map with the arguments splitted
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
	

	
	/**
	 * Applies the modifications required to the <code>FlexiblyBounded</code>'s parameters by setting the new values.
	 * 
	 * The <code>FlexiblyBounded</code> contained in <code>f</code> is set with the modified <code>fbo</code>, or the <code>smethod</code> takes the modified <code>fbo</code> as a parameter
	 * 
	 * @param map parameters of the <code>FlexiblyBounded</code> that need to be changed maped with their values
	 */
	@SuppressWarnings("unchecked")
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

	
	
	
	/**
	 * Create an Option for the Object <code>o</code> contained in <code>f</code> or got from the <code>get</code> Method.
	 * The option has : 
	 * <p><pre>
	 * <ul>
	 * <li> the name of the handler</li>
	 * <li> its description with its current value between the flexible bounds</li>
	 * </ul>
	 * </pre></p>
	 * @return option of the handler
	 */
	@SuppressWarnings("unchecked")
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
			return new Option(fc, true,"-- "+ t.description() +" --\n  current value : "+fbo.getValue()+ "\n  possible value : (" + fbo.getLowerBound()+ " " + lbound + " x " + ubound + " " + fbo.getUpperBound() + ")");
		}
		else if(gmethod!=null){
			try{
				currentValue = (T)gmethod.invoke(o);
			}catch(Exception e){e.printStackTrace();}
			return new Option(fc, true,"-- " +tg.description() +" --\n  current value : "+currentValue.getValue());
		}
		else return null;
	}
	
	
	
	/**
	 * Provides option with a lot of useful information : 
	 * <p><pre>
	 * <ul>
	 * <li>the current value</li>
	 * <li>the possible values : the lower and upper bounds</li>
	 * <li>whether or not they are strict</li>
	 * <li>how to set different parameters in one argument</li>
	 * </ul>
	 * </pre></p>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Option getDetailedOption() {
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
	
	
	
	/**
	 * Display some detailed informations to the user for this particular handler
	 * @param name of the handler
	 */
	private void displayCmds(String fc){
		HelpFormatter formatter = new HelpFormatter();
		Options options = new Options();
		options.addOption(this.getDetailedOption());
		formatter.setWidth(100);
		System.out.println("\n");
		formatter.printHelp("Detailed informations/commands for " + fc + " :", options);
	}
}
