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
public class CloneGraphInNewWindowAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public CloneGraphInNewWindowAction(CytoscapeWindow cytoscapeWindow) {
        super("Whole graph");
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed(ActionEvent e) {
        cytoscapeWindow.setInteractivity(false);
        cytoscapeWindow.cloneWindow();
        cytoscapeWindow.setInteractivity(true);
    }
}

