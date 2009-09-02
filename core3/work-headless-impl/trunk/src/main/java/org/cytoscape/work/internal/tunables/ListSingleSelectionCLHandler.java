package org.cytoscape.work.internal.tunables;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

/**
 * Constructs a commandline <code>Handler</code> for a List that allows the selection of just 1 item
 * 
 * @author pasteur
 *
 * @param <T> type of item that the <code>ListSingleSelection</code> contains : String, Double, Bounded...
 */
public class ListSingleSelectionCLHandler<T> extends AbstractCLHandler {

	/**
	 * A <code>ListSingleSelection</code> object
	 */
	private ListSingleSelection<T> lss;
	
	
	/**
	 * Constructs the <code>CLHandler</code> for the <code>ListSingleSelection</code> type of a Field <code>f</code>
	 * 
	 * @param f field that has been annotated
	 * @param o object contained in <code>f</code>
	 * @param t tunable associated to <code>f</code>
	 */
	@SuppressWarnings("unchecked")
	public ListSingleSelectionCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			lss = (ListSingleSelection<T>) f.get(o);
		}catch (Exception e){e.printStackTrace();}
	}

	
	
	/**
	 * Constructs the <code>CLHandler</code> for the <code>ListSingleSelection</code> type of an Object managed by <i>get</i> and <i>set</i> methods
	 * @param gmethod method that returns the value of the Object <code>o</code> annotated as a <code>Tunable</code>
	 * @param smethod method that sets a value to the Object <code>o</code> annotated as a <code>Tunable</code>
	 * @param o Object whose value will be set and get by the methods
	 * @param tg <code>Tunable</code> annotations of the Method <code>gmethod</code> annotated as <code>Tunable</code>
	 * @param ts <code>Tunable</code> annotations of the Method <code>smethod</code> annotated as <code>Tunable</code>
	 */
	public ListSingleSelectionCLHandler(Method gmethod,Method smethod,Object o, Tunable tg, Tunable ts){
		super(gmethod,smethod,o,tg,ts);
	}

	

	/**
	 *  If options/arguments are detected for this handler, it applies the argument : 
	 * <p><pre>
	 * <ul>
	 * <li> display some specific informations if the argument is <code>--cmd</code> ,or</li>
	 * <li> set the selected item (argument) in <code>lss</code>, and set the ListSingleSelection Object <code>o</code> contained in <code>f</code> with <code>lss</code> ,or</li>
	 * <li> set the selected item in <code>lss</code> with the argument, and set <code>lss</code> as a parameter for the <code>set</code> Method </li>
	 * </ul>
	 * </pre></p>
	 * @param commandline with arguments
	 */
	@SuppressWarnings("unchecked")
	public void handleLine( CommandLine line ) {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc = n.substring(ind);

		try {
			if ( line.hasOption( fc ) ) {
				if(line.getOptionValue(fc).equals("--cmd")){displayCmds(fc);System.exit(1);}
				if( f!= null){
					lss.setSelectedValue((T)line.getOptionValue(fc));
					f.set(o,lss);
				}
				else if(smethod!= null && gmethod!=null){
					lss = (ListSingleSelection<T>)gmethod.invoke(o);
					lss.setSelectedValue((T)line.getOptionValue(fc));
					smethod.invoke(o,lss);
				}
				else throw new Exception("no Field or Method to set!");
			}
		} catch(Exception e) {e.printStackTrace();}
	}
	
	
	
	
	/**
	 * Create an Option for the Object <code>o</code> contained in <code>f</code> or got from the <code>get</code> Method.
	 * The option has : 
	 * <p><pre>
	 * <ul>
	 * <li> the name of the handler</li>
	 * <li> its description, the available items that can be selected, and the one that is currently selected</li>
	 * </ul>
	 * </pre></p>
	 * @return option of the handler
	 */
	@SuppressWarnings("unchecked")
	public Option getOption() {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc = n.substring(ind);
		ListSingleSelection<T> currentValue = null;
		
		if(f!=null){
			return new Option(fc, true,"-- "+ t.description()+" --\n  current selected item : "+lss.getSelectedValue()+"\n  available items : "+lss.getPossibleValues());
		}
		else if (gmethod!=null){
			try{
				currentValue = (ListSingleSelection<T>) gmethod.invoke(o);
			}catch(Exception e){e.printStackTrace();}
			return new Option(fc, true,"-- "+ tg.description()+" --\n  current selected item : "+currentValue.getSelectedValue()+"\n  available items : "+currentValue.getPossibleValues());
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