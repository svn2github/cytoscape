
package org.example.tunable;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

/**
 * An interface that provides access to the {@link Field} or {@link Method} 
 * being annotated, the {@link Object} being annotated, and {@link Tunable}
 * annotation itself.  If a {@link Field} is annotated, expect the 
 * getMethod() method to return null, and vice versa.
 */
public interface Handler { 
	Field getField();
	Method getMethod();
	Object getObject();
	Tunable getTunable();
	void handlerChanged(Handler otherHandler);
	void addDependentHandler(Handler otherHandler);
	boolean removeDependentHandler(Handler otherHandler); 
}
