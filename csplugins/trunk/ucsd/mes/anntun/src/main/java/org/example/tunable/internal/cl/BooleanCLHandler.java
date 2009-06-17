package org.example.tunable.internal.cl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.example.tunable.Tunable;

public class BooleanCLHandler extends AbstractCLHandler {


	public BooleanCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}
	
	public BooleanCLHandler(Method gmethod,Method smethod,Object o, Tunable tg, Tunable ts){
		super(gmethod,smethod,o,tg,ts);
	}

	
//	public BooleanCLHandler(Method m, Object o, Tunable t) {
//		super(m,o,t);
//	}
	
	

	public void handleLine( CommandLine line ) {
		String n = getName(); 
		int ind = n.lastIndexOf(".")+1;
		String fc = n.substring(ind);
		
		try {
			if ( line.hasOption( fc ) ) {
				if(line.getOptionValue(fc).equals("--cmd")){displayCmds(fc);System.exit(1);}
				if ( f != null )
					f.set(o,Boolean.parseBoolean(line.getOptionValue(fc)));
				else if ( smethod != null )
					smethod.invoke(o,Boolean.parseBoolean(line.getOptionValue(fc)));
				else 
					throw new Exception("no Field or Method to set!");
			}
		} catch(Exception e) {e.printStackTrace();}
	}

    
    
	public Option getOption(){
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc = n.substring(ind);
		Boolean currentValue = null;		
		if (f!=null){
			try{
				currentValue = (Boolean)f.get(o);
			}catch(Exception e){e.printStackTrace();}
			return new Option(fc, true,"-- " + t.description() + " --\n  current value : "+ currentValue);
		}
		else if (gmethod!=null){
			try{
				currentValue = (Boolean)gmethod.invoke(o);
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