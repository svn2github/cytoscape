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
public class EditModeAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public EditModeAction(CytoscapeWindow cytoscapeWindow) {
        super("Edit Mode for Nodes and Edges");
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed(ActionEvent e) {
        cytoscapeWindow.switchToEditMode();
    }
}

