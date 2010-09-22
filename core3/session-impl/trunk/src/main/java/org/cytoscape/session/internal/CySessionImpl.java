package org.cytoscape.session.internal;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.session.CySession;

public class CySessionImpl implements CySession {

    public String getSessionName() {
    	return null;
    }

    public Set<CyNetwork> getNetworks() {
    	return null;
    }

    public Set<CyNetworkView> getNetworkViews() {
    	return null;
    }

    public Set<CyTable> getTables() {
    	return null;
    }

    public Set<VisualStyle> getVisualStyles() {
    	return null;
    }

    public Map<String,Properties> getProperties() {
    	return null;
    }

	@Override
	public String getFileName() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
