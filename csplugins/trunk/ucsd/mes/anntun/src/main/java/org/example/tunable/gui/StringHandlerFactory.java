
package org.example.tunable.gui;

import java.lang.reflect.*;
import org.example.tunable.*;

public class StringHandlerFactory implements HandlerFactory {
	public GuiHandler getHandler(Field f, Object o, Tunable t) {
		return new StringHandler(f, o, t);
	}
}
