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
public class HideSelectedAction extends AbstractAction  {
    CytoscapeWindow cytoscapeWindow;
    
    public HideSelectedAction (CytoscapeWindow cytoscapeWindow) {
        super ();
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed (ActionEvent e) {
        cytoscapeWindow.setInteractivity(false);
        GraphUtils.hideSelectedNodes( cytoscapeWindow.getGraph(),
                                      cytoscapeWindow.getGraphHider() );
        GraphUtils.hideSelectedEdges( cytoscapeWindow.getGraph(),
                                      cytoscapeWindow.getGraphHider() );
        cytoscapeWindow.redrawGraph(false, false);
        cytoscapeWindow.setInteractivity(true);
    }
}

