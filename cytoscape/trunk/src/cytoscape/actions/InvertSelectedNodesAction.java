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
//-------------------------------------------------------------------------
public class InvertSelectedNodesAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public InvertSelectedNodesAction(CytoscapeWindow cytoscapeWindow) {
        super("Invert selection");
        this.cytoscapeWindow = cytoscapeWindow;
    }

    public void actionPerformed (ActionEvent e) {
        cytoscapeWindow.setInteractivity(false);
        GraphUtils.invertSelectedNodes( cytoscapeWindow.getGraph() );
        cytoscapeWindow.redrawGraph(false, false);
        cytoscapeWindow.setInteractivity(true);
    }
}

