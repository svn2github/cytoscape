//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import y.layout.organic.OrganicLayouter;

import cytoscape.CytoscapeWindow;
//-------------------------------------------------------------------------
public class OrganicLayoutAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public OrganicLayoutAction (CytoscapeWindow cytoscapeWindow) {
        super("Organic");
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed (ActionEvent e) {
        OrganicLayouter ol = new OrganicLayouter();
        ol.setActivateDeterministicMode(true);
        ol.setPreferredEdgeLength(80);
        cytoscapeWindow.setLayouter(ol);
    }
}

