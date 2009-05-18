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
		//if(n.substring(ind).length()<3)fc = n.substring(ind); 
		//else fc = n.substring(ind,ind+3);
		fc = n.substring(ind);
		try {
			if ( line.hasOption( fc ) ) {
				if(line.getOptionValue(fc).equals("--cmd")){displayCmds(fc);System.exit(1);}
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
		int ind = n.lastIndexOf(".")+1;
		String fc;
		//if(n.substring(ind).length()<3)fc = n.substring(ind); 
		//else fc = n.substring(ind,ind+3);
		fc = n.substring(ind);
		return new Option(fc, true,"-- "+ t.description()+" --\n  current selected value : "+lss.getSelectedValue()+"\n  available values : "+lss.getPossibleValues());
	}
	
	private void displayCmds(String fc){
		HelpFormatter formatter = new HelpFormatter();
		Options options = new Options();
		options.addOption(this.getOption());
		formatter.setWidth(100);
		System.out.println("\n");
		formatter.printHelp("Detailed informations/commands for " + fc + " :", options);
	}
}