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
public class UnHideSelectedAction extends AbstractAction  {

    NetworkView networkView;

    public UnHideSelectedAction(NetworkView networkView) {
        super ("Un Hide selection");
        this.networkView = networkView;

    }
    

    public void actionPerformed (ActionEvent e) {
	if (networkView.getCytoscapeObj().getConfiguration().isYFiles()) {    
	  //not implemented for y files
	}
	else { // using giny
		
			GinyUtils.unHideSelectedNodes(networkView.getView());
			GinyUtils.unHideSelectedEdges(networkView.getView());
		
	}//!Yfiles
			
		
    }//action performed

}

