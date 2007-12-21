/**
 * 
 */
package org.systemsbiology.cytoscape.script;

import cytoscape.*;
import cytoscape.actions.GinyUtils;

/**
 * @author skillcoy
 *
 */
//for now just handle selected nodes and edges
public class CommandHandler 
	{
	public static void hideSelection(String netId)
		{
		GinyUtils.hideSelectedNodes(Cytoscape.getNetworkView(netId));
		GinyUtils.hideSelectedEdges(Cytoscape.getNetworkView(netId));
		
		Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, null);
		}

	
	public static void invertSelection(String netId)
		{
		GinyUtils.invertSelectedNodes(Cytoscape.getNetworkView(netId));
		GinyUtils.invertSelectedEdges(Cytoscape.getNetworkView(netId));
		
		Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, null);
		}

	
	public static void clearSelection(String netId)
		{// TODO stop unhiding, add a showAll
		GinyUtils.unHideAll(Cytoscape.getNetworkView(netId));
		
		Cytoscape.getNetwork(netId).unselectAllNodes();
		Cytoscape.getNetwork(netId).unselectAllEdges();
		
		Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, null);
		}
	
	
	
	}
