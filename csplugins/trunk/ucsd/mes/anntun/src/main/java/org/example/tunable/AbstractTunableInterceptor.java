
package org.example.tunable;

import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.List;
import java.util.LinkedList;
import org.example.command.Command;

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
	 * {@link Tunable} fields in the {@link Command} object and created appropriate
	 * handlers for each field. 
	 */
	public final void intercept(Command command) {
		List<T> handlerList = new LinkedList<T>();

		// Find each public field in the class.
		for (Field field : command.getClass().getFields()) {

			// See if the field is annotated as a Tunable.
   			if (field.isAnnotationPresent(Tunable.class)) {
				try {
					Tunable tunable = field.getAnnotation(Tunable.class);
					
					// Get a handler for this particular field type and
					// add it to the list.
					T handler = factory.getHandler(field,command,tunable);

					if ( handler != null )
					 	handlerList.add( handler ); 

				} catch (Throwable ex) {
					System.out.println("tunable field intercept failed: " + field.toString() );
					ex.printStackTrace();
				}
			}
		}

		// Find each public method in the class.
		for (Method method : command.getClass().getMethods()) {

			// See if the method is annotated as a Tunable.
   			if (method.isAnnotationPresent(Tunable.class)) {
				try {
					Tunable tunable = method.getAnnotation(Tunable.class);
					
					// Get a handler for this particular field type and
					// add it to the list.
					T handler = factory.getHandler(method,command,tunable);

					if ( handler != null )
					 	handlerList.add( handler ); 

				} catch (Throwable ex) {
					System.out.println("tunable method intercept failed: " + method.toString() );
					ex.printStackTrace();
				}
			}
		}

		process(handlerList);
	}

	/** 
	 * This method gets executed by the <code>intercept(Command c)</code> method after all 
	 * {@link Tunable}s have been extracted. This should NOT be called otherwise!
	 */
	protected abstract void process(List<T> handlers);
}
