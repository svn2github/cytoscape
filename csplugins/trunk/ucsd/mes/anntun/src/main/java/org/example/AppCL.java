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

		// create the interceptor for this context
		// in this case it's a command line, so it takes the
		// args from main()
		TunableInterceptor cl = new CLTunableInterceptor(args);

		// intercept the command and modify any tunables
		cl.intercept(com);

		// execute the command
		System.out.println("result of command execution:");
		com.execute();
		System.out.println();

		// a properties object generated from someplace
		Properties p = new Properties();
		p.setProperty("printSomething.firstName","marge");

		// create the interceptor 
		TunableInterceptor lp = new LoadPropsInterceptor(p);

		// intercept the command and set any fields identified by
		// property names with values from the props file 
		lp.intercept(com);

		// just to see what has been set
		System.out.println("result of command execution after properties have been loaded:");
		com.execute();
		System.out.println();

		// a properties object generated from someplace..
		Properties store = new Properties();

		// now load the properties into the appropriate tunables
		TunableInterceptor sp = new StorePropsInterceptor(store);
		sp.intercept(com);

		System.out.println("result of storing properties interceptor:");
		System.out.println(store.toString());
		System.out.println();

    }
}
