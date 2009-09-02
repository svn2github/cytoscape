package org.cytoscape.work.internal.tunables;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListMultipleSelection;


/**
 * Constructs a commandline <code>Handler</code> for a List that allows the selection of one or more item(s)
 * 
 * @author pasteur
 *
 * @param <T> type of item that the <code>ListMultipleSelection</code> contains : String, Double, Bounded...
 */
public class ListMultipleSelectionCLHandler<T> extends AbstractCLHandler {

	/**
	 * A <code>ListMultipleSelection</code> object
	 */
	private ListMultipleSelection<T> lms;
	
	
	/**
	 * Constructs the <code>CLHandler</code> for the <code>ListMultipleSelection</code> type of a Field <code>f</code>
	 * 
	 * @param f field that has been annotated
	 * @param o object contained in <code>f</code>
	 * @param t tunable associated to <code>f</code>
	 */
	@SuppressWarnings("unchecked")
	public ListMultipleSelectionCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			lms = (ListMultipleSelection<T>) f.get(o);
		}catch (Exception e){e.printStackTrace();}
	}

	
	
	/**
	 * Constructs the <code>CLHandler</code> for the <code>ListMultipleSelection</code> type of an Object managed by <i>get</i> and <i>set</i> methods
	 * @param gmethod method that returns the value of the Object <code>o</code> annotated as a <code>Tunable</code>
	 * @param smethod method that sets a value to the Object <code>o</code> annotated as a <code>Tunable</code>
	 * @param o Object whose value will be set and get by the methods
	 * @param tg <code>Tunable</code> annotations of the Method <code>gmethod</code> annotated as <code>Tunable</code>
	 * @param ts <code>Tunable</code> annotations of the Method <code>smethod</code> annotated as <code>Tunable</code>
	 */
	public ListMultipleSelectionCLHandler(Method gmethod,Method smethod,Object o,Tunable tg,Tunable ts){
		super(gmethod,smethod,o,tg,ts);
	}
	
	
	
	/**
	 * 	 * If options/arguments are detected for this handler, it applies the argument : 
	 * <p><pre>
	 * <ul>
	 * <li> display some specific informations if the argument is <code>--cmd</code> ,or</li>
	 * <li> if the arguments are surrounded by "[" and "]" ,split them between the "," and set them as <i>selected</i> in <code>lms</code> </li>
	 * </ul>
	 * </pre></p>
	 * 
	 * @param commandline with arguments
	 */
	public void handleLine( CommandLine line ) {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc = n.substring(ind);
		
		try {
			if ( line.hasOption( fc ) ) {
				if(line.getOptionValue(fc).equals("--cmd")){displayCmds(fc);System.exit(1);}
				if(line.getOptionValue(fc).startsWith("[") && line.getOptionValue(fc).endsWith("]")){
					String args = line.getOptionValue(fc).substring(line.getOptionValue(fc).indexOf("[")+1,line.getOptionValue(fc).indexOf("]"));
					String[] items = args.split(",");		
					setSelectedItems(items);
				}else throw new IllegalArgumentException("Items must be set as follow : [item1,...,itemX]");
			}
		} catch(Exception e) {e.printStackTrace();}
	}
	
	
	
	/**
	 * Applies the modifications required to the <code>ListMultipleSelection</code>'s parameters by setting the selected items.
	 * 
	 * The <code>ListMultipleSelection</code> contained in <code>f</code> is set with the modified <code>lms</code>, or the <code>smethod</code> takes the modified <code>lms</code> as a parameter
	 * 
	 * @param items that need to be set as <i>selected</i> in <code>ListMultipleSelection</code>
	 */
	@SuppressWarnings("unchecked")
	private void setSelectedItems(String[] items){
		java.util.List<T> list = new java.util.ArrayList<T>();
		for(String str : items) list.add((T)str);
		try{
			if(f!=null){
				lms.setSelectedValues(list);
				f.set(o, lms);
			}
			else if(gmethod!=null && smethod!=null){
				lms = (ListMultipleSelection<T>) gmethod.invoke(o);
				lms.setSelectedValues(list);
				smethod.invoke(o, lms);
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
	 * <li> its description, the available items that can be selected, and the one(s) that are currently selected</li>
	 * </ul>
	 * </pre></p>
	 * @return option of the handler
	 */
	@SuppressWarnings("unchecked")
	public Option getOption() {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc = n.substring(ind);
		ListMultipleSelection<Object> currentValue = null;
		
		if(f!=null){
			return new Option(fc, true,"-- "+t.description() +" --\n  current selected items : "+lms.getSelectedValues()+"\n  available items : "+ lms.getPossibleValues());		
		}
		else if(gmethod!=null){
			try{
				currentValue = (ListMultipleSelection<Object>)gmethod.invoke(o);
			}catch(Exception e){e.printStackTrace();}
			return new Option(fc, true,"-- "+tg.description() +" --\n  current selected items : "+currentValue.getSelectedValues()+"\n  available items : "+ currentValue.getPossibleValues());		
		}
		else
			return null;
	}
	
	
	
	
	/**
	 * Provides option with a lot of useful information : 
	 * <p><pre>
	 * <ul>
	 * <li>the current selected items</li>
	 * <li>the possible items : represented in a List</li>
	 * <li>how to set 1 or more items as selected by using the arguments</li>
	 * </ul>
	 * </pre></p>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Option getDetailedOption() {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc;
		if(n.substring(ind).length()<3)fc = n.substring(ind); 
		else fc = n.substring(ind,ind+3);
		if(f!=null){
			return new Option(fc, n, true,"-- "+t.description() +" --\n  current selected items : "+lms.getSelectedValues()+"\n  available items : "+ lms.getPossibleValues() +"\n to set items : -"+fc+" [item1,...,itemX]");			
		}
		else if(gmethod!=null){
			try{
				lms = (ListMultipleSelection<T>) gmethod.invoke(o);
			}catch(Exception e){e.printStackTrace();}
			return new Option(fc, n, true,"-- "+tg.description() +" --\n  current selected items : "+lms.getSelectedValues()+"\n  available items : "+ lms.getPossibleValues() +"\n to set items : -"+fc+" [item1,...,itemX]");
		}
		else
			return null;
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