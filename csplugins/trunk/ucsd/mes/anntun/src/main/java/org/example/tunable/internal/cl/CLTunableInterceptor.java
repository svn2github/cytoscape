package org.example.tunable.internal.cl;


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

	public void createUI(Object ... objs) {

		List<CLHandler> lh = new ArrayList<CLHandler>();

		for (Object o : objs ) { 

			if ( !handlerMap.containsKey(o) )
				throw new IllegalArgumentException("Interceptor does not yet know about this object!");
		
			lh.addAll(handlerMap.get(o).values());
		}

		Options options = new Options();

		for ( CLHandler h : lh )
			options.addOption( h.getOption() );

		options.addOption("H", "fullHelp", false, "Display all the available Commands");


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
        if (line.hasOption("H")) {
        	System.out.println("The Help for "+ objs[0].getClass().getSimpleName()+" has been called");
			printHelp(options);
			System.exit(0);
        }
        
        
		for ( CLHandler h : lh )
			h.handleLine( line );
	}

	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(100);
		formatter.printHelp("\njava -Xmx512M -jar cytoscape.jar [Options]","\nOptions", options,"\nRun : \"java -jar anntun.jar <command> --cmd\" to get detailed help on each command");
	}
}
