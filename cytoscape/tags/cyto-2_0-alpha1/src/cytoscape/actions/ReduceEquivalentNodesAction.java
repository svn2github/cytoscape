//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import cytoscape.view.NetworkView;
import cytoscape.layout.ReduceEquivalentNodes;
//-------------------------------------------------------------------------
public class ReduceEquivalentNodesAction extends AbstractAction {
    NetworkView networkView;
    
    public ReduceEquivalentNodesAction(NetworkView networkView) {
        super("Reduce Equivalent Nodes");
        this.networkView = networkView;
    }
    
   public void actionPerformed(ActionEvent e) {
       new ReduceEquivalentNodes(networkView.getNetwork().getNodeAttributes(),
                                 networkView.getNetwork().getEdgeAttributes(),
                                 networkView.getNetwork().getGraph() );
       /* this call to redrawGraph won't re-layout the graph, but
        * will reapply the visual appearances */
       networkView.redrawGraph(false, true); 
   }
}

