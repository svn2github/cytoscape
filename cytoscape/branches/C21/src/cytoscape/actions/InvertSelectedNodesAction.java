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
import cytoscape.view.CyNetworkView;
import cytoscape.util.CytoscapeAction;
//-------------------------------------------------------------------------
public class InvertSelectedNodesAction extends CytoscapeAction {
    
    public InvertSelectedNodesAction () {
        super("Invert selection");
        setPreferredMenu( "Select.Nodes" );
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_V, ActionEvent.CTRL_MASK );
    }

    public void actionPerformed (ActionEvent e) {
        String callerID = "InvertSelectedNodesAction.actionPerformed";
        GinyUtils.invertSelectedNodes( Cytoscape.getCurrentNetworkView() );
    }
}

