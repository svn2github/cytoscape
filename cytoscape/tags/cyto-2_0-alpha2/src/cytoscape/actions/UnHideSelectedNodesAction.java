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
public class UnHideSelectedNodesAction extends AbstractAction  {

    NetworkView networkView;

    public UnHideSelectedNodesAction(NetworkView networkView) {
        super ("Un Hide selection");
        this.networkView = networkView;
    }

    public void actionPerformed (ActionEvent e) {		
        GinyUtils.unHideSelectedNodes(networkView.getView());
        GinyUtils.unHideSelectedEdges(networkView.getView());
        networkView.redrawGraph(false, true);
    }//action performed
}

