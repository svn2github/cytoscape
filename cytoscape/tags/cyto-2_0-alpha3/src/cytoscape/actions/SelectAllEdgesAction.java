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
public class SelectAllEdgesAction extends CytoscapeAction  {

  public SelectAllEdgesAction () {
        super ("Select all edges");
        setPreferredMenu( "Select.Edges" );
    }

    public void actionPerformed (ActionEvent e) {		
      GinyUtils.selectAllEdges( Cytoscape.getCurrentNetworkView() );
    }//action performed
}

