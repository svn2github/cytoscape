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
public class UnHideSelectedNodesAction extends CytoscapeAction  {

    public UnHideSelectedNodesAction () {
        super ("UnHide selection");
        setPreferredMenu( "Select.Nodes" );
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_H, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK );
    }

    public void actionPerformed (ActionEvent e) {		
        GinyUtils.unHideSelectedNodes( Cytoscape.getCurrentNetworkView() );
        GinyUtils.unHideSelectedEdges( Cytoscape.getCurrentNetworkView() );
     }//action performed
}

