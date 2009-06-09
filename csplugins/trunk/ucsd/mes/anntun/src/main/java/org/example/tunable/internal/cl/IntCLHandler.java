package org.example.tunable.internal.cl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.example.tunable.Tunable;

public class IntCLHandler extends AbstractCLHandler {


	public IntCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}
	
	public IntCLHandler(Method gmethod, Method smethod, Object o, Tunable tg, Tunable ts){
		super(gmethod,smethod,o,tg,ts);
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
				f.set(o,Integer.parseInt(line.getOptionValue(fc)) );
			else if ( smethod != null )
				//m.invoke(o,Integer.parseInt(line.getOptionValue(fc)) );
				smethod.invoke(o,Integer.parseInt(line.getOptionValue(fc)) );
			else 
				throw new Exception("no Field or Method to set!");
		}
		} catch(Exception e) {e.printStackTrace();}
	}
	
	
//	public Option getOption() {
//		String n = getName();
//		int ind = n.lastIndexOf(".")+1;
//		String fc;
//		//if(n.substring(ind).length()<3)fc = n.substring(ind); 
//		//else fc = n.substring(ind,ind+3);
//		fc = n.substring(ind);
//		Integer currentValue = null;		
//		if( f!=null){
//			try{
//				currentValue = (Integer)f.get(o);
//			}catch(Exception e){e.printStackTrace();}
//			return new Option(fc, true,"-- "+ t.description()+" --\n  current value : "+ currentValue);
//		}
//		
//		else if(m!=null){
//			Type[] types = m.getParameterTypes();
//			java.util.List list = new java.util.ArrayList();
//			for(int i=0;i<types.length;i++) list.add(i,types[i]);
//			return new Option(fc,true,"-- "+ t.description()+" --\n Method's parameter : "+list);
//		}
//		else return null;
//	}
	
	public Option getOption(){
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc;
		//if(n.substring(ind).length()<3)fc = n.substring(ind); 
		//else fc = n.substring(ind,ind+3);
		fc = n.substring(ind);
		Integer currentValue = null;		
		if (f!=null){
			try{
				currentValue = (Integer)f.get(o);
			}catch(Exception e){e.printStackTrace();}
			return new Option(fc, true,"-- " + t.description() + " --\n  current value : "+ currentValue);
		}
		else if (gmethod!=null){
			try{
				currentValue = (Integer)gmethod.invoke(o);
			}catch(Exception e){e.printStackTrace();}
			return new Option(fc, true,"-- " + tg.description() + " --\n  current value : "+ currentValue);

			//Original Design
/*			Type[] type = smethod.getParameterTypes();
			java.util.List list = new java.util.ArrayList();
			for(int i=0;i<type.length;i++) list.add(i,type[i]);
			return new Option(fc,true,"-- "+ tg.description() + " / " + ts.description() + " --\n Method's set parameter : "+list);
*/			
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
