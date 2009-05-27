
package org.example.tunable;

import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.*;

/**
 * An abstract implementation of {@link TunableInterceptor} that should serve as
 * the super class for almost all implementations of {@link TunableInterceptor}.
 */
public abstract class AbstractTunableInterceptor<T extends Handler> 
	implements TunableInterceptor {

	protected HandlerFactory<T> factory;

	// We want the linked hash map to preserve insertion order since
	// this should also be rendering order.
	protected Map<Object,LinkedHashMap<String,T>> handlerMap;

	public AbstractTunableInterceptor(HandlerFactory<T> factory) {
		this.factory = factory; 
		handlerMap = new HashMap<Object,LinkedHashMap<String,T>>();
	}

	/**
	 * Calls the <code>process(List&lt;T&gt; handlers)</code> method once it has identified all
	 * {@link Tunable} fields in the {@link Object} and created appropriate
	 * handlers for each field. 
	 */
	public final void loadTunables(Object obj) {
		
		if(obj!=null){
			if ( !handlerMap.containsKey(obj) ) {
				//System.out.println("intercepting obj: " + obj.getClass().toString());
				LinkedHashMap<String,T> handlerList = new LinkedHashMap<String,T>();
	
				// Find each public field in the class.
				for (Field field : obj.getClass().getFields()) {
					//System.out.println("PUBLIC field " + field.getName());
	
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
								handlerList.put(field.getName(),handler);
							}
		
						} catch (Throwable ex) {
							System.out.println("tunable field intercept failed: " + field.toString() );
							ex.printStackTrace();
						}
					}
				}
		
				Map<String, Method> setMethodsMap = new HashMap<String,Method>();
				Map<String, Method> getMethodsMap = new HashMap<String,Method>();
				
				Map<String, Tunable> getTunableMap = new HashMap<String,Tunable>();
				Map<String, Tunable> setTunableMap = new HashMap<String,Tunable>();
						
				
				// Find each public method in the class.
				for (Method method : obj.getClass().getMethods()) {
					// See if the method is annotated as a Tunable.
	   				if (method.isAnnotationPresent(Tunable.class)) {
						try {
							Tunable tunable = method.getAnnotation(Tunable.class);
							if(method.getName().startsWith("get")){
								getMethodsMap.put(method.getName().substring(3),method);
								getTunableMap.put(method.getName().substring(3),tunable);
								if(setMethodsMap.containsKey(method.getName().substring(3))){
									//get a handler with the getMethod and setMethod
									T handler = factory.getHandler(getMethodsMap.get(method.getName().substring(3)),setMethodsMap.get(method.getName().substring(3)), obj, getTunableMap.get(method.getName().substring(3)),setTunableMap.get(method.getName().substring(3)));
									if ( handler != null ) {
									 	handlerList.put( "getset" + method.getName().substring(3), handler ); 
									}
								}
							}
							else if(method.getName().startsWith("set")){
								setMethodsMap.put(method.getName().substring(3),method);
								setTunableMap.put(method.getName().substring(3),tunable);
								if(getMethodsMap.containsKey(method.getName().substring(3))){
									//get a handler with the getMethod and setMethod
									T handler = factory.getHandler(getMethodsMap.get(method.getName().substring(3)),setMethodsMap.get(method.getName().substring(3)), obj, getTunableMap.get(method.getName().substring(3)),setTunableMap.get(method.getName().substring(3)));
									if ( handler != null ) {
									 	handlerList.put( "getset" + method.getName().substring(3), handler ); 
									}
								}
							}
							else throw new Exception("the name of the method has to start with \"set\" or \"get\"");
						
							// Get a handler for this particular field type and
							// add it to the list.
//							T handler = factory.getHandler(method,obj,tunable);
//							if ( handler != null ) {
//							 	handlerList.put( method.getName(), handler ); 
//							}

						} catch (Throwable ex) {
							System.out.println("tunable method intercept failed: " + method.toString() );
							ex.printStackTrace();
						}
					}
				}
	
				handlerMap.put(obj,handlerList);
			}
		}
		else throw new IllegalArgumentException("THE COMMAND IS EMPTY\nProvide something!");
	}
		
	public Map<String,T> getHandlers(Object o) {
		if ( o == null )
			return null;
		return handlerMap.get(o);
	}

	public abstract void createUI(Object ... objs);

}
