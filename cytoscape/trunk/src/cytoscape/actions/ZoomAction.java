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
public class ZoomAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    double factor;
    
    public ZoomAction(CytoscapeWindow cytoscapeWindow, double factor) {
        super ();
        this.cytoscapeWindow = cytoscapeWindow;
        this.factor = factor;
    }
    
    public void actionPerformed (ActionEvent e) {
        double newZoom = factor*cytoscapeWindow.getGraphView().getZoom();
        cytoscapeWindow.getGraphView().setZoom(newZoom);
        cytoscapeWindow.redrawGraph(false,false);
    }
}

