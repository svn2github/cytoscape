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
public class ExitAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public ExitAction(CytoscapeWindow cytoscapeWindow) {
        super("Exit");
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed (ActionEvent e) {
        cytoscapeWindow.saveCalculatorCatalog();
        cytoscapeWindow.getParentApp().exit(0);
    }
}

