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
public class DeSelectAllEdgesAction extends CytoscapeAction  {

    public DeSelectAllEdgesAction () {
        super ("Deselect all edges");
        setPreferredMenu( "Select.Edges" );
    }

    public void actionPerformed (ActionEvent e) {
        GinyUtils.deselectAllEdges( Cytoscape.getCurrentNetworkView() );
    }//action performed
}

