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
public class DeSelectAllEdgesAction extends AbstractAction  {

    NetworkView networkView;

    public DeSelectAllEdgesAction(NetworkView networkView) {
        super ("Deselect all edges");
        this.networkView = networkView;
    }

    public void actionPerformed (ActionEvent e) {
        GinyUtils.deselectAllEdges(networkView.getView());
    }//action performed
}

