
package org.cytoscape.event.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.cytoscape.event.CyPayloadEvent;

class PayloadAccumulator<S,P,E extends CyPayloadEvent<S,P>> {

	private List<P> payloadList; 
	private final Constructor<E> constructor;

	PayloadAccumulator(S source, Class<E> eventType) throws NoSuchMethodException {
		constructor = eventType.getConstructor(source.getClass(), Collection.class);
		payloadList = new ArrayList<P>();
	}

	E newEventInstance(Object source) throws InstantiationException, IllegalAccessException, InvocationTargetException {
		if ( source == null ) 
			return null;

		final Collection<P> coll = getPayloadCollection();

		if ( coll == null ) 
			return null;

		return constructor.newInstance( source, coll );			
	}

	synchronized void addPayload(P t) {
		if ( t != null ) 
			payloadList.add(t);
	}

	synchronized private Collection<P> getPayloadCollection() {
		if ( payloadList.isEmpty() )
			return null;

		List<P> ret = payloadList;
		payloadList = new ArrayList<P>();
		return ret; 
	}
}
