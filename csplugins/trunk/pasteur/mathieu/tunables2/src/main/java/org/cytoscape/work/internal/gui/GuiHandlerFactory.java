package org.cytoscape.work.internal.gui;

import java.lang.reflect.Field;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.util.BoundedFloat;
import org.cytoscape.work.util.BoundedInteger;
import org.cytoscape.work.util.BoundedLong;
import org.cytoscape.work.util.ListMultipleSelection;
import org.cytoscape.work.util.ListSingleSelection;
import org.cytoscape.work.util.myFile;

public class GuiHandlerFactory<T> implements HandlerFactory<Guihandler> {

	public Guihandler getHandler(Field f, Object o, Tunable t){
		
		Class<?> type = f.getType();
		
		if(type == Integer.class || type == int.class)
			return new IntegerHandler(f,o,t);
		else if(type == Double.class || type == double.class)
			return new DoubleHandler(f,o,t);
		else if(type == Float.class || type == float.class)
			return new FloatHandler(f,o,t);
		else if(type == Long.class || type == long.class)
			return new LongHandler(f,o,t);	
		else if(type == BoundedDouble.class)
			return new BoundedDoubleHandler(f,o,t);
		else if(type == BoundedInteger.class)
			return new BoundedIntegerHandler(f,o,t);
		else if(type == BoundedFloat.class)
			return new BoundedFloatHandler(f,o,t);
		else if(type == BoundedLong.class)
			return new BoundedLongHandler(f,o,t);
		else if(type == Boolean.class || type == boolean.class)
			return new BooleanHandler(f,o,t);
		else if(type == String.class)
			return new StringHandler(f,o,t);
		else if(type == ListSingleSelection.class)
			return new ListSingleHandler<String>(f,o,t);
		else if(type == ListMultipleSelection.class)
			return new ListMultipleHandler<String>(f,o,t);
		else if(type == myFile.class)
			return new FileHandler(f,o,t);
//		else if(type == myURL.class)
//			return new URLHandler(f,o,t);
		return null;
	}
}
