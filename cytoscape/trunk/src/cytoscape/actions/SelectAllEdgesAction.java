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
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_A, ActionEvent.ALT_MASK) ;
    }

    public void actionPerformed (ActionEvent e) {		
      //GinyUtils.selectAllEdges( Cytoscape.getCurrentNetworkView() );
      Cytoscape.getCurrentNetwork().flagAllEdges();
    }//action performed
}

