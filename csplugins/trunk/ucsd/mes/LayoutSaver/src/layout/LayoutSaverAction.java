

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

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import giny.model.Node;
import giny.view.NodeView;

class LayoutSaverAction extends AbstractAction {

	LayoutSaverAction() {
		super("Save Node Positions in Current Layout");
	}

    public static final String X_LOC = "saved_x_location";
    public static final String Y_LOC = "saved_y_location";

	public void actionPerformed(ActionEvent e) {
		
		CyNetworkView view = Cytoscape.getCurrentNetworkView();

		if ( view == null || view == Cytoscape.getNullNetworkView() )
			return;
		CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();
		Iterator it = view.getNodeViewsIterator();
		while ( it.hasNext() ) {
			NodeView nv = (NodeView)it.next();
			Node n = nv.getNode();
			nodeAttrs.setAttribute(n.getIdentifier(),X_LOC,nv.getXPosition());
			nodeAttrs.setAttribute(n.getIdentifier(),Y_LOC,nv.getYPosition());
		}
	}
}
