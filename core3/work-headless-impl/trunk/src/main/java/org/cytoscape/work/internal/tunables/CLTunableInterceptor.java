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
	
	public CLTunableInterceptor(CommandLineProvider clp ) {
		super(new CLHandlerFactory());
		this.args = clp.getCommandLineArgs();
	}

	public boolean createUI(Object ... objs) {

		List<CLHandler> lh = new ArrayList<CLHandler>();

		for (Object o : objs ) { 

			if ( !handlerMap.containsKey(o) )
				throw new IllegalArgumentException("Interceptor does not yet know about this object!");
		
			lh.addAll(handlerMap.get(o).values());
		}

		Options options = new Options();

		for ( CLHandler h : lh )
			options.addOption( h.getOption() );
//		options.addOption("h", "help", false, "Print this message.");
		options.addOption("H", "fullHelp", false, "Display all the available Commands");

		
        //Try to parse the command line
        CommandLineParser parser = new PosixParser();
        CommandLine line = null;
        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Parsing command line failed: " + e.getMessage());
			printHelp(options);
            System.exit(1);
        }

        
        //Print the Help if -h is requested
        if (line.hasOption("H")) {
        	System.out.println("The Help for "+ objs[0].getClass().getSimpleName()+" has been called");
			printHelp(options);
			System.exit(0);
        }
        
        //Set the new tunables with the arguments parsed for options
		for ( CLHandler h : lh )
			h.handleLine( line );
				
		return false;
	}

	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(100);
		formatter.printHelp("\njava -Xmx512M -jar cytoscape.jar [Options]","\nOptions", options,"\nRun : \"java -jar anntun.jar <command> --cmd\" to get detailed help on each command");
	}


	public void handle(){}
	public void setParent(Object o) {}
}
