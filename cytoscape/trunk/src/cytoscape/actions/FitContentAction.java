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
public class FitContentAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public FitContentAction(CytoscapeWindow cytoscapeWindow) {
        super();
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed(ActionEvent e) {
        cytoscapeWindow.getGraphView().fitContent();
        cytoscapeWindow.redrawGraph(false, false);
    }
}

