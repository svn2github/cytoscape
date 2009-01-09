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
public class HideSelectedEdgesAction extends AbstractAction {
    NetworkView networkView;
    
    public HideSelectedEdgesAction(NetworkView networkView) {
        super("Hide selection");
        this.networkView = networkView;
    }

    public void actionPerformed (ActionEvent e) {
        String callerID = "HideSelectedEdgesAction.actionPerformed";
        networkView.getNetwork().beginActivity(callerID);
        GinyUtils.hideSelectedEdges(networkView.getView());
        networkView.redrawGraph(false, false);
        networkView.getNetwork().endActivity(callerID);
    }
}

