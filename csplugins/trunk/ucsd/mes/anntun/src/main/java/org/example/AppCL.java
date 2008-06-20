package org.example;

import org.example.tunable.*;
import org.example.command.*;

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
		com.execute();
    }
}
