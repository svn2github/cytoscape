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
public class HideSelectedAction extends AbstractAction  {
    NetworkView networkView;
    
    public HideSelectedAction(NetworkView networkView) {
        super ();
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e) {
        String callerID = "HideSelectedAction.actionPerformed";
        networkView.getNetwork().beginActivity(callerID);
        GraphUtils.hideSelectedNodes( networkView.getNetwork().getGraph(),
                                      networkView.getGraphHider() );
        GraphUtils.hideSelectedEdges( networkView.getNetwork().getGraph(),
                                      networkView.getGraphHider() );
        networkView.redrawGraph(false, false);
        networkView.getNetwork().endActivity(callerID);;
    }
}

