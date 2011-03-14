
package org.cytoscape.command.internal.tunables;



import org.cytoscape.work.TunableHandler;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.AbstractTunableHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractStringTunableHandler extends AbstractTunableHandler implements StringTunableHandler {

	private static final Logger logger = LoggerFactory.getLogger(IntTunableHandler.class);

	protected String[] args;

	public AbstractStringTunableHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}

	public AbstractStringTunableHandler(Method get, Method set, Object o, Tunable t) {
		super(get,set,o,t);
	}

	public void handle() {
		try {
		for ( int i = 0; i < args.length; i++ ) {
			String arg = args[i];
			if ( arg.equals(getName()) && (i+1) < args.length ) {
				Object value;
				try {
					value = processArg(args[i+1]); 
				} catch (Exception e) {
					logger.warn("Couldn't parse value from: " + args[i+1], e);
					return;
				}
				setValue(value);
				return;
			}
		}
		logger.warn("found no match for tunable: " + getQualifiedName());
		} catch ( Exception e) {
			logger.warn("tunable handler exception: " + getQualifiedName(), e);
		}
	}

	public void setArgString(String s) {
		if ( s != null )
			args = s.split("\\s+");
	}

	public abstract Object processArg(String arg) throws Exception;
}
