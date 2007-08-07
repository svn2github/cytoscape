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
import cytoscape.filters.dialogs.EdgeTypeDialogIndep;
//-------------------------------------------------------------------------
public class EdgeTypeDialogAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public EdgeTypeDialogAction(CytoscapeWindow cytoscapeWindow) {
        super("Edges by Interaction Type");
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed(ActionEvent e) {
        new EdgeTypeDialogIndep(cytoscapeWindow,
                                cytoscapeWindow.getMainFrame(),
                                cytoscapeWindow.getGraph(),
                                cytoscapeWindow.getEdgeAttributes(),
                                cytoscapeWindow.getGraphHider(),
                                cytoscapeWindow.getInteractionTypes());
   }
}

