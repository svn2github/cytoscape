
package org.example.tunable.internal.props;

import java.lang.reflect.*;

import org.example.tunable.*;
import org.example.tunable.util.BoundedDouble;
import org.example.tunable.util.BoundedInteger;
import org.example.tunable.util.FlexiblyBoundedDouble;
import org.example.tunable.util.FlexiblyBoundedInteger;
import org.example.tunable.util.ListMultipleSelection;
import org.example.tunable.util.ListSingleSelection;

public class PropHandlerFactory implements HandlerFactory<PropHandler> {

	public PropHandler getHandler(Method m, Object o, Tunable t) {
		return null;
	}

	public PropHandler getHandler(Field f, Object o, Tunable t) {
		Class<?> type = f.getType();
		if ( type == int.class || type == Integer.class )
			return new IntPropHandler(f,o,t);
		else if ( type == String.class ) 
			return new StringPropHandler(f,o,t);
		
		//added
		else if ( type == Boolean.class || type == boolean.class)
			return new BooleanPropHandler(f,o,t);
		else if ( type == BoundedDouble.class )
			return new BoundedPropHandler<BoundedDouble>(f,o,t);
		else if ( type == BoundedInteger.class )
			return new BoundedPropHandler<BoundedInteger>(f,o,t);
		else if( type == FlexiblyBoundedInteger.class)
			return new FlexiblyBoundedPropHandler<FlexiblyBoundedInteger>(f,o,t);
		else if( type == FlexiblyBoundedDouble.class)
			return new FlexiblyBoundedPropHandler<FlexiblyBoundedDouble>(f,o,t);
		else if( type == ListSingleSelection.class) // check needed
			return new ListSingleSelectionPropHandler(f,o,t);
		else if( type == ListMultipleSelection.class) // check needed
			return new ListMultipleSelectionPropHandler(f,o,t);
		
		
		else
			return null;
	}

	public PropHandler getHandler(Method gmethod, Method smethod, Object o,
			Tunable t) {
		// TODO Auto-generated method stub
		return null;
	}

	public PropHandler getHandler(Method gmethod, Method smethod, Object o,
			Tunable tg, Tunable ts) {
		// TODO Auto-generated method stub
		return null;
	}
}
