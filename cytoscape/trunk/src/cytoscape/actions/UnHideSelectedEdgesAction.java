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
public class UnHideSelectedEdgesAction extends AbstractAction  {

    NetworkView networkView;

    public UnHideSelectedEdgesAction(NetworkView networkView) {
        super ("Un Hide selection");
        this.networkView = networkView;
    }

    public void actionPerformed (ActionEvent e) {
        GinyUtils.unHideSelectedEdges(networkView.getView());
        networkView.redrawGraph(false, true);		
    }//action performed
}

