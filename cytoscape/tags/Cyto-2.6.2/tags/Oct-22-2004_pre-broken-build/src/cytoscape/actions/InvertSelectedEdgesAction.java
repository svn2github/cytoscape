//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import cytoscape.CyNetwork;
import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape;
//-------------------------------------------------------------------------
public class InvertSelectedEdgesAction extends CytoscapeAction {
    
    public InvertSelectedEdgesAction () {
        super("Invert selection");
        setPreferredMenu( "Select.Edges" );
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_I, ActionEvent.ALT_MASK) ;
    }

    public void actionPerformed (ActionEvent e) {
	CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
	int [] flaggedEdgeIndices = cyNetwork.getFlaggedEdgeIndicesArray();
	cyNetwork.flagAllEdges();
	cyNetwork.setFlaggedEdges(flaggedEdgeIndices,false);
    }
}

