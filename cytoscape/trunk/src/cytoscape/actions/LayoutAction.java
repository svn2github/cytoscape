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
public class LayoutAction extends AbstractAction   {
    CytoscapeWindow cytoscapeWindow;
    
    public LayoutAction (CytoscapeWindow cytoscapeWindow) {
        super("Layout whole graph");
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed (ActionEvent e) {
        /* this forces a layout, but doesn't reapply the appearances */
        cytoscapeWindow.redrawGraph(true, false);
    }
}

