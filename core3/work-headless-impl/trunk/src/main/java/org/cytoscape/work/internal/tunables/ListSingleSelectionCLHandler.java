package org.cytoscape.work.internal.tunables;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

public class ListSingleSelectionCLHandler<T> extends AbstractCLHandler {

	ListSingleSelection<T> lss;
	
	
	@SuppressWarnings("unchecked")
	public ListSingleSelectionCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			lss = (ListSingleSelection<T>) f.get(o);
		}catch (Exception e){e.printStackTrace();}
	}

	public ListSingleSelectionCLHandler(Method gmethod,Method smethod,Object o, Tunable tg, Tunable ts){
		super(gmethod,smethod,o,tg,ts);
	}

	

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
	
	
	@SuppressWarnings("unchecked")
	public Option getOption() {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc = n.substring(ind);
		ListSingleSelection<T> currentValue = null;
		
		if(f!=null){
			return new Option(fc, true,"-- "+ t.description()+" --\n  current selected value : "+lss.getSelectedValue()+"\n  available values : "+lss.getPossibleValues());
		}
		else if (gmethod!=null){
			try{
				currentValue = (ListSingleSelection<T>) gmethod.invoke(o);
			}catch(Exception e){e.printStackTrace();}
			return new Option(fc, true,"-- "+ tg.description()+" --\n  current selected value : "+currentValue.getSelectedValue()+"\n  available values : "+currentValue.getPossibleValues());
		}
		else return null;
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