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
public class HideSelectedNodesAction extends AbstractAction   {
    NetworkView networkView;
    
    public HideSelectedNodesAction (NetworkView networkView) {
        super("Hide selection");
        this.networkView = networkView;
    }

    public void actionPerformed (ActionEvent e) {
        String callerID = "HideSelectedNodesAction.actionPerformed";
        networkView.getNetwork().beginActivity(callerID);
        GinyUtils.hideSelectedNodes(networkView.getView());
        networkView.redrawGraph(false, false);
        networkView.getNetwork().endActivity(callerID);
    }
}

