//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
//-------------------------------------------------------------------------
public class HideSelectedEdgesAction extends CytoscapeAction {
    
    
    public HideSelectedEdgesAction() {
        super("Hide selection");
        setPreferredMenu( "Select.Edges" );
    }

    public void actionPerformed (ActionEvent e) {
       
      GinyUtils.hideSelectedEdges( Cytoscape.getCurrentNetworkView() );
       
    }
}

