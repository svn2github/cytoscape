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
import cytoscape.filters.dialogs.MainFilterDialog;
//-------------------------------------------------------------------------
public class MainFilterDialogAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public MainFilterDialogAction(CytoscapeWindow cytoscapeWindow) {
        super();
        this.cytoscapeWindow = cytoscapeWindow;
    }
    public MainFilterDialogAction(CytoscapeWindow cytoscapeWindow, String title) {
        super(title);
        this.cytoscapeWindow = cytoscapeWindow;
    }

   public void actionPerformed(ActionEvent e) {
       String[] interactionTypes = cytoscapeWindow.getInteractionTypes();
       new MainFilterDialog(cytoscapeWindow,
                            cytoscapeWindow.getMainFrame(),
                            cytoscapeWindow.getGraph(),
                            cytoscapeWindow.getNodeAttributes(),
                            cytoscapeWindow.getEdgeAttributes(),
                            cytoscapeWindow.getExpressionData(),
                            cytoscapeWindow.getGraphHider(),
                            interactionTypes);
   }
}

