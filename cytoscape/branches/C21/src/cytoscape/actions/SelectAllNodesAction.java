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
public class SelectAllNodesAction extends AbstractAction  {

    NetworkView networkView;

    public SelectAllNodesAction(NetworkView networkView) {
        super ("Select all nodes");
        this.networkView = networkView;
    }

    public void actionPerformed (ActionEvent e) {		
        GinyUtils.selectAllNodes(networkView.getView());
        networkView.redrawGraph(false, true);
    }//action performed
}

