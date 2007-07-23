/* -*-Java-*-
********************************************************************************
*
* File:         DeleteHyperEdgeAction.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdgeEditor/src/cytoscape/hyperedge/editor/actions/DeleteHyperEdgeAction.java,v 1.1 2007/07/04 01:19:09 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Wed Dec 06 10:30:18 2006
* Modified:     Thu Jun 28 17:34:20 2007 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2006, Agilent Technologies, all rights reserved.
*
********************************************************************************
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
