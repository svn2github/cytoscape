package org.example;


import org.example.tunable.*;
import org.example.tunable.internal.props.*;
import org.example.tunable.internal.cl.*;
import org.example.tunable.util.BoundedDouble;
import org.example.tunable.util.FlexiblyBoundedInteger;
import org.example.tunable.util.ListSingleSelection;
import org.example.command.*;

import java.util.*;

public class AppCL
{
    public static void main(String[] args) {

		// command comes from someplace
		Command com = new PrintSomething();
		
		
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
		Properties load = new Properties();
		load.setProperty("PrintSomething.firstName","marge");
		load.setProperty("PrintSomething.footSize","5.34");
		load.setProperty("PrintSomething.bool", "true");
		load.setProperty("PrintSomething.lss","1");
		load.setProperty("PrintSomething.income","0,3444,10000,true,true");
		load.setProperty("PrintSomething.lms", "one,three");
		
		// create the interceptor 
		TunableInterceptor lp = new LoadPropsInterceptor(load);

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
