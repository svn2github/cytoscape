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
import cytoscape.CyNetwork;
import cytoscape.util.CytoscapeAction;
//-------------------------------------------------------------------------
public class InvertSelectedNodesAction extends CytoscapeAction {
    
    public InvertSelectedNodesAction () {
        super("Invert selection");
        setPreferredMenu( "Select.Nodes" );
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_I, ActionEvent.CTRL_MASK );
    }

    public void actionPerformed (ActionEvent e) {
	CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
	int [] flaggedNodeIndices = cyNetwork.getFlaggedNodeIndicesArray();
	cyNetwork.flagAllNodes();
	cyNetwork.setFlaggedNodes(flaggedNodeIndices,false);
    }
}

