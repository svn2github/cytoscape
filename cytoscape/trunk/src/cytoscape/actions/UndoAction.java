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
/**
 * Uses the UndoManager to undo changes.
 *
 * added by dramage 2002-08-21
 */
public class UndoAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public UndoAction(CytoscapeWindow cytoscapeWindow) {
        super("Undo");
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed(ActionEvent e) {
      cytoscapeWindow.getUndoManager().undo();
      cytoscapeWindow.updateUndoRedoMenuItemStatus();
      cytoscapeWindow.redrawGraph(false, true);
    }
}

