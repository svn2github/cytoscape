package org.cytoscape.work.internal.tunables;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.cytoscape.work.Tunable;


/**
 * Commandline handler for a <i>File</i> type of <code>Tunable</code>
 * 
 * @author pasteur
 */
public class FileCLHandler extends AbstractCLHandler{
	
	
	/**
	 * File object whose path will be set by this handler, to modify the original <code>File</code> contained in <code>o</code>
	 */
	private File file;
	
	
	/**
	 * Constructs the <code>CLHandler</code> for a <code>File</code> Object contained in a Field <code>f</code>
	 * 
	 * @param f field that has been annotated
	 * @param o object contained in <code>f</code>
	 * @param t tunable associated to <code>f</code>
	 */
	public FileCLHandler(Field f, Object o, Tunable t){
		super(f,o,t);
		try{
			file = (File)f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	
	/**
	 * Constructs the <code>CLHandler</code> for a <code>File</code> Object managed by <i>get</i> and <i>set</i> methods
	 * @param gmethod method that returns the value of the Object <code>o</code> annotated as a <code>Tunable</code>
	 * @param smethod method that sets a value to the Object <code>o</code> annotated as a <code>Tunable</code>
	 * @param o Object whose value will be set and get by the methods
	 * @param tg <code>Tunable</code> annotations of the Method <code>gmethod</code> annotated as <code>Tunable</code>
	 * @param ts <code>Tunable</code> annotations of the Method <code>smethod</code> annotated as <code>Tunable</code>
	 */
	public FileCLHandler(Method gmethod,Method smethod,Object o,Tunable tg, Tunable ts){
		super(gmethod,smethod,o,tg,ts);
	}
	
	
	
	/**
	 * If options/arguments are well detected for this handler, it applies the argument : 
	 * <p><pre>
	 * <ul>
	 * <li> display some specific informations if the argument is <code>--cmd</code> ,or</li>
	 * <li> set the argument as a path to <code>file</code> and set the File Object <code>o</code> contained in <code>f</code> with <code>file</code>,or</li>
	 * <li> set the argument as a path to <code>file</code> and set <code>file</code> as a parameter for the <code>set</code> Method </li>
	 * </ul>
	 * </pre></p>
	 * 
	 * @param commandline with arguments
	 */
	public void handleLine( CommandLine line ) {
		String n = getName();
		int ind = n.lastIndexOf(".")+1;		
		String fc = n.substring(ind);

		//for(String st : line.getArgs())System.out.println(st);
		
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


	
	
	/**
	 * Create an Option for the Object <code>o</code> contained in <code>f</code> or got from the <code>get</code> Method.
	 * The option has : 
	 * <p><pre>
	 * <ul>
	 * <li> the name of the handler</li>
	 * <li> its description with the current path of the file</li>
	 * </ul>
	 * </pre></p>
	 * @return option of the handler
	 */
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
	
	
	
	/**
	 * Display some detailed informations to the user for this particular handler
	 * @param name of the handler
	 */
	private void displayCmds(String fc){
		HelpFormatter formatter = new HelpFormatter();
		Options options = new Options();
		options.addOption(this.getOption());
		formatter.setWidth(100);
		System.out.println("\n");
		formatter.printHelp("Detailed informations/commands for " + fc + " :", options);
	}

}