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
public class UnHideSelectedEdgesAction extends CytoscapeAction  {

    public UnHideSelectedEdgesAction () {
        super ("Un Hide selection");
        setPreferredMenu( "Select.Edges" );
    }

    public void actionPerformed (ActionEvent e) {
      GinyUtils.unHideSelectedEdges( Cytoscape.getCurrentNetworkView() );
    }//action performed
}

