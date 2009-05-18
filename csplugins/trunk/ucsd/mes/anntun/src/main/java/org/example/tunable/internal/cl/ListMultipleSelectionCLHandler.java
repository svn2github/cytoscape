package org.example.tunable.internal.cl;

import java.lang.reflect.*;

import org.apache.commons.cli.*;
import org.example.tunable.Tunable;
import org.example.tunable.util.ListMultipleSelection;

public class ListMultipleSelectionCLHandler<T> extends AbstractCLHandler {

	ListMultipleSelection<T> lms;
	
	public ListMultipleSelectionCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			lms = (ListMultipleSelection<T>) f.get(o);
		}catch (Exception e){e.printStackTrace();}
	}

	
	public ListMultipleSelectionCLHandler(Method m, Object o, Tunable t) {
		super(m,o,t);
		try{
			lms = (ListMultipleSelection<T>)f.get(o);
		}catch (Exception e){e.printStackTrace();}
	}

	
	public void handleLine( CommandLine line ) {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc;
		//if(n.substring(ind).length()<3)fc = n.substring(ind); 
		//else fc = n.substring(ind,ind+3);
		fc = n.substring(ind);
		
		try {
			if ( line.hasOption( fc ) ) {
				if(line.getOptionValue(fc).equals("--cmd")){displayCmds(fc);System.exit(1);}
				if(line.getOptionValue(fc).startsWith("[") && line.getOptionValue(fc).endsWith("]")){
					String args = line.getOptionValue(fc).substring(1,line.getOptionValue(fc).length()-1);
					String[] items = args.split(",");		
					setSelectedItems(items);
				}else throw new IllegalArgumentException("Items must be set as follow : [item1,...,itemX]");
			}
		} catch(Exception e) {e.printStackTrace();}
	}
	
	
	public Option getOption() {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc;
		//if(n.substring(ind).length()<3)fc = n.substring(ind); 
		//else fc = n.substring(ind,ind+3);
		fc = n.substring(ind);
		return new Option(fc, true,"-- "+t.description() +" --\n  current selected values : "+lms.getSelectedValues()+"\n  available values : "+ lms.getPossibleValues());		
	}
	
	private void setSelectedItems(String[] items){
		java.util.List<T> list = new java.util.ArrayList<T>();
		for(String str : items) list.add((T)str);
		lms.setSelectedValues(list);
		try{
			if( f!= null) f.set(o, lms);
			else if( m!= null) m.invoke(o, lms);
			else throw new Exception("no Field or Method to set!");
		}catch(Exception e){e.printStackTrace();}
	}
	
	public Option getDetailedOption() {
		String n = getName();
		//System.out.println("creating option for:    " + n);
		int ind = n.lastIndexOf(".")+1;
		String fc;
		if(n.substring(ind).length()<3)fc = n.substring(ind); 
		else fc = n.substring(ind,ind+3);
		return new Option(fc, n, true,"-- "+t.description() +" --\n  current selected values : "+lms.getSelectedValues()+"\n  available values : "+ lms.getPossibleValues() +"\n to set items : -"+fc+" [item1,...,itemX]");		
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