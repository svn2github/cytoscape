package org.cytoscape.work.internal.tunables;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.cytoscape.work.Tunable;

/**
 * Commandline handler for the type <i>Integer</i> of <code>Tunable</code>
 * 
 * @author pasteur
 */
public class IntCLHandler extends AbstractCLHandler {

	/**
	 * Constructs the <code>CLHandler</code> for the <code>Integer</code> type of a Field <code>f</code>
	 * 
	 * @param f field that has been annotated
	 * @param o object contained in <code>f</code>
	 * @param t tunable associated to <code>f</code>
	 */
	public IntCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}
	
	
	/**
	 * Constructs the <code>CLHandler</code> for the <code>Integer</code> type of an Object managed by <i>get</i> and <i>set</i> methods
	 * @param gmethod method that returns the value of the Object <code>o</code> annotated as a <code>Tunable</code>
	 * @param smethod method that sets a value to the Object <code>o</code> annotated as a <code>Tunable</code>
	 * @param o Object whose value will be set and get by the methods
	 * @param tg <code>Tunable</code> annotations of the Method <code>gmethod</code> annotated as <code>Tunable</code>
	 * @param ts <code>Tunable</code> annotations of the Method <code>smethod</code> annotated as <code>Tunable</code>
	 */
	public IntCLHandler(Method gmethod, Method smethod, Object o, Tunable tg, Tunable ts){
		super(gmethod,smethod,o,tg,ts);
	}
	

	/**
	 *  If options/arguments are detected for this handler, it applies the argument : 
	 * <p><pre>
	 * <ul>
	 * <li> display some specific informations if the argument is <code>--cmd</code> ,or</li>
	 * <li> apply the value to the Integer Object <code>o</code> contained in <code>f</code> ,or</li>
	 * <li> set the argument as an integer parameter for the <code>set</code> Method</li>
	 * </ul>
	 * </pre></p>
	 * @param commandline with arguments
	 */
	public void handleLine( CommandLine line ) {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;		
		String fc = n.substring(ind);

		try {
		if ( line.hasOption( fc ) ) {
			if(line.getOptionValue(fc).equals("--cmd")){displayCmds(fc);System.exit(1);}
			if ( f != null )
				f.set(o,Integer.parseInt(line.getOptionValue(fc)) );
			else if ( smethod != null )
				smethod.invoke(o,Integer.parseInt(line.getOptionValue(fc)) );
			else 
				throw new Exception("no Field or Method to set!");
		}
		} catch(Exception e) {e.printStackTrace();}
	}

	
	
	/**
	 * Create an Option for the Object <code>o</code> contained in <code>f</code> or got from the <code>get</code> Method.
	 * The option has : 
	 * <p><pre>
	 * <ul>
	 * <li> the name of the handler</li>
	 * <li> its description with its current value</li>
	 * </ul>
	 * </pre></p>
	 * @return option of the handler
	 */
	public Option getOption(){
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc = n.substring(ind);
		Integer currentValue = null;
		
		if (f!=null){
			try{
				currentValue = (Integer)f.get(o);
			}catch(Exception e){e.printStackTrace();}
			return new Option(fc, true,"-- " + t.description() + " --\n  current value : "+ currentValue);
		}
		else if (gmethod!=null){
			try{
				currentValue = (Integer)gmethod.invoke(o);
			}catch(Exception e){e.printStackTrace();}
			return new Option(fc, true,"-- " + tg.description() + " --\n  current value : "+ currentValue);
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
		options.addOption(this.getOption());
		formatter.setWidth(100);
		System.out.println("\n");
		formatter.printHelp("Detailed informations/commands for " + fc + " :", options);
	}
}
