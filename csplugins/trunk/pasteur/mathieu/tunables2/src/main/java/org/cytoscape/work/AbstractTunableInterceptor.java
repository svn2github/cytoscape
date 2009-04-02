package org.cytoscape.work;

import java.awt.Component;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.cytoscape.work.internal.gui.HandlerFactory;


public abstract class AbstractTunableInterceptor<T extends Handler> implements TunableInterceptor<T>{
	
	protected HandlerFactory<T> factory;
	protected Map<Object,LinkedHashMap<String,T>> handlerMap;
	
	public AbstractTunableInterceptor(HandlerFactory<T> tunablehandlerfactory) {
		this.factory = tunablehandlerfactory;
		handlerMap = new HashMap<Object,LinkedHashMap<String,T>>();
	}

	public final void loadTunables(Object obj){
		//if ( !handlerMap.containsKey(obj) ) {
			LinkedHashMap<String,T> handlerList = new LinkedHashMap<String,T>();
			// Find each public field in the class.
			for (Field field : obj.getClass().getFields()) {
				// See if the field is annotated as a Tunable.
   				if (field.isAnnotationPresent(Tunable.class)) {
					try {
						Tunable tunable = field.getAnnotation(Tunable.class);
						
						T handler = factory.getHandler(field,obj,tunable);
						if ( handler != null )handlerList.put( field.getName(),handler );
						else System.out.println("No handler for type: "+ field.getType().getName());
					}catch (Throwable ex) {
						System.out.println("tunable field intercept failed: " + field.toString() );
						ex.printStackTrace();
					}
				}
			}
			handlerMap.put(obj,handlerList);
		//}													//End of the deleted Loop
	}	
	
	
	public Map<String,T> getHandlers(Object o) {
		if ( o == null )
			return null;
		return handlerMap.get(o);
	}
	
	public abstract boolean createUI(Object ... objs);
}