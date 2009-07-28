package org.cytoscape.work.internal.tunables;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.cytoscape.work.Tunable;


public class FileCLHandler extends AbstractCLHandler{
	
	File file;
	
	public FileCLHandler(Field f, Object o, Tunable t){
		super(f,o,t);
		try{
			file = (File)f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public FileCLHandler(Method gmethod,Method smethod,Object o,Tunable tg, Tunable ts){
		super(gmethod,smethod,o,tg,ts);
	}
	
	
	public void handleLine( CommandLine line ) {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;		
		String fc = n.substring(ind);

		for(String st : line.getArgs())System.out.println(st);
		
		try {
			if ( line.hasOption( fc ) ) {
				if(line.getOptionValue(fc).equals("--cmd")){displayCmds(fc);System.exit(1);}
				if ( f != null ){
					file = new File(line.getOptionValue(fc));
					f.set(o,file);
				}
				else if ( smethod != null ){
					file = new File(line.getOptionValue(fc));
					smethod.invoke(o,file);
				}
				else 
					throw new Exception("no Field or Method to set!");
			}
		} catch(Exception e) {e.printStackTrace();}
	}


		
	public Option getOption() {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;
		String fc = n.substring(ind);
		
		File currentValue = null;
		
		if(f!=null){
			if(file!=null)
				return new Option(fc, true,"-- " + t.description() + " --\n  current path file : " + file.getAbsolutePath());
			else
				return new Option(fc, true,"-- " + t.description() + " --\n  current path file : " + "");				
		}
		else if(gmethod!=null){
			try{
				currentValue = (File)gmethod.invoke(o);
			}catch(Exception e){e.printStackTrace();}
			return new Option(fc, true,"-- "+tg.description() +" --\n  current selected values : "+currentValue.getAbsolutePath());
		}
		else
			return null;
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