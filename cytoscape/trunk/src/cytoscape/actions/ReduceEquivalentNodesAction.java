//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import cytoscape.CytoscapeWindow;
import cytoscape.layout.ReduceEquivalentNodes;
//-------------------------------------------------------------------------
public class ReduceEquivalentNodesAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public ReduceEquivalentNodesAction(CytoscapeWindow cytoscapeWindow) {
        super("Reduce Equivalent Nodes");
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
   public void actionPerformed(ActionEvent e) {
       new ReduceEquivalentNodes(cytoscapeWindow.getNodeAttributes(),
                                 cytoscapeWindow.getEdgeAttributes(),
                                 cytoscapeWindow.getGraph() );
       /* this call to redrawGraph won't re-layout the graph, but
        * will reapply the visual appearances */
       cytoscapeWindow.redrawGraph(false, true); 
   }
}

