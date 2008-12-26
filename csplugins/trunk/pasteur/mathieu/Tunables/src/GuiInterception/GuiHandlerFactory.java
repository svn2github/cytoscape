package GuiInterception;

import Factory.*;
import HandlerFactory.HandlerFactory;
import java.lang.reflect.*;
import java.security.acl.Group;
import Tunable.Tunable;
import Tunable.Tunable.Param;
import Utils.*;

public class GuiHandlerFactory<T> implements HandlerFactory<Guihandler> {


	public Guihandler getHandler(Field f, Object o, Tunable t){
		
		Class<?> type = f.getType();
		
		if(type == Integer.class || type == int.class)
			return new IntegerHandler(f,o,t);//new BoundedHandler<String>(f,o,t);
		if(type == Double.class || type == double.class)
			return new DoubleHandler(f,o,t);//new BoundedHandler<String>(f,o,t);
		if(type == Bounded.class)
			return new BoundedHandler<String>(f,o,t);		
		if(type == Boolean.class)
			return new BooleanHandler(f,o,t);
		if(type == String.class)
			return new StringHandler(f,o,t);
		if(type == Group.class)
			return new GroupHandler(f,o,t);
		if(type == ListSingleSelection.class)
			return new ListSingleHandler<String>(f,o,t);
		if(type == ListMultipleSelection.class)
			return new ListMultipleHandler<String>(f,o,t);		
		if(type == myButton.class)
			return new ButtonHandler(f,o,t);
		return null;
	}
}
