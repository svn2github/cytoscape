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
public class FitContentAction extends AbstractAction {
    NetworkView networkView;
    
    public FitContentAction(NetworkView networkView) {
        super();
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e) {
        networkView.getGraphView().fitContent();
        networkView.redrawGraph(false, false);
    }
}

