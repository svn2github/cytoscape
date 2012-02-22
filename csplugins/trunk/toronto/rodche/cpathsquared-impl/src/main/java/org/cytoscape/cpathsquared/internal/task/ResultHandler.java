package org.cytoscape.cpathsquared.internal.task;

public interface ResultHandler {
	void finished(int matchesFound) throws Exception;
}
