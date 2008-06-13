package org.example;

import org.example.tunable.cl.*;
import org.example.command.*;

public class AppCL
{
	private static Command[] coms = { new PrintSomething() }; // , new PrintSomethingElse() };

    public static void main(String[] args) {
		CLTunableInterceptor.modify(coms, args);
		for ( Command c : coms ) 
			c.execute();
    }

}
