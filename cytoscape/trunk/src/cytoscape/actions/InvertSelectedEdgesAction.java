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
public class InvertSelectedEdgesAction extends AbstractAction {
    NetworkView networkView;
    
    public InvertSelectedEdgesAction(NetworkView networkView) {
        super("Invert selection");
        this.networkView = networkView;
    }

    public void actionPerformed (ActionEvent e) {

        if (networkView.getCytoscapeObj().getConfiguration().isYFiles()) {
	    String callerID = "InvertSelectedEdgesAction.actionPerformed";
	    networkView.getNetwork().beginActivity(callerID);
	    GraphUtils.invertSelectedEdges( networkView.getNetwork().getGraph() );
	    networkView.redrawGraph(false, false);
	    networkView.getNetwork().endActivity(callerID);
	}
	else {
	    GinyUtils.invertSelectedEdges(networkView.getView());
	}
    }
}

