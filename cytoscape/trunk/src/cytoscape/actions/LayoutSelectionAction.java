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
public class LayoutSelectionAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public LayoutSelectionAction (CytoscapeWindow cytoscapeWindow) {
        super("Layout current selection");
        this.cytoscapeWindow = cytoscapeWindow;
    }

  public void actionPerformed (ActionEvent e) {
      cytoscapeWindow.setInteractivity(false);
      
      cytoscapeWindow.getUndoManager().saveRealizerState();
      cytoscapeWindow.getUndoManager().pause();
      cytoscapeWindow.applyLayoutSelection();
      cytoscapeWindow.getUndoManager().resume();
      
      cytoscapeWindow.redrawGraph(false, false);
      cytoscapeWindow.setInteractivity(true);
    }
}

