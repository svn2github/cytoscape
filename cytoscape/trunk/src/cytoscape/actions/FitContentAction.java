//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import phoebe.*;

import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class FitContentAction extends AbstractAction {
    NetworkView networkView;
    
    public FitContentAction(NetworkView networkView) {
        super();
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e) {
      // Y-Files check
      if ( networkView.getCytoscapeObj().getConfiguration().isYFiles() ) {
        networkView.getGraphView().fitContent();
        networkView.redrawGraph(false, false);
      } else {
        // GINY
        PGraphView view = networkView.getView();
        view.getCanvas().getCamera().animateViewToCenterBounds( view.getCanvas().getLayer().getGlobalFullBounds(), true, 500l );
      }
    }
}

