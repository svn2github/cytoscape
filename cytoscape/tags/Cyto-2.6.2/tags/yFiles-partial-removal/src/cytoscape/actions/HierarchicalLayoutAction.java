//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import y.layout.hierarchic.HierarchicLayouter;

import cytoscape.view.NetworkView;
import cytoscape.layout.HierarchicalLayoutDialog;
//-------------------------------------------------------------------------
public class HierarchicalLayoutAction extends AbstractAction {
    NetworkView networkView;
    HierarchicalLayoutDialog hDialog;
    
    public HierarchicalLayoutAction(NetworkView networkView) {
        super("Hierarchical");
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e) {
        if (hDialog == null)
            hDialog = new HierarchicalLayoutDialog(networkView.getMainFrame());
        hDialog.pack();
        hDialog.setLocationRelativeTo(networkView.getMainFrame());
        hDialog.setVisible(true);
        /* the above method blocks this thread, ensuring that the dialog
         * finishes customizing the layouter before it gets set below */
        networkView.setLayouter( hDialog.getLayouter() );
    }
}

