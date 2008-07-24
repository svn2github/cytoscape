
package org.cytoscape.work.tunable.impl.props;

import java.lang.reflect.*;
import org.cytoscape.work.tunable.*;
import org.cytoscape.work.Tunable;

public class PropHandlerFactory implements HandlerFactory<PropHandler> {
	public PropHandler getHandler(Field f, Object o, Tunable t) {
		Class type = f.getType();
		if ( type == int.class || type == Integer.class )
			return new IntPropHandler(f,o,t);
		else if ( type == String.class ) 
			return new StringPropHandler(f,o,t);
		else
			return null;
	}
}
