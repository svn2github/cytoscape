
package org.example.tunable.gui;

import java.lang.reflect.*;
import org.example.tunable.*;

public class GuiHandlerFactory implements HandlerFactory<GuiHandler> {
	public GuiHandler getHandler(Field f, Object o, Tunable t) {
		Class type = f.getType();
		if ( type == int.class || type == Integer.class )
			return new IntHandler(f,o,t);
		else if ( type == String.class ) 
			return new StringHandler(f,o,t);
		else
			return null;
	}
}
