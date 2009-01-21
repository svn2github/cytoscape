
package org.example.tunable;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

/**
 * A place holder interface that identifies the code
 * that will set the {@link Tunable} {@link Field} in 
 * the {Object} based on whatever input this Handler
 * is able to generate.
 */
public interface Handler { 
	Field getField();
	Method getMethod();
	Object getObject();
	Tunable getTunable();
}
