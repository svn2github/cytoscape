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
public class DeSelectAllNodesAction extends AbstractAction  {

    NetworkView networkView;

    public DeSelectAllNodesAction(NetworkView networkView) {
        super ("Deselect all nodes");
        this.networkView = networkView;
    }

    public void actionPerformed (ActionEvent e) {
        GinyUtils.deselectAllNodes(networkView.getView());
    }
}

