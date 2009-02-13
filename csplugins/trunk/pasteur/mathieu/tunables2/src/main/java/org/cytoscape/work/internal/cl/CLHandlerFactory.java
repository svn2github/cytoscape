
package org.cytoscape.work.internal.cl;

import java.lang.reflect.*;
import java.util.*;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.internal.gui.HandlerFactory;


public class CLHandlerFactory implements HandlerFactory<CLHandler> {

	public CLHandler getHandler(Method m, Object o, Tunable t) {
		Class<?>[] types = m.getParameterTypes();
		if ( types.length != 1 ) {
			System.err.println("found bad method");
			return null;
		}
		Class<?> type = types[0];

		if ( type == int.class || type == Integer.class )
			return new IntCLHandler(m,o,t);
		else if ( type == String.class )
			return new StringCLHandler(m,o,t);
		else if ( type == boolean.class || type == Boolean.class )
			return new BooleanCLHandler(m,o,t);
		else 
			return null;

	}

	public CLHandler getHandler(Field f, Object o, Tunable t) {
		Class type = f.getType();

		if ( type == int.class || type == Integer.class )
			return new IntCLHandler(f,o,t);
		else if ( type == String.class )
			return new StringCLHandler(f,o,t);
		else if ( type == boolean.class || type == Boolean.class )
			return new BooleanCLHandler(f,o,t);
		else if( type == double.class || type == Double.class)
			return new DoubleCLHandler(f,o,t);
		else if( type == float.class || type == Float.class)
			return new FloatCLHandler(f,o,t);
		else if( type == long.class || type == Long.class)
			return new LongCLHandler(f,o,t);
		else 
			return null;
	}
}

