package org.cytoscape.session;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;

/**
 * A session is an immutable snapshot of the data contents of Cytoscape.
 * Sessions are only meant for saving and restoring the state of Cytoscape
 * and are not meant to be used interactively for anything besides 
 * writing, reading, and restoring from session files.
 * <br/>
 * Using the data returned from the various methods in a CySession object
 * should be sufficient to recreate all aspects of Cytoscape at the time
 * the session was created.
 */
public interface CySession {

    Set<CyNetworkView> getNetworkViews();

    Set<CyTable> getTables();

	Map<CyNetworkView,String> getViewVisualStyleMap();

    Properties getCytoscapeProperties();

    Properties getVizmapProperties();

    Properties getDesktopProperties();
}
