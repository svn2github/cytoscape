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
public class SelectAllAction extends AbstractAction  {

    NetworkView networkView;

    public SelectAllAction(NetworkView networkView) {
        super ("Select all nodes and edges");
        this.networkView = networkView;

    }
    

    public void actionPerformed (ActionEvent e) {
	if (networkView.getCytoscapeObj().getConfiguration().isYFiles()) {    
	  //not implemented for y files
	}
	else { // using giny
		
			GinyUtils.selectAllNodes(networkView.getView());
			GinyUtils.selectAllEdges(networkView.getView());
		
	}//!Yfiles
			
		
    }//action performed

}

