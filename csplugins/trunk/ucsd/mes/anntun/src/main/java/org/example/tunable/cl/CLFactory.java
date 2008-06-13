
package org.example.tunable.cl;

import java.lang.reflect.*;
import org.example.tunable.*;

public interface CLFactory {
	public CLHandler getHandler(Field f, Object o, Tunable t);
}
