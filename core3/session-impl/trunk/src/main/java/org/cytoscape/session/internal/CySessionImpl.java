package org.cytoscape.session.internal;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.HashSet;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.session.CySession;

public class CySessionImpl implements CySession {

	@Override
    public Set<CyNetworkView> getNetworkViews() {
    	return new HashSet<CyNetworkView>();
    }

	@Override
    public Set<CyTable> getTables() {
    	return new HashSet<CyTable>();
    }

	@Override
    public Map<CyNetworkView,String> getViewVisualStyleMap() {
    	return new HashMap<CyNetworkView,String>();
    }

	@Override
    public Properties getCytoscapeProperties() {
		return new Properties();
    }

	@Override
    public Properties getVizmapProperties() {
		return new Properties();
    }

	@Override
    public Properties getDesktopProperties() {
		return new Properties();
    }
}
