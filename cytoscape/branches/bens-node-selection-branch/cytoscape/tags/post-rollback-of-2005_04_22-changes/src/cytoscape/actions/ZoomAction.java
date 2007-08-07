//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import cytoscape.view.CyNetworkView;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;

public class ZoomAction extends CytoscapeAction {
  
    double factor;
    
    public ZoomAction( double factor) {
        super ();
        this.factor = factor;
    }
    
  public void zoom () {
    Cytoscape.getCurrentNetworkView().setZoom( factor );
  }

    public void actionPerformed (ActionEvent e) {
      Cytoscape.getCurrentNetworkView().setZoom( factor );
    }
}

