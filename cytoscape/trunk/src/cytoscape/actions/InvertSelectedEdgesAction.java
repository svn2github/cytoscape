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
public class InvertSelectedEdgesAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public InvertSelectedEdgesAction(CytoscapeWindow cytoscapeWindow) {
        super("Invert selection");
        this.cytoscapeWindow = cytoscapeWindow;
    }

    public void actionPerformed (ActionEvent e) {
        cytoscapeWindow.setInteractivity(false);
        GraphUtils.invertSelectedEdges( cytoscapeWindow.getGraph() );
        cytoscapeWindow.redrawGraph(false, false);
        cytoscapeWindow.setInteractivity(true);
    }
}

