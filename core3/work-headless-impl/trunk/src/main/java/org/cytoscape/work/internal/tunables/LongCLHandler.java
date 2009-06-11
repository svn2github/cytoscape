package org.cytoscape.work.internal.tunables;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.cytoscape.work.Tunable;

public class LongCLHandler extends AbstractCLHandler {


	public LongCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}
	
	public LongCLHandler(Method gmethod, Method smethod, Object o, Tunable tg, Tunable ts){
		super(gmethod,smethod,o,tg,ts);
	}

	
	public void handleLine( CommandLine line ) {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;		
		String fc = n.substring(ind);

		try {
		if ( line.hasOption( fc ) ) {
			if(line.getOptionValue(fc).equals("--cmd")){displayCmds(fc);System.exit(1);}
			if ( f != null )
				f.set(o,Long.parseLong(line.getOptionValue(fc)) );
			else if ( smethod != null )
				smethod.invoke(o,Long.parseLong(line.getOptionValue(fc)) );
			else 
				throw new Exception("no Field or Method to set!");
		}
		} catch(Exception e) {e.printStackTrace();}
	}
	
	
	public Option getOption(){
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc = n.substring(ind);
		Long currentValue = null;
		
		if (f!=null){
			try{
				currentValue = (Long)f.get(o);
			}catch(Exception e){e.printStackTrace();}
			return new Option(fc, true,"-- " + t.description() + " --\n  current value : "+ currentValue);
		}
		else if (gmethod!=null){
			try{
				currentValue = (Long)gmethod.invoke(o);
			}catch(Exception e){e.printStackTrace();}
			return new Option(fc, true,"-- " + tg.description() + " --\n  current value : "+ currentValue);
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