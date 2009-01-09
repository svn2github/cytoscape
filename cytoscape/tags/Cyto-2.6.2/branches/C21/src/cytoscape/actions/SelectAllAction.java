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
public class SelectAllAction extends CytoscapeAction  {

    public SelectAllAction () {
        super ("Select all nodes and edges");
        setPreferredMenu( "Select" );
    }

    public void actionPerformed (ActionEvent e) {		
        GinyUtils.selectAllNodes( Cytoscape.getCurrentNetworkView() );
        GinyUtils.selectAllEdges( Cytoscape.getCurrentNetworkView() );
    }//action performed
}

