package org.cytoscape.webservice.client;

public interface CyWebServiceEvent<P> {

	public WSEventType getEventType();

	public P getParameter();

	public WSEventType getNextMove();

}
