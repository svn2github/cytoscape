package org.cytoscape;


import java.util.*;

import org.cytoscape.command.Command;
import org.cytoscape.command.PrintSomething;
import org.cytoscape.work.HandlerController;
import org.cytoscape.work.TunableInterceptor;
import org.cytoscape.work.internal.cl.CLTunableInterceptor;
import org.cytoscape.work.internal.props.LoadPropsInterceptor;
import org.cytoscape.work.internal.props.StorePropsInterceptor;


public class applicationCL
{
    @SuppressWarnings("unchecked")
	public static void main(String[] args) {

		// command comes from someplace
		Command com = new PrintSomething();

		// create the interceptor for this context
		// in this case it's a command line, so it takes the
		// args from main()
		Properties p= new Properties();
		Properties store = new Properties();
		TunableInterceptor cli = new CLTunableInterceptor(args);
		TunableInterceptor lpi = new LoadPropsInterceptor(p);
		TunableInterceptor spi = new StorePropsInterceptor(store);
		
		
		lpi.loadTunables(com);
		lpi.createProperties(com);
		System.out.println("InputProperties of "+com.getClass().getSimpleName()+ " = "+ p);
		System.out.println();
		// load the tunables from the object
		cli.loadTunables(com);

		// if the object implements the interface,
		// give the object access to the handlers
		// created for the tunables
		if ( com instanceof HandlerController )
			((HandlerController)com).controlHandlers(cli.getHandlers(com));
		
		// create the UI based on the object
		cli.createUI(com);

		// execute the command
		System.out.println();
		System.out.println("result of command execution:");
		com.execute();
		System.out.println();

		spi.loadTunables(com);spi.createProperties(com);
	
		System.out.println("result of storing properties interceptor:");
		System.out.println(store.toString());
		System.out.println();

    }
}
