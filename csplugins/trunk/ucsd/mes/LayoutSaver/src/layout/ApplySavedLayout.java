

/*
 Copyright (c) 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package layout;

import cytoscape.layout.AbstractLayout;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CyNodeView;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import java.util.Iterator;
import giny.model.Node;
import giny.view.NodeView;

public class ApplySavedLayout extends AbstractLayout {

	public ApplySavedLayout() { }

	
	public void construct() {
		// calculate the yOffset for nodes that don't have existing positions
		Iterator it = Cytoscape.getCurrentNetwork().nodesIterator();
		double yOffset = Double.NEGATIVE_INFINITY;
		while (it.hasNext()) {
			Node node = (Node) it.next();
			NodeView nodeView = networkView.getNodeView(node);
			yOffset = Math.max(nodeView.getXPosition(),yOffset);
		}
		yOffset += 100.0;

		// now place the nodes
		int nextX = 0;
		int nextY = 0;
		CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();
		CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
		it = Cytoscape.getCurrentNetwork().nodesIterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			NodeView nodeView = networkView.getNodeView(node);

			Double X_pos = nodeAttrs.getDoubleAttribute(node.getIdentifier(), LayoutSaverAction.X_LOC);
			double x_pos = X_pos != null ? X_pos.intValue() : (nextX++ % 10)*100;
			nodeView.setXPosition((int)x_pos);

			Double Y_pos = nodeAttrs.getDoubleAttribute(node.getIdentifier(), LayoutSaverAction.Y_LOC);
			double y_pos = Y_pos != null ? Y_pos.intValue() : yOffset + (nextY++ / 10)*100;
			nodeView.setYPosition((int)y_pos);
		}
	}

	public String getName() {
		return "Apply Saved Layout";
	}
	public String toString() {
		return getName(); 
	}

}

