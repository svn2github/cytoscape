//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import phoebe.*;

import phoebe.util.*;
import giny.model.*;
import giny.view.*;
import java.util.*;
import edu.umd.cs.piccolo.*;


import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class SelectAllEdgesAction extends AbstractAction  {

    NetworkView networkView;

    public SelectAllEdgesAction(NetworkView networkView) {
        super ("Select all edges");
        this.networkView = networkView;

    }
    

    public void actionPerformed (ActionEvent e) {
	if (networkView.getCytoscapeObj().getConfiguration().isYFiles()) {    
	  //not implemented for y files
	}
	else { // using giny
		
			GinyUtils.selectAllEdges(networkView.getView());
		
	}//!Yfiles
			
		
    }//action performed

}

