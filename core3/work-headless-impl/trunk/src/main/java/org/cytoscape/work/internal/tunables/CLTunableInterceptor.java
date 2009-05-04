package org.cytoscape.work.internal.tunables;

import java.util.*;
import org.apache.commons.cli.*;
import org.cytoscape.work.AbstractTunableInterceptor;




public class CLTunableInterceptor extends AbstractTunableInterceptor<CLHandler>{

	private String[] args;
	
	public CLTunableInterceptor(String[] args) {
		super(new CLHandlerFactory());
		this.args = args;
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
		options.addOption("h", "help", false, "Print this message.");

		
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
        if (line.hasOption("h")) {
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
		formatter.printHelp("java -Xmx512M -jar cytoscape.jar [OPTIONS]", options);
	}


	public void handle(){}
	public void setParent(Object o) {}
}
