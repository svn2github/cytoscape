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
public class ShowAllAction extends AbstractAction {
    NetworkView networkView;
    
    public ShowAllAction(NetworkView networkView) {
        super();
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e) {
        String callerID = "ShowAllAction.actionPerformed";
        networkView.getNetwork().beginActivity(callerID);
        networkView.getGraphHider().unhideAll();
        
        networkView.getGraphView().fitContent();
        double zoom = 0.9*networkView.getGraphView().getZoom();
        networkView.getGraphView().setZoom(zoom);
        // the apps may have changed dynamically
        networkView.redrawGraph(false, true);
        networkView.getNetwork().endActivity(callerID);
    }
}

