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
public class DeselectAllAction extends AbstractAction {
    NetworkView networkView;
    
    public DeselectAllAction (NetworkView networkView) {
        super("Deselect All");
        this.networkView = networkView;
    }

    public void actionPerformed(ActionEvent e) {

      if (networkView.getCytoscapeObj().getConfiguration().isYFiles()) {  	    
        //cytoscapeWindow.deselectAllNodes();
        String callerID = "DeselectAllAction.actionPerformed";
        networkView.getNetwork().beginActivity(callerID);
        
        networkView.getNetwork().getGraph().unselectAll();
	    
        //no new layout, but appearances may need to change
        networkView.redrawGraph(false, true);
        networkView.getNetwork().endActivity(callerID);
        
      }
      else {
        GinyUtils.deselectAllNodes(networkView.getView());
      }
    }
}

