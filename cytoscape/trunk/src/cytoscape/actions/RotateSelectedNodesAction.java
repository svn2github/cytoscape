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
import cytoscape.dialogs.RotateSelectionDialog;
//-------------------------------------------------------------------------
/**
 * Rotates the given selection by the specified amount.
 *
 * added by dramage 2002-08-20
 */
public class RotateSelectedNodesAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public RotateSelectedNodesAction (CytoscapeWindow cytoscapeWindow) {
        super("Rotate Selected Nodes");
        this.cytoscapeWindow = cytoscapeWindow;
    }

    public void actionPerformed (ActionEvent e) {
        cytoscapeWindow.getUndoManager().saveRealizerState();
        cytoscapeWindow.getUndoManager().pause();
        RotateSelectionDialog d =
            new RotateSelectionDialog(cytoscapeWindow.getMainFrame(),
                                      cytoscapeWindow,
                                      cytoscapeWindow.getGraph());
        /* Once again, isn't this going to cause problems since the above
         * method doesn't block, thus this method continues before it should?
         */
        cytoscapeWindow.getUndoManager().resume();
    }
}

