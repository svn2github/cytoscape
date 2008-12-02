package org.cytoscape.webservice.client;

public interface CyWebServiceEventFactory {

	public <P> CyWebServiceEvent<P> createEvent(String name, WSEventType type,
			P parameter);
	
	public <P> CyWebServiceEvent<P> createEvent(String name, WSEventType type,
			P parameter, WSEventType nextAction);

}
