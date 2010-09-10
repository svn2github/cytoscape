package org.cytoscape.io.write;


import org.cytoscape.session.CySessionManager;

/**
 * Returns a Task that will write
 */
public interface SessionWriterFactory extends CyWriterFactory {

	void setSessionManager(CySessionManager mgr);
}
