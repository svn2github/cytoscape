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
 * Uses the UndoManager to redo changes.
 *
 * added by dramage 2002-08-21
 */
public class RedoAction extends AbstractAction {
    CyWindow cyWindow;
    
    public RedoAction(CyWindow cyWindow) {
        super("Redo");
        this.cyWindow = cyWindow;
    }

    public void actionPerformed(ActionEvent e) {
      cyWindow.getUndoManager().redo();
      cyWindow.getCyMenus().updateUndoRedoMenuItemStatus();
      cyWindow.redrawGraph(false, true);
    }
}

