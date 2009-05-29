package org.cytoscape.work.internal.props;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.io.File;
import java.net.URL;

import org.cytoscape.work.HandlerFactory;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.*;


public class PropHandlerFactory implements HandlerFactory<PropHandler> {

	public PropHandler getHandler(Field f, Object o, Tunable t) {
		
		Class<?> type = f.getType();
		
		if(type == Boolean.class || type == boolean.class)
			return new BooleanPropHandler(f,o,t);
		else if (type == String.class)
			return new StringPropHandler(f, o, t);

		else if ((type == int.class || type == Integer.class))
			return new IntPropHandler(f, o, t);
		else if ((type == float.class || type == Float.class))
			return new FloatPropHandler(f, o, t);
		else if ((type == long.class || type == Long.class))
			return new LongPropHandler(f, o, t);
		else if(type == Double.class || type == double.class)
			return new DoublePropHandler(f,o,t);
		
		else if(type == BoundedDouble.class)
			return new BoundedPropHandler<BoundedDouble>(f,o,t);
		else if(type == BoundedInteger.class)
			return new BoundedPropHandler<BoundedInteger>(f,o,t);
		else if(type == BoundedLong.class)
			return new BoundedPropHandler<BoundedLong>(f,o,t);
		else if(type == BoundedFloat.class)
			return new BoundedPropHandler<BoundedFloat>(f,o,t);

		else if(type == FlexiblyBoundedDouble.class)
			return new FlexiblyBoundedPropHandler<FlexiblyBoundedDouble>(f,o,t);
		else if(type == FlexiblyBoundedInteger.class)
			return new FlexiblyBoundedPropHandler<FlexiblyBoundedInteger>(f,o,t);
		else if(type == FlexiblyBoundedLong.class)
			return new FlexiblyBoundedPropHandler<FlexiblyBoundedLong>(f,o,t);
		else if(type == FlexiblyBoundedFloat.class)
			return new FlexiblyBoundedPropHandler<FlexiblyBoundedFloat>(f,o,t);
		
		
		else if (type == ListSingleSelection.class)
			return new ListSinglePropHandler(f,o,t);
		else if (type == ListMultipleSelection.class)
			return new ListMultiplePropHandler(f,o,t);
		
		else if (type == File.class)
			return new FilePropHandler(f,o,t);
		else if(type == URL.class)
			return new URLPropHandler(f,o,t);
		return null;
	}

	public PropHandler getHandler(Method m, Object o, Tunable t) {
		return null;
	}

}
