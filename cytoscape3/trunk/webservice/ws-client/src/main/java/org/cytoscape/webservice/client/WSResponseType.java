package org.cytoscape.webservice.client;

/**
 * Will be used to catch the signal from WS.
 * Core will listen to this event to determine next move.
 * 
 * @author kono
 *
 */
public enum WSResponseType {
	SEARCH_FINISHED, DATA_IMPORT_FINISHED;
}