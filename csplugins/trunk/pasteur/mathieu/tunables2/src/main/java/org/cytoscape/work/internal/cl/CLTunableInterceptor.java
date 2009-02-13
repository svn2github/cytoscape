package org.cytoscape.work.internal.cl;

import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.*;

import org.apache.commons.cli.*;
import org.cytoscape.work.AbstractTunableInterceptor;

/**
 * This would presumably be a Service.
 */
public class CLTunableInterceptor extends AbstractTunableInterceptor<CLHandler>{

	private String[] args;
	
	public CLTunableInterceptor(String[] args) {
		super(new CLHandlerFactory());
		this.args = args;
	}

	public int createUI(Object ... objs) {

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

        // try to parse the cmd line
        CommandLineParser parser = new PosixParser();
        CommandLine line = null;

        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Parsing command line failed: " + e.getMessage());
			printHelp(options);
            System.exit(1);
        }

        // use what is found on the command line to set values
        if (line.hasOption("h")) {
			printHelp(options);
			System.exit(0);
        }

		for ( CLHandler h : lh )
			h.handleLine( line );
		return 0;
	}
	
	

	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -Xmx512M -jar cytoscape.jar [OPTIONS]", options);
	}

	@Override
	protected void getResultsPanels(List<CLHandler> handlers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void processProps(List<CLHandler> handlers) {
		// TODO Auto-generated method stub
		
	}

	public void createProperties(Object... obs) {
		// TODO Auto-generated method stub
		
	}
}
