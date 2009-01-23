
package org.example.tunable;

import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

/**
 * An abstract implementation of {@link TunableInterceptor} that should serve as
 * the super class for almost all implementations of {@link TunableInterceptor}.
 */
public abstract class AbstractTunableInterceptor<T extends Handler> 
	implements TunableInterceptor {

	protected HandlerFactory<T> factory;
	protected Map<Object,List<T>> handlerMap;

	public AbstractTunableInterceptor(HandlerFactory<T> factory) {
		this.factory = factory; 
		handlerMap = new HashMap<Object,List<T>>();
	}

	/**
	 * Calls the <code>process(List&lt;T&gt; handlers)</code> method once it has identified all
	 * {@link Tunable} fields in the {@link Object} and created appropriate
	 * handlers for each field. 
	 */
	public final void intercept(Object obj) {
		
		if ( !handlerMap.containsKey(obj) ) {
			//System.out.println("intercepting obj: " + obj.getClass().toString());
			List<T> handlerList = new LinkedList<T>();

			// Find each public field in the class.
			for (Field field : obj.getClass().getFields()) {
				//System.out.println("field " + field.getName());

				// See if the field is annotated as a Tunable.
   				if (field.isAnnotationPresent(Tunable.class)) {
					//System.out.println("   has tunable");
					try {
						Tunable tunable = field.getAnnotation(Tunable.class);
						
						// Get a handler for this particular field type and
						// add it to the list.
						T handler = factory.getHandler(field,obj,tunable);

						if ( handler != null ) {
							//System.out.println("   adding handler");
						 	handlerList.add( handler ); 
						}
	
					} catch (Throwable ex) {
						System.out.println("tunable field intercept failed: " + field.toString() );
						ex.printStackTrace();
					}
				}
			}
	
			// Find each public method in the class.
			for (Method method : obj.getClass().getMethods()) {
	
				// See if the method is annotated as a Tunable.
   				if (method.isAnnotationPresent(Tunable.class)) {
					try {
						Tunable tunable = method.getAnnotation(Tunable.class);
						
						// Get a handler for this particular field type and
						// add it to the list.
						T handler = factory.getHandler(method,obj,tunable);
	
						if ( handler != null )
						 	handlerList.add( handler ); 
	
					} catch (Throwable ex) {
						System.out.println("tunable method intercept failed: " + method.toString() );
						ex.printStackTrace();
					}
				}
			}

			handlerMap.put(obj,handlerList);
		}

		process(handlerMap.get(obj));
	}

	/** 
	 * This method gets executed by the <code>intercept(Object o)</code> method after all 
	 * {@link Tunable}s have been extracted. This should NOT be called otherwise!
	 */
	protected abstract void process(List<T> handlers);
}
