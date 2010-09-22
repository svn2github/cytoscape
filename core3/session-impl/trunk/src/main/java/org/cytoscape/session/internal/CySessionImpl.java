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
    public String getSessionName() {
    	return "test";
    }

	@Override
    public Set<CyNetwork> getNetworks() {
    	return new HashSet<CyNetwork>();
    }

	@Override
    public Set<CyNetworkView> getNetworkViews() {
    	return new HashSet<CyNetworkView>();
    }

	@Override
    public Set<CyTable> getTables() {
    	return new HashSet<CyTable>();
    }

	@Override
    public Set<VisualStyle> getVisualStyles() {
    	return new HashSet<VisualStyle>();
    }

	@Override
    public Map<String,Properties> getProperties() {
		Map<String,Properties> map = new HashMap<String,Properties>();
		map.put("cytoscape", new Properties());
		map.put("vizmap", new Properties());
    	return map;
    }

	@Override
	public String getFileName() {
		return "default";
	}
	
}
