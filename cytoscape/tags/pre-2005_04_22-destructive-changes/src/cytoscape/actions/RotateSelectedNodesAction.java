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
        PhoebeNodeControl pnc = new PhoebeNodeControl( networkView.getView() );
    }
}

