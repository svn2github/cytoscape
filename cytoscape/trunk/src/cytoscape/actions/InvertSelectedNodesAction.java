//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class InvertSelectedNodesAction extends AbstractAction {
    NetworkView networkView;
    
    public InvertSelectedNodesAction(NetworkView networkView) {
        super("Invert selection");
        this.networkView = networkView;
    }

    public void actionPerformed (ActionEvent e) {

	if (networkView.getCytoscapeObj().getConfiguration().isYFiles()) {
	    String callerID = "InvertSelectedNodesAction.actionPerformed";
	    networkView.getNetwork().beginActivity(callerID);
	    GraphUtils.invertSelectedNodes( networkView.getNetwork().getGraph() );
	    networkView.redrawGraph(false, false);
	    networkView.getNetwork().endActivity(callerID);
	}
	else {
	    GinyUtils.invertSelectedNodes(networkView.getView());
	}

    }
}

