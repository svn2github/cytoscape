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
public class DeselectAllAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public DeselectAllAction (CytoscapeWindow cytoscapeWindow) {
        super("Deselect All");
        this.cytoscapeWindow = cytoscapeWindow;
    }

    public void actionPerformed(ActionEvent e) {
        cytoscapeWindow.deselectAllNodes();
    }
}

