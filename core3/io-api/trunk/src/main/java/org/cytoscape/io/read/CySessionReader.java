package org.cytoscape.io.read;

import org.cytoscape.session.CySession;
import org.cytoscape.work.Task;

/**
 * An extension of the Task interface that returns a
 * {@link CySession} object. The reader does nothing
 * beyond create the CySession object and does NOT 
 * use the CySession object to define the state of 
 * Cytoscape - that is managed by the CySessionManager.
 * Instances of this interface are created by InputStreamTaskFactory
 * objects registered as OSGi services, which are in turn processed
 * by associated reader manager objects that distinguish 
 * InputStreamTaskFactories based on the DataCategory associated with
 * the CyFileFilter.
 */
public interface CySessionReader extends Task {

	/**
	 * return a session object
	 * @return A {@link CySession} object. 
	 */
    CySession getCySession();
}

