//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape;
//-------------------------------------------------------------------------
public class ShowAllAction extends CytoscapeAction {
       
    public ShowAllAction () {
        super();
    }
    
    public void actionPerformed(ActionEvent e) {
      GinyUtils.unHideAll( Cytoscape.getCurrentNetworkView() );
        //networkView.redrawGraph(false, true);
    }
}

