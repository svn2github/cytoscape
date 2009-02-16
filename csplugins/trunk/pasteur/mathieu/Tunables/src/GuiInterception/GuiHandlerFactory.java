package GuiInterception;

import java.lang.reflect.Field;

import Factory.BooleanHandler;
import Factory.BoundedDoubleHandler;
import Factory.BoundedFloatHandler;
import Factory.BoundedIntegerHandler;
import Factory.BoundedLongHandler;
import Factory.DoubleHandler;
import Factory.FileHandler;
import Factory.FloatHandler;
import Factory.IntegerHandler;
import Factory.ListMultipleHandler;
import Factory.ListSingleHandler;
import Factory.LongHandler;
import Factory.StringHandler;
import Factory.URLHandler;
import HandlerFactory.HandlerFactory;
import Tunable.Tunable;
import Utils.BoundedDouble;
import Utils.BoundedFloat;
import Utils.BoundedInteger;
import Utils.BoundedLong;
import Utils.ListMultipleSelection;
import Utils.ListSingleSelection;
import Utils.myFile;
import Utils.myURL;

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
		else if(type == myURL.class)
			return new URLHandler(f,o,t);
		return null;
	}
}
