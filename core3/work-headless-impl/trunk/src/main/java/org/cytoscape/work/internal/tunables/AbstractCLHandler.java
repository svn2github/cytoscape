package org.cytoscape.work.internal.tunables;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.cytoscape.work.AbstractHandler;
import org.cytoscape.work.Tunable;

/**
 * Abstract handler for the creation of the user interface.
 * <br>
 * It provides the functions that are common to all types of Handlers
 */
public abstract class AbstractCLHandler extends AbstractHandler implements CLHandler {

	/**
	 * Constructs an abstract commandline handler for <i>Field</i>
	 * @param f Field that is intercepted
	 * @param o Object that is contained in the Field <code>f</code>
	 * @param t <code>Tunable</code> annotations of the Field <code>f</code> annotated as <code>Tunable</code>
	 */
	protected AbstractCLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}

	
	/**
	 * Constructs an abstract commandline handler for <i>Methods</i>
	 * @param gmethod Method that returns the value from the Object <code>o</code>
	 * @param smethod Method that sets a value to the Object <code>o</code>
	 * @param o Object whose value will be set and get by the methods
	 * @param tg <code>Tunable</code> annotations of the Method <code>gmethod</code> annotated as <code>Tunable</code>
	 * @param ts <code>Tunable</code> annotations of the Method <code>smethod</code> annotated as <code>Tunable</code>
	 */
	protected AbstractCLHandler(Method gmethod, Method smethod, Object o, Tunable tg, Tunable ts){
		super(gmethod,smethod,o,tg,ts);
	}


	/**
	 * To get the name of the <code>CLHandler</code>
	 * @return name of the <code>CLHandler</code>
	 */
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
