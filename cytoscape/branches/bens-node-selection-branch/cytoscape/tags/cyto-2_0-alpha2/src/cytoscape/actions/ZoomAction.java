//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import giny.view.GraphView;

import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class ZoomAction extends AbstractAction {
    NetworkView networkView;
    double factor;
    
    public ZoomAction(NetworkView networkView, double factor) {
        super ();
        this.networkView = networkView;
        this.factor = factor;
    }
    
  public void zoom () {
    networkView.getView().setZoom( factor );
  }

    public void actionPerformed (ActionEvent e) {
      networkView.getView().setZoom( factor );
    }
}

