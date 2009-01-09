
package org.example.tunable.gui;

import java.lang.reflect.*;
import org.example.tunable.*;
import org.example.tunable.util.*;

public class GuiHandlerFactory implements HandlerFactory<GuiHandler> {
	public GuiHandler getHandler(Method m, Object o, Tunable t) {
		return null;
	}

	public GuiHandler getHandler(Field f, Object o, Tunable t) {
		Class type = f.getType();
		if ( type == int.class || type == Integer.class )
			return new IntHandler(f,o,t);
		else if ( type == String.class ) 
			return new StringHandler(f,o,t);
		else if ( type == BoundedInteger.class ) 
			return new BoundedIntegerHandler(f,o,t);
		else if ( type == BoundedDouble.class ) 
			return new BoundedDoubleHandler(f,o,t);
		return null;
	}
}
