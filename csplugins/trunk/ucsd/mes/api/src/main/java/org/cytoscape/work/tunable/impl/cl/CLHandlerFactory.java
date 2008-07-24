
package org.cytoscape.work.tunable.impl.cl;

import java.lang.reflect.*;
import java.util.*;
import org.cytoscape.work.tunable.*;
import org.cytoscape.work.Tunable;

public class CLHandlerFactory implements HandlerFactory<CLHandler> {

	public CLHandler getHandler(Field f, Object o, Tunable t) {
		Class type = f.getType();

		if ( type == int.class || type == Integer.class )
			return new IntCLHandler(f,o,t);
		else if ( type == String.class )
			return new StringCLHandler(f,o,t);
		else 
			return null;
	}
}

