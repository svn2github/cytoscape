package GuiInterception;

import HandlerFactory.Handler;
import HandlerFactory.HandlerFactory;
import Tunable.Tunable;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public abstract class HiddenTunableInterceptor<T extends Handler> implements TunableInterceptor{
	
	protected HandlerFactory<T> factory;
	List<T> handlerList = new LinkedList<T>();
	protected Map<Object,List<T>> handlerMap;
	
	public HiddenTunableInterceptor(HandlerFactory<T> tunablehandlerfactory) {
		this.factory = tunablehandlerfactory;
		handlerMap = new HashMap<Object,List<T>>();
	}

	public final int intercept(Object obj){	
		if ( !handlerMap.containsKey(obj) ) {
			List<T> handlerList = new LinkedList<T>();
			// Find each public field in the class.
			for (Field field : obj.getClass().getFields()) {
				// See if the field is annotated as a Tunable.
   				if (field.isAnnotationPresent(Tunable.class)) {
					try {
						Tunable tunable = field.getAnnotation(Tunable.class);
						T handler = factory.getHandler(field,obj,tunable);
						if ( handler != null )handlerList.add( handler );
						else System.out.println("No handler for type: "+ field.getType().getName());
					}catch (Throwable ex) {
						System.out.println("tunable field intercept failed: " + field.toString() );
						ex.printStackTrace();
					}
				}
			}
			handlerMap.put(obj,handlerList);
		}
		int action = process(handlerMap.get(obj));
		return action;
	}
	
	public final void processProperties(Object obj){
		processProps(handlerMap.get(obj));
	}
	
	protected abstract void processProps(List<T> handlers);
	protected abstract int process(List<T> handlers);
}