//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import cytoscape.data.Semantics;
import cytoscape.view.NetworkView;
import cytoscape.filters.dialogs.MainFilterDialog;
//-------------------------------------------------------------------------
public class MainFilterDialogAction extends AbstractAction {
    NetworkView networkView;
    
    public MainFilterDialogAction(NetworkView networkView) {
        super();
        this.networkView = networkView;
    }
    public MainFilterDialogAction(NetworkView networkView, String title) {
        super(title);
        this.networkView = networkView;
    }

   public void actionPerformed(ActionEvent e) {
       //should refactor the filters constructor, but will do this later
       new MainFilterDialog(networkView,
                            networkView.getMainFrame(),
                            networkView.getNetwork().getGraph(),
                            networkView.getNetwork().getNodeAttributes(),
                            networkView.getNetwork().getEdgeAttributes(),
                            networkView.getNetwork().getExpressionData(),
                            networkView.getGraphHider(),
                            Semantics.getInteractionTypes(networkView.getNetwork()) );
   }
}

