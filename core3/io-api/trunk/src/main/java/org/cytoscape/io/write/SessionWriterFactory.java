package org.cytoscape.io.write;


import org.cytoscape.session.CySession;

/**
 * Returns a Task that will write
 */
public interface SessionWriterFactory extends CyWriterFactory {

	void setSession(CySession session);
}
