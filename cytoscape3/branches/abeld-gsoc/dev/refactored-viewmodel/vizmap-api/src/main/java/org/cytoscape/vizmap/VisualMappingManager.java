package org.cytoscape.vizmap;

import org.cytoscape.view.model.CyNetworkView;

/**
 * From pre-3.0 functionality, only 'network->VisualStyle' map remains in here.
 * @author abeld
 *
 */
public interface VisualMappingManager {
	
	public void setVisualStyle(VisualStyle vs, CyNetworkView nv);
	public VisualStyle getVisualStyle(CyNetworkView nv);
}
