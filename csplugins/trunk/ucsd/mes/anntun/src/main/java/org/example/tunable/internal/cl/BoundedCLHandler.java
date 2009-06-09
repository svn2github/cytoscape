package org.example.tunable.internal.cl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.example.tunable.Tunable;
import org.example.tunable.util.AbstractBounded;

public class BoundedCLHandler<T extends AbstractBounded<?>> extends AbstractCLHandler {

	private T bo;
	private String lbound="\u2264";
	private String ubound="\u2264";


	public BoundedCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}

	public BoundedCLHandler(Method gmethod, Method smethod, Object o, Tunable tg, Tunable ts) {
		super(gmethod,smethod,o,tg,ts);
	}

	
	public void handleLine( CommandLine line ) {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc;
		fc = n.substring(ind);
		
		
		try {
			if ( line.hasOption( fc ) ) {
				if(line.getOptionValue(fc).equals("--cmd")){displayCmds(fc);System.exit(1);}
				if ( f != null ){
					bo.setValue(line.getOptionValue(fc));
					f.set(o,bo);}
				else if (smethod != null && gmethod!=null){
					bo = (T)gmethod.invoke(o);
					bo.setValue(line.getOptionValue(fc).toString());
					smethod.invoke(o,bo);
				}
				else 
					throw new Exception("no Field or Method to set!");
			}
		} catch(Exception e) {e.printStackTrace();}
	}
	
	
	@SuppressWarnings("unchecked")
	public Option getOption() {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc = n.substring(ind);
	
		T currentValue = null;
		if( f!=null){
			try{
				bo = (T) f.get(o);
			}catch (Exception e){e.printStackTrace();}
			if(bo.isLowerBoundStrict())lbound="<";
			if(bo.isUpperBoundStrict())ubound="<";
			return new Option(fc, true,"-- " +t.description() +" --\n  current value : "+bo.getValue()+ "\n  possible value : (" + bo.getLowerBound()+ " " + lbound + " x " + ubound + " " + bo.getUpperBound() + ")");		
		}
		else if(gmethod!=null){
			try{
				currentValue = (T)gmethod.invoke(o);
			}catch(Exception e){e.printStackTrace();}
			return new Option(fc, true,"-- " +tg.description() +" --\n  current value : "+currentValue.getValue());
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
