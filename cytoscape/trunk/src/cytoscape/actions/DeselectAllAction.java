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
public class DeselectAllAction extends CytoscapeAction {
       
    public DeselectAllAction () {
        super("Deselect All Nodes and Edges");
        setPreferredMenu( "Select" );
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_A, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK|ActionEvent.ALT_MASK) ;
    }

    public void actionPerformed(ActionEvent e) {
      //GinyUtils.deselectAllNodes( Cytoscape.getCurrentNetworkView() );
      //GinyUtils.deselectAllEdges( Cytoscape.getCurrentNetworkView() );
      Cytoscape.getCurrentNetwork().unFlagAllEdges();
      Cytoscape.getCurrentNetwork().unFlagAllNodes();
    }
}

