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
public class InvertSelectedEdgesAction extends CytoscapeAction {
    
    public InvertSelectedEdgesAction () {
        super("Invert selection");
        setPreferredMenu( "Select.Edges" );
    }

    public void actionPerformed (ActionEvent e) {
      GinyUtils.invertSelectedEdges( Cytoscape.getCurrentNetworkView() );
    }
}

