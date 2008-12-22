package Props;


import java.lang.reflect.*;

import Tunable.*;
import Utils.Bounded;
import HandlerFactory.HandlerFactory;


public class PropHandlerFactory implements HandlerFactory<PropHandler> {

	public PropHandler getHandler(Field f, Object o, Tunable t) {
		
		Class<?> type = f.getType();

		if ((type == int.class) || (type == Integer.class))
			return new IntPropHandler(f, o, t);
		else if (type == String.class)
			return new StringPropHandler(f, o, t);
		else if(type==Bounded.class)
			return new BoundedPropHandler(f,o,t);
		else if(type==Double.class)
			return new DoublePropHandler(f,o,t);
		else if(type==Boolean.class)
			return new BooleanPropHandler(f,o,t);
		else

			return null;
	}

}
