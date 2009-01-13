package GuiInterception;

import Factory.*;
import HandlerFactory.HandlerFactory;
import java.lang.reflect.Field;
import Tunable.Tunable;
import Utils.*;

public class GuiHandlerFactory<T> implements HandlerFactory<Guihandler> {

	public Guihandler getHandler(Field f, Object o, Tunable t){
		
		Class<?> type = f.getType();
		
		if(type == Integer.class || type == int.class)
			return new IntegerHandler(f,o,t);
		if(type == Double.class || type == double.class)
			return new DoubleHandler(f,o,t);
		if(type == Float.class || type == float.class)
			return new FloatHandler(f,o,t);
		if(type == Long.class || type == long.class)
			return new LongHandler(f,o,t);		
		if(type == BoundedDouble.class)
			return new BoundedDoubleHandler(f,o,t);
		if(type == BoundedInteger.class)
			return new BoundedIntegerHandler(f,o,t);
		if(type == Boolean.class)
			return new BooleanHandler(f,o,t);
		if(type == String.class)
			return new StringHandler(f,o,t);
		if(type == ListSingleSelection.class)
			return new ListSingleHandler<String>(f,o,t);
		if(type == ListMultipleSelection.class)
			return new ListMultipleHandler<String>(f,o,t);		
		if(type == myButton.class)
			return new ButtonHandler(f,o,t);
		return null;
	}
}
