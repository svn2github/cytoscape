/*
 Copyright (c) 2011, The Cytoscape Consortium (www.cytoscape.org)

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
/*
*
* Revisions:
*
* Fri May 11 15:38:43 2007 (Michael L. Creech) creech@w235krbza760
*  Updated actionPerformed() to correctly remove ConnectorNodes.
********************************************************************************
*/
package cytoscape.hyperedge.editor.actions;

import cytoscape.hyperedge.HyperEdge;
import cytoscape.hyperedge.HyperEdgeFactory;
import cytoscape.hyperedge.impl.HyperEdgeManagerImpl;

import cytoscape.CyNetwork;

import cytoscape.util.CytoscapeAction;

import java.awt.event.ActionEvent;

/**
 * Action performed when the popup menu
 * 'HyperEdgeEditor->Delete->Delete HyperEdge xxx' is used to delete
 * connector nodes.
 */
public class DeleteHyperEdgeAction extends CytoscapeAction {
    private static final long serialVersionUID  = 5456878151703608284L;
    private HyperEdge         _he;
    private CyNetwork         _net;
    private boolean           _allNetworkDelete;

    public DeleteHyperEdgeAction(HyperEdge he, CyNetwork net, String label,
                                 boolean allNetworkDelete) {
        super(label);
        _he               = he;
        _net              = net;
        _allNetworkDelete = allNetworkDelete;
    }

    public void actionPerformed(ActionEvent ae) {
	// MLC 05/11/07 BEGIN:
	// NOTE: Because this may go away if HyperEdge's are placed in
	// the core, we don't make this a part of the public
	// interface:
	HyperEdgeManagerImpl heMan = (HyperEdgeManagerImpl) HyperEdgeFactory.INSTANCE.getHyperEdgeManager();
        if (_allNetworkDelete) {
            _he.destroy();
	    // MLC 05/11/07: 
	    heMan.hideConnectorNodes();
        } else {
            _he.removeFromNetwork(_net);
	    // MLC 05/11/07: 
	    heMan.hideConnectorNodes(_net);
        }
    }
}
