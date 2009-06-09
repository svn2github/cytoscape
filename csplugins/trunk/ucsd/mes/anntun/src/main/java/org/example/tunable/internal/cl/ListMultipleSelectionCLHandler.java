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

	public ListMultipleSelectionCLHandler(Method gmethod,Method smethod,Object o,Tunable tg,Tunable ts){
		super(gmethod,smethod,o,tg,ts);
	}
	
	
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
	
	
	public Option getOption() {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc = n.substring(ind);
		ListMultipleSelection<Object> currentValue = null;
		
		if(f!=null){
			return new Option(fc, true,"-- "+t.description() +" --\n  current selected values : "+lms.getSelectedValues()+"\n  available values : "+ lms.getPossibleValues());		
		}
		else if(gmethod!=null){
			try{
				currentValue = (ListMultipleSelection<Object>)gmethod.invoke(o);
			}catch(Exception e){e.printStackTrace();}
			return new Option(fc, true,"-- "+tg.description() +" --\n  current selected values : "+currentValue.getSelectedValues()+"\n  available values : "+ currentValue.getPossibleValues());		
		}
		else
			return null;
	}
	
	
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
	
	
	public Option getDetailedOption() {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc;
		if(n.substring(ind).length()<3)fc = n.substring(ind); 
		else fc = n.substring(ind,ind+3);
		if(f!=null){
			return new Option(fc, n, true,"-- "+t.description() +" --\n  current selected values : "+lms.getSelectedValues()+"\n  available values : "+ lms.getPossibleValues() +"\n to set items : -"+fc+" [item1,...,itemX]");			
		}
		else if(gmethod!=null){
			try{
				lms = (ListMultipleSelection<T>) gmethod.invoke(o);
			}catch(Exception e){e.printStackTrace();}
			return new Option(fc, n, true,"-- "+tg.description() +" --\n  current selected values : "+lms.getSelectedValues()+"\n  available values : "+ lms.getPossibleValues() +"\n to set items : -"+fc+" [item1,...,itemX]");
		}
		else
			return null;
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