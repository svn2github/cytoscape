package org.cytoscape.work.internal.tunables;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.cytoscape.work.Tunable;



public class URLCLHandler extends AbstractCLHandler implements CLHandler{

	URL url;
	
	public URLCLHandler(Field f, Object o, Tunable t) {
		super(f, o, t);
		try{
			this.url = (URL) f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}

	public URLCLHandler(Method gmethod,Method smethod,Object o,Tunable tg, Tunable ts){
		super(gmethod,smethod,o,tg,ts);
	}
	
	
	public Option getOption() {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc = n.substring(ind);
		
		URL currentValue = null;
		
		if(f!=null){
			if(url!=null)
				return new Option(fc, true,"-- " + t.description() + " --\n  current url : " + url.getPath());
			else
				return new Option(fc, true,"-- " + t.description() + " --\n  current url : " + "");				
		}
		else if(gmethod!=null){
			try{
				currentValue = (URL)gmethod.invoke(o);
			}catch(Exception e){e.printStackTrace();}
			return new Option(fc, true,"-- "+tg.description() +" --\n  current selected values : "+currentValue.toString());
		}
		else
			return null;
	}

	public void handleLine(CommandLine line) {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;		
		String fc = n.substring(ind);

		for(String st : line.getArgs())System.out.println(st);
		
		try {
		if ( line.hasOption( fc ) ) {
			if(line.getOptionValue(fc).equals("--cmd")){displayCmds(fc);System.exit(1);}
			if ( f != null ){
				url = new URL(line.getOptionValue(fc));
				f.set(o,url);
			}
			else if ( smethod != null ){
				url = new URL(line.getOptionValue(fc));
				smethod.invoke(o,url);
			}
			else 
				throw new Exception("no Field or Method to set!");
		}
		} catch(Exception e) {e.printStackTrace();}
		
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