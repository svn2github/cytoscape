//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import cytoscape.view.CyWindow;
//-------------------------------------------------------------------------
/**
 * Uses the UndoManager to undo changes.
 *
 * added by dramage 2002-08-21
 */
public class UndoAction extends AbstractAction {
    CyWindow cyWindow;
    
    public UndoAction(CyWindow cyWindow) {
        super("Undo");
        this.cyWindow = cyWindow;
    }
    
    public void actionPerformed(ActionEvent e) {
      cyWindow.getUndoManager().undo();
      cyWindow.getCyMenus().updateUndoRedoMenuItemStatus();
      cyWindow.redrawGraph(false, true);
    }
}

