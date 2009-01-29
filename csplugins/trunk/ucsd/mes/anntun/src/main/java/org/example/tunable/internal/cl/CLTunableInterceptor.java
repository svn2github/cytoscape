package org.example.tunable.internal.cl;

import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.*;
import org.apache.commons.cli.*;
import org.example.tunable.*;

/**
 * This would presumably be a Service.
 */
public class CLTunableInterceptor extends AbstractTunableInterceptor<CLHandler>{

	private String[] args;
	
	public CLTunableInterceptor(String[] args) {
		super(new CLHandlerFactory());
		this.args = args;
	}

	public void createUI(Object o) {

		if ( !handlerMap.containsKey(o) )
			throw new IllegalArgumentException("Interceptor does not yet know about this object!");
		
		Options options = new Options();

		Collection<CLHandler> lh = handlerMap.get(o).values();

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
	}

	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -Xmx512M -jar cytoscape.jar [OPTIONS]", options);
	}
}
