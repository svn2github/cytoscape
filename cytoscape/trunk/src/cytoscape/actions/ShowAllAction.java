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
public class ShowAllAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public ShowAllAction(CytoscapeWindow cytoscapeWindow) {
        super();
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed(ActionEvent e) {
        cytoscapeWindow.getGraph().firePreEvent();
        cytoscapeWindow.getGraphHider().unhideAll();
        cytoscapeWindow.getGraph().firePostEvent();
        
        cytoscapeWindow.getGraphView().fitContent();
        double zoom = 0.9*cytoscapeWindow.getGraphView().getZoom();
        cytoscapeWindow.getGraphView().setZoom(zoom);
        // the apps may have changed dynamically
        cytoscapeWindow.redrawGraph(false, true);
    }
}

