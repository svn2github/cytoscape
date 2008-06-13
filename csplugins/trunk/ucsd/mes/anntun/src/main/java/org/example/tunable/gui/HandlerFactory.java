
package org.example.tunable.gui;

import java.lang.reflect.*;
import org.example.tunable.*;

public interface HandlerFactory {
	public GuiHandler getHandler(Field f, Object o, Tunable t);
}
