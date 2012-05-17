package org.cytoscape.extras.event_tracker.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.cytoscape.event.CyPayloadEvent;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

public class EventTracker {
	private Map<String, Object> serviceProxies;
	private Map<String, EventData> serviceEventData;
	
	private BundleContext context;
	private InvocationHandler handler;
	private List<EventOccurredListener> listeners;

	public EventTracker(BundleContext context) {
		this.context = context;

		serviceProxies = new HashMap<String, Object>();
		serviceEventData = new HashMap<String, EventData>();
		listeners = new ArrayList<EventOccurredListener>();
		
		handler = new InvocationHandler() {
			public Object invoke(Object source, Method method, Object[] parameters) throws Throwable {
				if (parameters.length == 1 && method.getName().equals("equals")) {
					return source == parameters[0];
				}

				if (parameters.length == 0 && method.getName().equals("hashCode")) {
					return System.identityHashCode(source);
				}

				if (!(parameters.length == 1 && method.getName().equals("handleEvent"))) {
					return null;
				}
				
				Class<?> interfaceClass = source.getClass().getInterfaces()[0];
				String className = interfaceClass.getName();
				synchronized (serviceEventData) {
					EventData data = serviceEventData.get(className);
					if (data == null) {
						data = new EventData(interfaceClass);
						serviceEventData.put(className, data);
					}
					
					if (parameters[0] instanceof CyPayloadEvent<?, ?>) {
						CyPayloadEvent<?, ?> event = (CyPayloadEvent<?, ?>) parameters[0];
						data.addPayload(event.getPayloadCollection().size());
					}
					data.increment();
					
					for (EventOccurredListener listener : listeners) {
						listener.eventOccurred(data);
					}
				}
				return null;
			}
		};
	}
	
	public void start() {
		context.addServiceListener(new ServiceListener() {
			public void serviceChanged(ServiceEvent event) {
				handleServiceChanged(event);
			}
		});
		trackExistingServices();
	}

	private void trackExistingServices() {
		for (Bundle bundle : context.getBundles()) {
			ServiceReference[] services = bundle.getRegisteredServices();
			if (services == null) {
				continue;
			}
			for (ServiceReference reference : services) {
				trackService(reference);
			}
		}
	}

	private void handleServiceChanged(ServiceEvent event) {
		trackService(event.getServiceReference());
	}

	private void trackService(ServiceReference serviceReference) {
		String[] classNames = (String[]) serviceReference.getProperty(Constants.OBJECTCLASS);
		for (String name : classNames) {
			if (name.endsWith("Listener")) {
				trackService(name);
			}
		}
	}

	private void trackService(String name) {
		ServiceReference reference = context.getServiceReference(name);
		Object service = context.getService(reference);
		Class<?>[] interfaces = service.getClass().getInterfaces();
		context.ungetService(reference);
		
		for (Class<?> interfaceClass : interfaces) {
			if (interfaceClass.getName().equals(name)) {
				trackService(interfaceClass);
			}
		}
	}

	private void trackService(Class<?> interfaceClass) {
		String name = interfaceClass.getName();
		if (serviceProxies.containsKey(name)) {
			return;
		}
		
		Object proxy = Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[] { interfaceClass }, handler);
		serviceProxies.put(name, proxy);
		context.registerService(name, proxy, new Properties());
	}

	public void addListener(EventOccurredListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(EventOccurredListener listener) {
		while (!listeners.remove(listener));
	}
}
