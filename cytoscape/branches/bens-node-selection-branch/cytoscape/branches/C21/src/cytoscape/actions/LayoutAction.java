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

//-------------------------------------------------------------------------
public class LayoutAction extends AbstractAction   {
    NetworkView networkView;
    
    public LayoutAction(NetworkView networkView) {
        super("Layout whole graph");
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e) {
        networkView.applyLayout(networkView.getView());
        networkView.redrawGraph(false, false);
    }
}

