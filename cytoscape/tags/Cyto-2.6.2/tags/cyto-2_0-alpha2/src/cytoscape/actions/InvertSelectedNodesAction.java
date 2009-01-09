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
        String callerID = "InvertSelectedNodesAction.actionPerformed";
        networkView.getNetwork().beginActivity(callerID);
        GinyUtils.invertSelectedNodes(networkView.getView());
        networkView.redrawGraph(false, false);
        networkView.getNetwork().endActivity(callerID);
    }
}

