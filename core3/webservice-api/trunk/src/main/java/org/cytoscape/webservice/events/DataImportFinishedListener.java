package org.cytoscape.webservice.events;

public interface DataImportFinishedListener {
	void handleEvent(DataImportFinishedEvent<?> evt);
}
