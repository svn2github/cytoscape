package org.cytoscape.vizmap.internal;

import java.util.HashMap;
import java.util.HashSet;

import org.cytoscape.viewmodel.CyNetworkView;
import org.cytoscape.vizmap.VisualMappingManager;
import org.cytoscape.vizmap.VisualStyle;

public class VisualMappingManagerImpl implements VisualMappingManager {
	private HashMap<CyNetworkView, VisualStyle> vsForNetwork;
	
	public VisualMappingManagerImpl() {
		vsForNetwork = new HashMap<CyNetworkView, VisualStyle>();
	}
	
	public VisualStyle getVisualStyle(CyNetworkView nv) {
		return vsForNetwork.get(nv);
	}

	public void setVisualStyle(VisualStyle vs, CyNetworkView nv) {
		// TODO Auto-generated method stub
		vsForNetwork.put(nv, vs);
	}

}
