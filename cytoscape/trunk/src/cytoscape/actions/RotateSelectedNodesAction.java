//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import cytoscape.view.NetworkView;
import cytoscape.dialogs.RotateSelectionDialog;
import cytoscape.dialogs.PhoebeNodeControl;
//-------------------------------------------------------------------------
/**
 * Rotates the given selection by the specified amount.
 *
 * added by dramage 2002-08-20
 */
public class RotateSelectedNodesAction extends AbstractAction {
    NetworkView networkView;
    
    public RotateSelectedNodesAction (NetworkView networkView) {
        super("Rotate Selected Nodes");
        this.networkView = networkView;
    }

    public void actionPerformed (ActionEvent e) {
      // Y-Files check
      if ( networkView.getCytoscapeObj().getConfiguration().isYFiles() ) {
        String callerID = "RotateSelectedNodesAction.actionPerformed";
        networkView.getNetwork().beginActivity(callerID);
        RotateSelectionDialog d = new RotateSelectionDialog(networkView);
        /* the above dialog is a modal dialog, so it blocks this thread
         * until it finishes -AM 09-16-03 */
        networkView.getNetwork().endActivity(callerID);
      
      } else {

        PhoebeNodeControl pnc = new PhoebeNodeControl( networkView.getView() );

      }
    }

}

