package org.cytoscape.work.internal.props;

import java.lang.reflect.Field;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.internal.gui.HandlerFactory;
import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.util.BoundedFloat;
import org.cytoscape.work.util.BoundedInteger;
import org.cytoscape.work.util.BoundedLong;
import org.cytoscape.work.util.ListMultipleSelection;
import org.cytoscape.work.util.ListSingleSelection;
import org.cytoscape.work.util.myFile;

public class PropHandlerFactory<T> implements HandlerFactory<PropHandler> {

	public PropHandler getHandler(Field f, Object o, Tunable t) {
		
		Class<?> type = f.getType();
		
		if ((type == int.class || type == Integer.class))
			return new IntPropHandler(f, o, t);
		else if ((type == float.class || type == Float.class))
			return new FloatPropHandler(f, o, t);
		else if ((type == long.class || type == Long.class))
			return new LongPropHandler(f, o, t);
		else if(type == BoundedDouble.class)
			return new BoundedDoublePropHandler(f,o,t);
		else if(type == BoundedInteger.class)
			return new BoundedIntegerPropHandler(f,o,t);
		else if(type == BoundedLong.class)
			return new BoundedLongPropHandler(f,o,t);
		else if(type == BoundedFloat.class)
			return new BoundedFloatPropHandler(f,o,t);
		else if(type == Double.class || type == double.class)
			return new DoublePropHandler(f,o,t);
		else if (type == String.class)
			return new StringPropHandler(f, o, t);
		else if(type == Boolean.class || type == boolean.class)
			return new BooleanPropHandler(f,o,t);
		else if (type == ListSingleSelection.class)
			return new ListSinglePropHandler<T>(f,o,t);
		else if (type == ListMultipleSelection.class)
			return new ListMultiplePropHandler<T>(f,o,t);
		else if (type == myFile.class)
			return new FilePropHandler(f,o,t);
//		else if(type == myURL.class)
//			return new URLPropHandler(f,o,t);
		return null;
	}

}
