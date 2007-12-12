/**
 * 
 */
package stub.controller.event;

import java.util.*;

/**
 * @author skillcoy
 * 
 */
public class EventPublisher<T> {

	private HashMap<Class<T>, List<EventHandler<T>>> registered;

	public EventPublisher() {
		registered = new HashMap<Class<T>, List<EventHandler<T>>>();
	}
	
	public void registerInterest(Class<T> eventClass, EventHandler<T> eventHandler) {
		List<EventHandler<T>> Handlers = new ArrayList<EventHandler<T>>();
		if (registered.containsKey(eventClass)) {
			Handlers = registered.get(eventClass);
		}
		Handlers.add(eventHandler);
		registered.put(eventClass, Handlers);
	}

	public void publishEvent(T event) {
		System.out.println("Publishing event " + event.toString());
		for (Class eventClass: registered.keySet()) {
			if (event.getClass().equals(eventClass)) {
				for (EventHandler<T> handler: registered.get(eventClass))
					handler.handle(event);
			}
		}
	}
	
	
}
