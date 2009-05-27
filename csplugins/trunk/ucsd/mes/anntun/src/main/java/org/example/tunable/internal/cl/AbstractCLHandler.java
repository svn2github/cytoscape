
package org.example.tunable.internal.cl;

import java.lang.reflect.*;
import org.apache.commons.cli.*;
import org.example.tunable.*;

public abstract class AbstractCLHandler extends AbstractHandler implements CLHandler {

	public AbstractCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}

	public AbstractCLHandler(Method m, Object o, Tunable t) {
		super(m,o,t);
	}
	
	public AbstractCLHandler(Method gmethod, Method smethod, Object o, Tunable tg, Tunable ts){
		super(gmethod,smethod,o,tg,ts);
	}

//	protected String getName() {
//		if ( f != null ) {
//			String ns = f.getDeclaringClass().toString();
//			return ns.substring( ns.lastIndexOf(".")+1) + "." + f.getName();
//		} else if ( m != null ) {
//			String ns = m.getDeclaringClass().toString();
//			return ns.substring( ns.lastIndexOf(".")+1) + "." + m.getName();
//		} else 
//			return "";
//	}

	protected String getName() {
		if ( f!=null ) {
			String ns = f.getDeclaringClass().toString();
			return ns.substring( ns.lastIndexOf(".")+1) + "." + f.getName();
		} else if ( gmethod != null && smethod != null) {
			String ns = smethod.getDeclaringClass().toString();
			return ns.substring( ns.lastIndexOf(".")+1) + "." + "getset" + smethod.getName().substring(3);
		} else
			return "";
	}
	
	
	public abstract void handleLine( CommandLine line );
}
