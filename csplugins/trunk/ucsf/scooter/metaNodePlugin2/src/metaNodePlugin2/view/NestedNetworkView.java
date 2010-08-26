/* vim: set ts=2: */
/**
 * Copyright (c) 2007 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package metaNodePlugin2.view;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.groups.CyGroup;
import cytoscape.view.CyNetworkView;

public class NestedNetworkView {
	static private Map<CyGroup, CyNetworkView> nnMap = new HashMap<CyGroup, CyNetworkView>();

	public static void create(CyGroup group, CyNetworkView view, double opacity) {
		// Get the network
		CyNetwork nn = group.getGraphPerspective();

		// Create the view
		CyNetworkView nnView = Cytoscape.createNetworkView(nn, group.toString());

		Dimension position = new Dimension();
		position.setSize(0.0,0.0);

		// Move the nodes around
		Dimension boundingBox = ViewUtils.restoreNodes(group, nn, nnView, position, Cytoscape.getNodeAttributes(), null);

		// Add it to the network map as a child of the group's network
		CyNetwork network = group.getNetwork();
		// We do this by firing a NETWORK_CREATED event with the parent network as the old value
		// and our new network as the new value
		Cytoscape.firePropertyChange(Cytoscape.NETWORK_CREATED, nn.getIdentifier(), network.getIdentifier());

		// Set it as a nested network
		group.getGroupNode().setNestedNetwork(nn);

		// Update the size of our group node to match the bounding box
		ViewUtils.setNodeSize(group.getGroupNode(), view, boundingBox);

		// Remember that we did it
		nnMap.put(group, nnView);

		// Bring our original view back into focus!
	}

	public static void destroy(CyGroup group, CyNetworkView view) {
		if (!nnMap.containsKey(group))
			return;

		CyNetworkView nnView = nnMap.get(group);
		nnMap.remove(group);

		group.getGroupNode().setNestedNetwork(null);

		// Destroy the network view
		Cytoscape.destroyNetworkView(nnView);

		// Remove the graph perspective from our list
		Cytoscape.firePropertyChange(Cytoscape.NETWORK_DESTROYED, group.getGraphPerspective().getIdentifier(), null);
	}
}
