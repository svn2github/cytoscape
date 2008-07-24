
package org.cytoscape.work.tunable;

import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.List;
import java.util.LinkedList;

import org.cytoscape.work.Tunable;

/**
 * An abstract implementation of {@link TunableInterceptor} that should serve as
 * the super class for almost all implementations of {@link TunableInterceptor}.
 */
public abstract class AbstractTunableInterceptor<T extends Handler> 
	implements TunableInterceptor {

	protected HandlerFactory<T> factory;

	public AbstractTunableInterceptor(HandlerFactory<T> factory) {
		this.factory = factory; 
	}

	/**
	 * Calls the <code>process(List&lt;T&gt; handlers)</code> method once it has identified all
	 * {@link Tunable} fields in the object and created appropriate
	 * handlers for each field. 
	 */
	public final void intercept(Object command) {
		List<T> handlerList = new LinkedList<T>();

		// Find each field in the class.
		for (Field field : command.getClass().getDeclaredFields()) {

			// See if the field is annotated as a Tunable.
   			if (field.isAnnotationPresent(Tunable.class)) {
				try {
					Tunable tunable = field.getAnnotation(Tunable.class);
					field.setAccessible(true);
					
					// Get a handler for this particular field type and
					// add it to the list.
					T handler = factory.getHandler(field,command,tunable);

					if ( handler != null )
					 	handlerList.add( handler ); 
					else
						System.out.println("No handler for type: " + field.getType().getName());

				} catch (Throwable ex) {
					System.out.println("tunable intercept failed: " + field.toString() );
					ex.printStackTrace();
				}
			}
		}

		process(handlerList);
	}

	/** 
	 * This method gets executed by the <code>intercept(Object c)</code> method after all 
	 * {@link Tunable}s have been extracted. This should NOT be called otherwise!
	 */
	protected abstract void process(List<T> handlers);
}
