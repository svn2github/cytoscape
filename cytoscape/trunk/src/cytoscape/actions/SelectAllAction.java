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
public class SelectAllAction extends AbstractAction  {

    NetworkView networkView;

    public SelectAllAction(NetworkView networkView) {
        super ("Select all nodes and edges");
        this.networkView = networkView;
    }

    public void actionPerformed (ActionEvent e) {		
        GinyUtils.selectAllNodes(networkView.getView());
        GinyUtils.selectAllEdges(networkView.getView());
        networkView.redrawGraph(false, true);
    }//action performed
}

