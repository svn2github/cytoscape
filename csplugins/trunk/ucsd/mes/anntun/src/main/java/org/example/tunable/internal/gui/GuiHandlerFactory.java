
package org.example.tunable.internal.gui;

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
			return new BoundedHandler<BoundedInteger>(f,o,t);

		else if ( type == BoundedDouble.class ) 
			return new BoundedHandler<BoundedDouble>(f,o,t);

		else if ( type == FlexiblyBoundedInteger.class ) 
			return new FlexiblyBoundedHandler<FlexiblyBoundedInteger>(f,o,t);

		else if ( type == FlexiblyBoundedDouble.class ) 
			return new FlexiblyBoundedHandler<FlexiblyBoundedDouble>(f,o,t);

		else if ( type == Boolean.class || type == boolean.class ) 
			return new BooleanHandler(f,o,t);

		else if ( type == ListSingleSelection.class ) 
			return new ListSingleSelectionHandler(f,o,t);

		else if ( type == ListMultipleSelection.class ) 
			return new ListMultipleSelectionHandler(f,o,t);

		return null;
	}
}
