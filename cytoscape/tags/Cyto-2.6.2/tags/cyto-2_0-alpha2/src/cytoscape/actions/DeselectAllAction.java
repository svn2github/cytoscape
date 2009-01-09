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
        super("Deselect All Nodes and Edges");
        this.networkView = networkView;
    }

    public void actionPerformed(ActionEvent e) {
        GinyUtils.deselectAllNodes(networkView.getView());
	GinyUtils.deselectAllEdges(networkView.getView());
    }
}

