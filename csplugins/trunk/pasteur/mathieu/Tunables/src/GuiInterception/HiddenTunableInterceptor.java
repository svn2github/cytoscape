package GuiInterception;

import HandlerFactory.Handler;
import HandlerFactory.HandlerFactory;
import Tunable.Tunable;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public abstract class HiddenTunableInterceptor<T extends Handler> implements TunableInterceptor<T>{
	
	protected HandlerFactory<T> factory;
	protected Map<Object,LinkedHashMap<String,T>> handlerMap;
	
	public HiddenTunableInterceptor(HandlerFactory<T> tunablehandlerfactory) {
		this.factory = tunablehandlerfactory;
		handlerMap = new HashMap<Object,LinkedHashMap<String,T>>();
	}

	public final void loadTunables(Object obj){
		//if ( !handlerMap.containsKey(obj) ) {					//Deleted to get new Panels if we do it many times
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
	
	public final void interceptandDisplayResults(Object obj){
		List<T> handlerList = new LinkedList<T>();	
		Iterator<Guihandler> iter = (Iterator<Guihandler>) handlerMap.get(obj).values().iterator();
		while(iter.hasNext()){
			T elem = (T) iter.next();
			handlerList.add(elem);
		}
		getResultsPanels(handlerList);
	}
	
	
	public final void processProperties(Object obj){
		List<T> handlerList = new LinkedList<T>();	
		Iterator<Guihandler> iter = (Iterator<Guihandler>) handlerMap.get(obj).values().iterator();
		while(iter.hasNext()){
			T elem = (T) iter.next();
			handlerList.add(elem);
		}
		processProps(handlerList);
	}
	
	
	
	public Map<String,T> getHandlers(Object o) {
		if ( o == null )
			return null;
		return handlerMap.get(o);
	}

	
	public final void interceptAndReinitializeObjects(Object obj){
		//if ( !handlerMap.containsKey(obj) ) {
			LinkedHashMap<String,T> handlerList = new LinkedHashMap<String,T>();
			// Find each public field in the class.
			for (Field field : obj.getClass().getFields()) {
				// See if the field is annotated as a Tunable.
   				if (field.isAnnotationPresent(Tunable.class)) {
					try {
						Tunable tunable = field.getAnnotation(Tunable.class);
						T handler = factory.getHandler(field,obj,tunable);
						if ( handler != null )handlerList.put(field.getName(), handler );
						else System.out.println("No handler for type: "+ field.getType().getName());
					}catch (Throwable ex) {
						System.out.println("tunable field intercept failed: " + field.toString() );
						ex.printStackTrace();
					}
				}
			}
			handlerMap.put(obj,handlerList);
		//}
	}
	
	
	protected abstract void getResultsPanels(List<T> handlers);
	protected abstract void processProps(List<T> handlers);
	public abstract int createUI(Object ... objs);
}