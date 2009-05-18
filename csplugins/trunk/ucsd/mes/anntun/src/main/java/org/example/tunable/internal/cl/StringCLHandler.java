
package org.example.tunable.internal.cl;

import java.lang.reflect.*;

import org.apache.commons.cli.*;
import org.example.tunable.*;

public class StringCLHandler extends AbstractCLHandler {


	public StringCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}

	public StringCLHandler(Method m, Object o, Tunable t) {
		super(m,o,t);
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
				if ( f != null )
					f.set(o,line.getOptionValue(fc) );
				else if ( m != null )
					m.invoke(o,line.getOptionValue(fc));
				else 
					throw new Exception("no Field or Method to set!");
			}
		} catch(Exception e) {e.printStackTrace();}
	}
	
	
	
	public Option getOption() {
		String n = getName();
		//System.out.println("creating option for:    " + n);
		int ind = n.lastIndexOf(".")+1;

		String fc;
		//if(n.substring(ind).length()<3)fc = n.substring(ind); 
		//else fc = n.substring(ind,ind+3);
		fc = n.substring(ind);
		String currentValue = null;
		
		if( f!=null){
			try{
				currentValue = (String)f.get(o);
			}catch(Exception e){e.printStackTrace();}
			return new Option(fc, true,"-- " + t.description()+" --\n current value : "+ currentValue);
		}
		else if(m!=null){
			Type[] types = m.getParameterTypes();
			java.util.List list = new java.util.ArrayList();
			for(int i=0;i<types.length;i++) list.add(i,types[i]);
			return new Option(fc, true,"-- "+ t.description()+" --\n  Method's parameters : "+list);
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
