package org.cytoscape.webservice.client.internal;

import org.cytoscape.webservice.client.CyWebServiceEvent;
import org.cytoscape.webservice.client.CyWebServiceEventFactory;
import org.cytoscape.webservice.client.WSEventType;

public class CyWebServiceEventFactoryImpl implements CyWebServiceEventFactory {

	public <P> CyWebServiceEvent<P> createEvent(String name, WSEventType type,
			P parameter) {

		return new CyWebServiceEventImpl<P>(name, type, parameter);
	}

	public <P> CyWebServiceEvent<P> createEvent(String name, WSEventType type,
			P parameter, WSEventType nextAction) {
		return new CyWebServiceEventImpl<P>(name, type, parameter, nextAction);
	}

}
