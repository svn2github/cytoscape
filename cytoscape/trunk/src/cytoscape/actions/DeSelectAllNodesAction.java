//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import giny.model.*;
import giny.view.*;
import java.util.*;



import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class DeSelectAllNodesAction extends AbstractAction  {

    NetworkView networkView;

    public DeSelectAllNodesAction(NetworkView networkView) {
        super ("Deselect all nodes");
        this.networkView = networkView;

    }
    

    public void actionPerformed (ActionEvent e) {
	if (networkView.getCytoscapeObj().getConfiguration().isYFiles()) {    
	  //not implemented for y files
	}
	else { // using giny
		
			GinyUtils.deselectAllNodes(networkView.getView());
			//GinyUtils.deselectAllEdges(networkView.getView());
		
	}//!Yfiles
			
		
    }//action performed

}

