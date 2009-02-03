
package org.example.tunable;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

/**
 * Allows an object to listen to changes in a Handler.
 */
public interface HandlerListener { 
	void handlerChanged(Handler otherHandler);
}
