package org.cytoscape.io.read;

import org.cytoscape.session.CySession;
import org.cytoscape.work.Task;

/**
 */
public interface CySessionReader extends Task {

    CySession getCySession();

}

