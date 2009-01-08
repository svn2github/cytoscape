package Props;


import java.lang.reflect.*;

import Tunable.*;
import Utils.Bounded;
import Utils.ListMultipleSelection;
import Utils.ListSingleSelection;
import Utils.myButton;
import HandlerFactory.HandlerFactory;


public class PropHandlerFactory<T> implements HandlerFactory<PropHandler> {

	public PropHandler getHandler(Field f, Object o, Tunable t) {
		
		Class<?> type = f.getType();

		if ((type == int.class || type == Integer.class))
			return new IntPropHandler(f, o, t);
		else if(type == Bounded.class)
			return new BoundedPropHandler(f,o,t);
		else if(type == Double.class || type == double.class)
			return new DoublePropHandler(f,o,t);
		else if (type == String.class)
			return new StringPropHandler(f, o, t);
		else if(type == Boolean.class)
			return new BooleanPropHandler(f,o,t);
		else if (type == ListSingleSelection.class)
			return new ListSinglePropHandler<T>(f,o,t);
		else if (type == ListMultipleSelection.class)
			return new ListMultiplePropHandler<T>(f,o,t);
		else if (type == myButton.class)
			return new ButtonPropHandler(f,o,t);

		return null;
	}

}
