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
public class CloseWindowAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public CloseWindowAction(CytoscapeWindow cytoscapeWindow) {
        super("Close");
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed(ActionEvent e) {
        cytoscapeWindow.saveCalculatorCatalog();
        cytoscapeWindow.getMainFrame().dispose();
    }
}

