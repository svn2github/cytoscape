package cytoscape.layout.label;

import cytoscape.plugin.CytoscapePlugin;

import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import cytoscape.data.CyAttributes;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.LayoutProperties;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.Cytoscape;

import cytoscape.view.CyNetworkView;
import cytoscape.ding.DingNetworkView;
import cytoscape.layout.CyLayouts;

import giny.view.NodeView;
import giny.view.Label;
import cytoscape.view.CyNodeView;

import cytoscape.visual.Appearance;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.LabelPosition;

import cytoscape.data.Semantics;

/** ---------------------------LabelLayoutPlugin-----------------------------
 * Takes the current network and reorganizes it so that the new network is more
 * readable.  This will be done through the repositioning of network labels,
 * and subtle repositioning of nodes.
 * @author Victoria Mui, Gerardo Huck
 *
 */
public class LabelLayoutPlugin extends CytoscapePlugin {
	
    /**
     * Constructor which adds this layout to Cytoscape Layouts.  This in turn
     * adds it to the Cytoscape menus as well.
     */
	public LabelLayoutPlugin() {

		// Add this layout to the Layout menu under "Label Layouts".
		CyLayouts.addLayout(new LabelForceDirectedLayout(), 
				"Label Layouts");

	}
       	

 }