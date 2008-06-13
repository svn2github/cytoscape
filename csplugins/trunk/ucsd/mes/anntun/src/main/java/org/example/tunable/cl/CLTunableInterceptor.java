package org.example.tunable.cl;

import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.*;
import org.example.command.Command;
import org.apache.commons.cli.*;
import org.example.tunable.*;

public class CLTunableInterceptor {

	public static void modify(Command[] commands, String[] args) {
		for ( Command d : commands ) {

			java.util.List<CLHandler> lh = new LinkedList<CLHandler>();

			// Find each field in the class.
			for (Field f : d.getClass().getFields()) {

				// See if the field is annotated as a Tunable.
   				if (f.isAnnotationPresent(Tunable.class)) {
					try {
						Tunable a = f.getAnnotation(Tunable.class);
						//System.out.println("We're modifying Tunable:  " + f.getName() + 
						 //                  " : " + a.description());

						CLFactory h = handlers.get(f.getType());
						if ( h != null )
						 	lh.add( h.getHandler(f,d,a) );	
						else
							System.out.println("No handler for type: " + f.getType().getName());

					} catch (Throwable ex) {
						System.out.println("Modification failed: " + f.toString() );
						ex.printStackTrace();
					}
				}
			}

			parseCommandLine( lh, args );
		}
	}

	private static Map<Class,CLFactory> handlers; 
	
	static {
		handlers = new HashMap<Class,CLFactory>();
		handlers.put( int.class, new IntCLFactory() );
		handlers.put( String.class, new StringCLFactory() );
	}

	private static void parseCommandLine(java.util.List<CLHandler> lh, String[] args ) {
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
	}

	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -Xmx512M -jar cytoscape.jar [OPTIONS]", options);
	}
}
