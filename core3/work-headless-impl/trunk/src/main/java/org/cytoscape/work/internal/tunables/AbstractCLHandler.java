package org.cytoscape.work.internal.tunables;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.cytoscape.work.AbstractHandler;
import org.cytoscape.work.Tunable;


public abstract class AbstractCLHandler extends AbstractHandler implements CLHandler {

	public AbstractCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}

	public AbstractCLHandler(Method gmethod, Method smethod, Object o, Tunable tg, Tunable ts){
		super(gmethod,smethod,o,tg,ts);
	}

	
//	public AbstractCLHandler(Method m, Object o, Tunable t) {
//		super(m,o,t);
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
	

	public abstract Option getOption();
	public abstract void handleLine( CommandLine line );
}
