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
public class MenuFilterAction extends MainFilterDialogAction {
    CytoscapeWindow cytoscapeWindow;
    
    public MenuFilterAction(CytoscapeWindow cytoscapeWindow) {
        super(cytoscapeWindow, "Using filters..."); 
    }
}
