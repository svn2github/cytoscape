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
public class ReadOnlyModeAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public ReadOnlyModeAction(CytoscapeWindow cytoscapeWindow) {
        super("Read only Mode");
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed(ActionEvent e) {
        cytoscapeWindow.switchToReadOnlyMode();
    }
}

