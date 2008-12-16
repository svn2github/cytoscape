package Props;


import java.lang.reflect.*;

import Tunable.*;
import HandlerFactory.HandlerFactory;


/**
 * DOCUMENT ME!
  */
public class PropHandlerFactory implements HandlerFactory<PropHandler> {
	/**
	 *  DOCUMENT ME!
	 *
	 * @param f DOCUMENT ME!
	 * @param o DOCUMENT ME!
	 * @param t DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public PropHandler getHandler(Field f, Object o, Tunable t) {
		
		Class type = f.getType();

		if ((type == int.class) || (type == Integer.class))
			return new IntPropHandler(f, o, t);
		else if (type == String.class)
			return new StringPropHandler(f, o, t);
		else

			return null;
	}

}
