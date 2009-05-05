package org.example;

import org.example.tunable.*;
import org.example.tunable.internal.props.*;
import org.example.tunable.internal.cl.*;
import org.example.command.*;

import java.util.*;

public class AppCL
{
    public static void main(String[] args) {

		// command comes from someplace
		Command com = new PrintSomething();
//		Command com = new TunableSampler();

		// create the interceptor for this context
		// in this case it's a command line, so it takes the
		// args from main()
		TunableInterceptor cl = new CLTunableInterceptor(args);

		// load the tunables from the object
		cl.loadTunables(com);

		
		
//		// if the object implements the interface,
//		// give the object access to the handlers
//		// created for the tunables
//		if ( com instanceof HandlerController )
//			((HandlerController)com).controlHandlers(cl.getHandlers(com));

		
		
		// create the UI based on the object
		cl.createUI(com);

		// execute the command
		System.out.println("\n"+"result of command execution:");
		com.execute();
		System.out.println();

		// a properties object generated from someplace
		Properties p = new Properties();
		p.setProperty("printSomething.firstName","marge");

		// create the interceptor 
		TunableInterceptor lp = new LoadPropsInterceptor(p);

		// load the tunables from the object
		lp.loadTunables(com);
		
		
		
//		// if the object implements the interface,
//		// give the object access to the handlers
//		// created for the tunables
//		if ( com instanceof HandlerController )
//			((HandlerController)com).controlHandlers(lp.getHandlers(com));
		
		
		
		// create the UI based on the object
		lp.createUI(com);

		// just to see what has been set
		System.out.println("result of command execution after properties have been loaded:");
		com.execute();
		System.out.println();
		
		// a properties object generated from someplace..
		Properties store = new Properties();

		// now load the properties into the appropriate tunables
		TunableInterceptor sp = new StorePropsInterceptor(store);
		// load the tunables from the object
		sp.loadTunables(com);


		
//		// if the object implements the interface,
//		// give the object access to the handlers
//		// created for the tunables
//		if ( com instanceof HandlerController )
//			((HandlerController)com).controlHandlers(sp.getHandlers(com));
		

		
		// create the UI based on the object
		sp.createUI(com);

		System.out.println("result of storing properties interceptor:");
		System.out.println(store.toString());
		System.out.println();

    }
}
