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
public class HideSelectedEdgesAction extends AbstractAction   {
    CytoscapeWindow cytoscapeWindow;
    
    public HideSelectedEdgesAction (CytoscapeWindow cytoscapeWindow) {
        super("Hide selection");
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed (ActionEvent e) {
        cytoscapeWindow.setInteractivity(false);
        GraphUtils.hideSelectedEdges( cytoscapeWindow.getGraph(),
                                      cytoscapeWindow.getGraphHider() );
        cytoscapeWindow.redrawGraph(false, false);
        cytoscapeWindow.setInteractivity(true);
    }
}

