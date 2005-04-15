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
public class UnHideSelectedAction extends AbstractAction  {

    NetworkView networkView;

    public UnHideSelectedAction(NetworkView networkView) {
        super ("Un Hide selection");
        this.networkView = networkView;
    }

    public void actionPerformed (ActionEvent e) {		
      //GinyUtils.unHideSelectedNodes(networkView.getView());
      //GinyUtils.unHideSelectedEdges(networkView.getView());
      //networkView.redrawGraph(false, true);	
      GinyUtils.unHideAll( cytoscape.Cytoscape.getCurrentNetworkView() );
    }//action performed
}

