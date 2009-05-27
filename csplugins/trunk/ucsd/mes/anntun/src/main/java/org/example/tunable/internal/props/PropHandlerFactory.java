
package org.example.tunable.internal.props;

import java.lang.reflect.*;

import org.example.tunable.*;

public class PropHandlerFactory implements HandlerFactory<PropHandler> {

	public PropHandler getHandler(Method m, Object o, Tunable t) {
		return null;
	}

	public PropHandler getHandler(Field f, Object o, Tunable t) {
		Class type = f.getType();
		if ( type == int.class || type == Integer.class )
			return new IntPropHandler(f,o,t);
		else if ( type == String.class ) 
			return new StringPropHandler(f,o,t);
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
