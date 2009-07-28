package org.cytoscape.work.internal.tunables;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.cytoscape.work.AbstractTunableInterceptor;
import org.cytoscape.cmdline.launcher.CommandLineProvider;


public class CLTunableInterceptor extends AbstractTunableInterceptor<CLHandler>{

	private String[] args;
	CommandLineProvider clp;
	
	public CLTunableInterceptor(CommandLineProvider clp ) {
		super(new CLHandlerFactory());
		this.clp = clp;
		this.args = clp.getCommandLineCompleteArgs();
	}

	public boolean createUI(Object ... objs) {

		List<CLHandler> lh = new ArrayList<CLHandler>();
		
		for (Object o : objs ) { 

			if ( !handlerMap.containsKey(o) )
				throw new IllegalArgumentException("Interceptor does not yet know about this object!");
		
			lh.addAll(handlerMap.get(o).values());
		}

		
		//to get the right arguments from the parser
		this.args = clp.getSpecificArgs();		
		
		//create the options for all the handlers
		Options options = new Options();
		for ( CLHandler h : lh )
			options.addOption( h.getOption() );
		//add an "Help" option
		options.addOption("H", "fullHelp", false, "Display all the available Commands for this Task");

		if(lh.size()==0)args = new String[0];
		//for(String st : args)System.out.println(st);
		
		
		
        //Try to parse the command line
        CommandLineParser parser = new PosixParser();
        CommandLine line = null;
        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Parsing command line failed: " + e.getMessage()+"\n");
			printHelp(options,"Error in arguments of "+ objs[0].getClass().getSimpleName() + " -->  see options below",objs[0].getClass().getSimpleName());
            System.exit(1);
        }

        
        //Print the Help if -H is requested or if there is an error of parsing
        if (line.hasOption("H")) {
        	printHelp(options,"For the arguments of "+ objs[0].getClass().getSimpleName() + " -->  see options below",objs[0].getClass().getSimpleName());
			System.exit(0);
        }

        //Set the new tunables with the arguments parsed for options
		for ( CLHandler h : lh )
			h.handleLine( line );
				
		return true;
	}

	private static void printHelp(Options options,String info,String taskName) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(110);
		System.out.println(info);
		formatter.printHelp("\njava -Xmx512M -jar headless-cytoscape.jar -"+ taskName + " -<option> -<arg>","\noptions:", options,"\nTip : run \"java -jar headless-cytoscape.jar -<task> -<option> --cmd\" to get detailed help on this option");
	}


	public boolean handle(){return false;}
	public void setParent(Object o) {}
}
