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
 * Uses the UndoManager to redo changes.
 *
 * added by dramage 2002-08-21
 */
public class RedoAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public RedoAction(CytoscapeWindow cytoscapeWindow) {
        super("Redo");
        this.cytoscapeWindow = cytoscapeWindow;
    }

    public void actionPerformed(ActionEvent e) {
      cytoscapeWindow.getUndoManager().redo();
      cytoscapeWindow.updateUndoRedoMenuItemStatus();
      cytoscapeWindow.redrawGraph();
    }
}

