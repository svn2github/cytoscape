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
public class SelectAllEdgesAction extends AbstractAction  {

    NetworkView networkView;

    public SelectAllEdgesAction(NetworkView networkView) {
        super ("Select all edges");
        this.networkView = networkView;
    }

    public void actionPerformed (ActionEvent e) {		
        GinyUtils.selectAllEdges(networkView.getView());
        networkView.redrawGraph(false, true);
    }//action performed
}

